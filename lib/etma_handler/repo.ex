defmodule EtmaHandler.Repo do
  @moduledoc """
  The Crash-Proof Data Layer.

  Uses CubDB (Append-Only B-Tree) to ensure no data is ever lost, even on power cut.
  Pure Elixir implementation means this runs everywhere the BEAM runs:
  - Linux (x86_64, ARM64, RISC-V)
  - macOS
  - Windows
  - Minix (experimental)

  ## The "Time Machine" Feature

  Every save creates a timestamped snapshot. You can:
  - Slide back to any previous state
  - Recover from crashes
  - Audit changes over time

  ## Example

      # Save marking data (creates a snapshot)
      Repo.save_snapshot("M150-rg8274-TMA01", %{marks: [10, 20], comments: "Good work"})

      # Retrieve history
      Repo.get_history("M150-rg8274-TMA01")
      # => [{~U[2025-11-27 10:00:00Z], %{marks: [10, 20], ...}}, ...]

      # Restore a specific point in time
      Repo.get_snapshot_at("M150-rg8274-TMA01", ~U[2025-11-27 09:30:00Z])
  """

  use GenServer
  require Logger

  @db_name :etma_db

  # --- Client API ---

  def start_link(opts \\ []) do
    GenServer.start_link(__MODULE__, opts, name: __MODULE__)
  end

  @doc """
  Saves a snapshot of the marking data.

  Creates a timestamped entry, preserving the full history.
  Returns `{:ok, timestamp}` on success.
  """
  @spec save_snapshot(String.t(), map()) :: {:ok, DateTime.t()} | {:error, term()}
  def save_snapshot(submission_id, data) do
    GenServer.call(__MODULE__, {:save_snapshot, submission_id, data})
  end

  @doc """
  Retrieves the full history of a submission.

  Returns a list of `{timestamp, data}` tuples, newest first.
  """
  @spec get_history(String.t()) :: [{DateTime.t(), map()}]
  def get_history(submission_id) do
    GenServer.call(__MODULE__, {:get_history, submission_id})
  end

  @doc """
  Gets the most recent snapshot for a submission.
  """
  @spec get_latest(String.t()) :: {:ok, map()} | {:error, :not_found}
  def get_latest(submission_id) do
    GenServer.call(__MODULE__, {:get_latest, submission_id})
  end

  @doc """
  Retrieves the snapshot closest to (but not after) the given timestamp.
  """
  @spec get_snapshot_at(String.t(), DateTime.t()) :: {:ok, map()} | {:error, :not_found}
  def get_snapshot_at(submission_id, timestamp) do
    GenServer.call(__MODULE__, {:get_snapshot_at, submission_id, timestamp})
  end

  @doc """
  Simple key-value get.
  """
  @spec get(term()) :: term() | nil
  def get(key) do
    GenServer.call(__MODULE__, {:get, key})
  end

  @doc """
  Simple key-value put.
  """
  @spec put(term(), term()) :: :ok
  def put(key, value) do
    GenServer.call(__MODULE__, {:put, key, value})
  end

  @doc """
  Delete a key.
  """
  @spec delete(term()) :: :ok
  def delete(key) do
    GenServer.call(__MODULE__, {:delete, key})
  end

  @doc """
  List all submissions (for the dashboard).
  """
  @spec list_submissions() :: [map()]
  def list_submissions do
    GenServer.call(__MODULE__, :list_submissions)
  end

  @doc """
  List submissions for a specific course.
  """
  @spec list_submissions(String.t()) :: [map()]
  def list_submissions(course_code) do
    GenServer.call(__MODULE__, {:list_submissions, course_code})
  end

  # --- Server Implementation ---

  @impl true
  def init(opts) do
    data_dir = Keyword.get(opts, :data_dir, get_data_dir())

    # Ensure data directory exists
    File.mkdir_p!(data_dir)

    db_path = Path.join(data_dir, "etma.cub")

    case CubDB.start_link(data_dir: db_path, name: @db_name, auto_compact: true) do
      {:ok, _pid} ->
        Logger.info("CubDB started at #{db_path}")
        {:ok, %{db_path: db_path}}

      {:error, {:already_started, _pid}} ->
        Logger.info("CubDB already running")
        {:ok, %{db_path: db_path}}

      {:error, reason} ->
        Logger.error("Failed to start CubDB: #{inspect(reason)}")
        {:stop, reason}
    end
  end

  @impl true
  def handle_call({:save_snapshot, submission_id, data}, _from, state) do
    timestamp = DateTime.utc_now()
    key = {:snapshot, submission_id, timestamp}

    # Add metadata to the snapshot
    snapshot_data = Map.merge(data, %{
      __saved_at__: timestamp,
      __submission_id__: submission_id
    })

    result = CubDB.put(@db_name, key, snapshot_data)

    # Also update the "latest" pointer for fast access
    CubDB.put(@db_name, {:latest, submission_id}, snapshot_data)

    case result do
      :ok -> {:reply, {:ok, timestamp}, state}
      error -> {:reply, {:error, error}, state}
    end
  end

  @impl true
  def handle_call({:get_history, submission_id}, _from, state) do
    # Find all snapshots for this submission
    history =
      CubDB.select(@db_name)
      |> Stream.filter(fn
        {{:snapshot, ^submission_id, _ts}, _data} -> true
        _ -> false
      end)
      |> Stream.map(fn {{:snapshot, _, ts}, data} -> {ts, data} end)
      |> Enum.sort_by(fn {ts, _} -> ts end, {:desc, DateTime})

    {:reply, history, state}
  end

  @impl true
  def handle_call({:get_latest, submission_id}, _from, state) do
    case CubDB.get(@db_name, {:latest, submission_id}) do
      nil -> {:reply, {:error, :not_found}, state}
      data -> {:reply, {:ok, data}, state}
    end
  end

  @impl true
  def handle_call({:get_snapshot_at, submission_id, target_ts}, _from, state) do
    # Find the snapshot closest to (but not after) the target timestamp
    result =
      CubDB.select(@db_name)
      |> Stream.filter(fn
        {{:snapshot, ^submission_id, ts}, _data} ->
          DateTime.compare(ts, target_ts) != :gt
        _ ->
          false
      end)
      |> Enum.max_by(fn {{:snapshot, _, ts}, _} -> ts end, DateTime, fn -> nil end)

    case result do
      nil -> {:reply, {:error, :not_found}, state}
      {{:snapshot, _, _}, data} -> {:reply, {:ok, data}, state}
    end
  end

  @impl true
  def handle_call({:get, key}, _from, state) do
    {:reply, CubDB.get(@db_name, key), state}
  end

  @impl true
  def handle_call({:put, key, value}, _from, state) do
    CubDB.put(@db_name, key, value)
    {:reply, :ok, state}
  end

  @impl true
  def handle_call({:delete, key}, _from, state) do
    CubDB.delete(@db_name, key)
    {:reply, :ok, state}
  end

  @impl true
  def handle_call(:list_submissions, _from, state) do
    submissions =
      CubDB.select(@db_name)
      |> Stream.filter(fn
        {{:latest, _id}, _data} -> true
        _ -> false
      end)
      |> Stream.map(fn {{:latest, _id}, data} -> data end)
      |> Enum.to_list()

    {:reply, submissions, state}
  end

  @impl true
  def handle_call({:list_submissions, course_code}, _from, state) do
    submissions =
      CubDB.select(@db_name)
      |> Stream.filter(fn
        {{:latest, id}, _data} -> String.starts_with?(id, course_code)
        _ -> false
      end)
      |> Stream.map(fn {{:latest, _id}, data} -> data end)
      |> Enum.to_list()

    {:reply, submissions, state}
  end

  # --- Private Helpers ---

  defp get_data_dir do
    Application.get_env(:etma_handler, :data_dir, "priv/data")
  end
end
