# SPDX-License-Identifier: MPL-2.0

defmodule EtmaHandler.Repo do
  @moduledoc """
  Persistence layer over CubDB.

  Owns three logical tables, distinguished by the first element of every
  key (always a 2-tuple `{table_tag, sub_key}`):

  * `{:submission, [course, presentation, tma, oucu]}` — the latest record
    for a submission.
  * `{:audit, [course, presentation, tma, oucu, seq]}` — append-only log
    of every mutation: `event`, `prev`, `value`, `at`.
  * `{:snapshot, [course, presentation, tma, oucu, seq]}` — point-in-time
    snapshots for the time-machine restore.

  `submission_id` is the 4-tuple `{course, presentation, tma, oucu}`.

  ## Why lists for sub-keys

  Erlang term order compares tuples by **arity first**, so `{c}` and
  `{c, p, t, o}` do not prefix-sort. Lists compare element-by-element with
  the shorter list winning on tie, so `[c] < [c, p, t, o]`. That gives us
  clean prefix-range scans for free.

  ## Why a `seq` instead of a wall-clock timestamp

  `seq` is `:erlang.unique_integer([:monotonic, :positive])`. Monotonic,
  collision-free, integer-comparable, so it works as a clean key suffix
  and as a public time-machine token. The human-readable wall-clock value
  lives inside the record as `:updated_at` / `:at`.
  """

  alias CubDB.Tx

  @cubdb __MODULE__.CubDB

  @type submission_id :: {course :: String.t(), presentation :: String.t(),
                          tma :: String.t(), oucu :: String.t()}
  @type submission :: map()
  @type seq :: pos_integer()

  # --- Supervision -------------------------------------------------------

  @doc false
  def child_spec(opts) do
    data_dir = Keyword.fetch!(opts, :data_dir)
    File.mkdir_p!(data_dir)

    %{
      id: __MODULE__,
      start:
        {CubDB, :start_link,
         [[data_dir: data_dir, name: @cubdb, auto_compact: true]]},
      type: :worker,
      restart: :permanent,
      shutdown: 5_000
    }
  end

  @doc """
  Underlying CubDB process name. Exposed for tests and tooling; application
  code should use the functions in this module.
  """
  @spec cubdb() :: atom()
  def cubdb, do: @cubdb

  @doc """
  Block until in-flight writes are flushed to disk.
  """
  @spec sync() :: :ok
  def sync, do: CubDB.file_sync(@cubdb)

  # --- Submission CRUD ---------------------------------------------------

  @doc """
  Insert-or-update a submission. Atomically records an `:put` audit entry
  and a snapshot in the same transaction.
  """
  @spec put_submission(submission_id(), submission()) :: :ok
  def put_submission(id, value) when is_map(value) do
    now = DateTime.utc_now()
    seq = next_seq()
    parts = id_parts(id)

    record =
      value
      |> Map.put(:id, id)
      |> Map.put(:updated_at, now)

    CubDB.transaction(@cubdb, fn tx ->
      prev = Tx.get(tx, submission_key(parts))

      tx
      |> Tx.put(submission_key(parts), record)
      |> Tx.put(audit_key(parts, seq),
                %{event: :put, prev: prev, value: record, at: now})
      |> Tx.put(snapshot_key(parts, seq), record)
      |> commit(:ok)
    end)
  end

  @doc """
  Fetch the live submission record. Returns `nil` if absent.
  """
  @spec get_submission(submission_id()) :: submission() | nil
  def get_submission(id) do
    CubDB.get(@cubdb, submission_key(id_parts(id)))
  end

  @doc """
  List submissions, optionally filtered by `:course` and/or `:presentation`.
  Returns records in `submission_id` order.
  """
  @spec list_submissions(keyword()) :: [submission()]
  def list_submissions(opts \\ []) do
    {min, max} = submission_range(opts)

    @cubdb
    |> CubDB.select(
      min_key: min,
      max_key: max,
      min_key_inclusive: false,
      max_key_inclusive: false
    )
    |> Enum.map(fn {_k, v} -> v end)
  end

  @doc """
  Delete the live record. Audit log and snapshots are retained, so
  `restore/2` can still recover prior state.
  """
  @spec delete_submission(submission_id()) :: :ok | {:error, :not_found}
  def delete_submission(id) do
    parts = id_parts(id)
    now = DateTime.utc_now()
    seq = next_seq()

    CubDB.transaction(@cubdb, fn tx ->
      case Tx.get(tx, submission_key(parts)) do
        nil ->
          {:cancel, {:error, :not_found}}

        prev ->
          tx
          |> Tx.delete(submission_key(parts))
          |> Tx.put(audit_key(parts, seq),
                    %{event: :delete, prev: prev, value: nil, at: now})
          |> commit(:ok)
      end
    end)
  end

  # --- Time machine ------------------------------------------------------

  @doc """
  Take an ad-hoc snapshot of the current submission state. Returns a `seq`
  suitable for passing to `restore/2`.
  """
  @spec snapshot(submission_id()) :: {:ok, seq()} | {:error, :not_found}
  def snapshot(id) do
    parts = id_parts(id)
    seq = next_seq()

    CubDB.transaction(@cubdb, fn tx ->
      case Tx.get(tx, submission_key(parts)) do
        nil ->
          {:cancel, {:error, :not_found}}

        record ->
          tx
          |> Tx.put(snapshot_key(parts, seq), record)
          |> commit({:ok, seq})
      end
    end)
  end

  @doc """
  Full audit + snapshot history for a submission, in chronological order.
  Each entry is `{seq, kind, payload}` with `kind` in `:audit | :snapshot`.
  """
  @spec history(submission_id()) :: [{seq(), :audit | :snapshot, map()}]
  def history(id) do
    parts = id_parts(id)

    audits =
      @cubdb
      |> CubDB.select(
        min_key: {:audit, parts},
        max_key: {:audit, parts ++ [<<0xFF>>]},
        min_key_inclusive: false,
        max_key_inclusive: false
      )
      |> Enum.map(fn {{:audit, sk}, v} -> {List.last(sk), :audit, v} end)

    snapshots =
      @cubdb
      |> CubDB.select(
        min_key: {:snapshot, parts},
        max_key: {:snapshot, parts ++ [<<0xFF>>]},
        min_key_inclusive: false,
        max_key_inclusive: false
      )
      |> Enum.map(fn {{:snapshot, sk}, v} -> {List.last(sk), :snapshot, v} end)

    Enum.sort_by(audits ++ snapshots, fn {seq, _, _} -> seq end)
  end

  @doc """
  Restore the submission to the state captured by the snapshot at-or-before
  `seq`. The restore itself is recorded as a new `:put` (with audit +
  snapshot).
  """
  @spec restore(submission_id(), seq()) :: :ok | {:error, :no_snapshot}
  def restore(id, seq) do
    case snapshot_at_or_before(id, seq) do
      nil ->
        {:error, :no_snapshot}

      record ->
        put_submission(id, Map.drop(record, [:id, :updated_at]))
    end
  end

  # --- Internals ---------------------------------------------------------

  defp id_parts({c, p, t, o}), do: [c, p, t, o]

  defp submission_key(parts), do: {:submission, parts}
  defp audit_key(parts, seq), do: {:audit, parts ++ [seq]}
  defp snapshot_key(parts, seq), do: {:snapshot, parts ++ [seq]}

  # Range bounds rely on Erlang term order: list < binary, so `<<0xFF>>`
  # as a sentinel element sorts after any real string in the same slot.
  defp submission_range(opts) do
    course = Keyword.get(opts, :course)
    presentation = Keyword.get(opts, :presentation)

    sub_min =
      case {course, presentation} do
        {nil, _} -> []
        {c, nil} -> [c]
        {c, p} -> [c, p]
      end

    sub_max =
      case {course, presentation} do
        {nil, _} -> [<<0xFF>>]
        {c, nil} -> [c, <<0xFF>>]
        {c, p} -> [c, p, <<0xFF>>]
      end

    {{:submission, sub_min}, {:submission, sub_max}}
  end

  defp snapshot_at_or_before(id, seq) do
    parts = id_parts(id)

    @cubdb
    |> CubDB.select(
      min_key: {:snapshot, parts},
      max_key: {:snapshot, parts ++ [seq]},
      min_key_inclusive: false,
      max_key_inclusive: true,
      reverse: true
    )
    |> Enum.take(1)
    |> case do
      [{_k, record}] -> record
      [] -> nil
    end
  end

  defp next_seq, do: :erlang.unique_integer([:monotonic, :positive])

  defp commit(tx, result), do: {:commit, tx, result}
end
