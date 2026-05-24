# SPDX-License-Identifier: MPL-2.0
defmodule EtmaHandler.RepoTest do
  use ExUnit.Case, async: false

  alias EtmaHandler.Repo

  @id1 {"M150", "25J", "01", "rg8274"}
  @id2 {"M150", "25J", "02", "rg8274"}
  @id3 {"M150", "25B", "01", "rg8274"}
  @id4 {"TM470", "25J", "01", "ab1234"}

  setup do
    dir = Path.join(System.tmp_dir!(), "etma_repo_test_#{System.unique_integer([:positive])}")
    File.mkdir_p!(dir)

    {:ok, pid} = start_supervised({Repo, data_dir: dir})

    on_exit(fn -> File.rm_rf!(dir) end)

    {:ok, dir: dir, pid: pid}
  end

  describe "put_submission/2 and get_submission/1" do
    test "round-trips a submission" do
      assert :ok = Repo.put_submission(@id1, %{score: 75, comments: "Good work"})

      assert %{score: 75, comments: "Good work", id: @id1, updated_at: %DateTime{}} =
               Repo.get_submission(@id1)
    end

    test "get returns nil for absent submission" do
      refute Repo.get_submission(@id1)
    end

    test "put overwrites the live record" do
      :ok = Repo.put_submission(@id1, %{score: 50})
      :ok = Repo.put_submission(@id1, %{score: 80})

      assert %{score: 80} = Repo.get_submission(@id1)
    end

    test "updated_at advances on each put" do
      :ok = Repo.put_submission(@id1, %{score: 50})
      first = Repo.get_submission(@id1).updated_at

      Process.sleep(2)
      :ok = Repo.put_submission(@id1, %{score: 80})
      second = Repo.get_submission(@id1).updated_at

      assert DateTime.compare(second, first) == :gt
    end
  end

  describe "list_submissions/1" do
    setup do
      :ok = Repo.put_submission(@id1, %{score: 1})
      :ok = Repo.put_submission(@id2, %{score: 2})
      :ok = Repo.put_submission(@id3, %{score: 3})
      :ok = Repo.put_submission(@id4, %{score: 4})
      :ok
    end

    test "returns every submission with no filter" do
      ids = Repo.list_submissions() |> Enum.map(& &1.id) |> Enum.sort()
      assert ids == Enum.sort([@id1, @id2, @id3, @id4])
    end

    test "filters by course" do
      ids = Repo.list_submissions(course: "M150") |> Enum.map(& &1.id) |> Enum.sort()
      assert ids == Enum.sort([@id1, @id2, @id3])
    end

    test "filters by course and presentation" do
      ids =
        Repo.list_submissions(course: "M150", presentation: "25J")
        |> Enum.map(& &1.id)
        |> Enum.sort()

      assert ids == Enum.sort([@id1, @id2])
    end

    test "filter that matches nothing returns empty" do
      assert [] = Repo.list_submissions(course: "XX999")
    end

    test "list does not return audit or snapshot entries" do
      :ok = Repo.put_submission(@id1, %{score: 99})
      assert length(Repo.list_submissions()) == 4
    end
  end

  describe "delete_submission/1" do
    test "removes the live record but retains audit history" do
      :ok = Repo.put_submission(@id1, %{score: 50})
      :ok = Repo.delete_submission(@id1)

      refute Repo.get_submission(@id1)

      assert Enum.any?(Repo.history(@id1), fn {_, kind, payload} ->
               kind == :audit and payload.event == :delete
             end)
    end

    test "returns :not_found when nothing to delete" do
      assert {:error, :not_found} = Repo.delete_submission(@id1)
    end
  end

  describe "snapshot/1 and restore/2" do
    test "restores prior state" do
      :ok = Repo.put_submission(@id1, %{score: 10, draft: "rough"})
      {:ok, seq1} = Repo.snapshot(@id1)

      :ok = Repo.put_submission(@id1, %{score: 90, draft: "polished"})
      assert %{score: 90} = Repo.get_submission(@id1)

      :ok = Repo.restore(@id1, seq1)
      assert %{score: 10, draft: "rough"} = Repo.get_submission(@id1)
    end

    test "every put implicitly snapshots, so restore works without explicit snapshot" do
      :ok = Repo.put_submission(@id1, %{score: 10})
      :ok = Repo.put_submission(@id1, %{score: 90})

      # Pick the seq of the first implicit snapshot.
      [first_snap_seq | _] =
        Repo.history(@id1)
        |> Enum.filter(fn {_, k, _} -> k == :snapshot end)
        |> Enum.map(fn {seq, _, _} -> seq end)

      :ok = Repo.restore(@id1, first_snap_seq)
      assert %{score: 10} = Repo.get_submission(@id1)
    end

    test "snapshot of absent submission errors" do
      assert {:error, :not_found} = Repo.snapshot(@id1)
    end

    test "restore with no matching snapshot errors" do
      :ok = Repo.put_submission(@id1, %{score: 1})
      assert {:error, :no_snapshot} = Repo.restore(@id1, 0)
    end
  end

  describe "history/1" do
    test "returns audit + snapshot entries in chronological order" do
      :ok = Repo.put_submission(@id1, %{score: 1})
      :ok = Repo.put_submission(@id1, %{score: 2})
      {:ok, _} = Repo.snapshot(@id1)
      :ok = Repo.delete_submission(@id1)

      events = Repo.history(@id1)
      kinds = Enum.map(events, fn {_, k, _} -> k end)

      # 2 puts emit 2 :audit + 2 :snapshot.
      # Explicit snapshot emits 1 more :snapshot.
      # Delete emits 1 :audit.
      assert Enum.count(kinds, &(&1 == :audit)) == 3
      assert Enum.count(kinds, &(&1 == :snapshot)) == 3

      seqs = Enum.map(events, fn {s, _, _} -> s end)
      assert seqs == Enum.sort(seqs)
    end

    test "history is isolated per submission" do
      :ok = Repo.put_submission(@id1, %{score: 1})
      :ok = Repo.put_submission(@id2, %{score: 2})
      :ok = Repo.put_submission(@id2, %{score: 3})

      assert length(Repo.history(@id1)) == 2
      assert length(Repo.history(@id2)) == 4
    end
  end

  describe "concurrency" do
    test "concurrent puts to disjoint ids all land" do
      ids = for n <- 1..50, do: {"M150", "25J", "01", "x#{n}"}

      ids
      |> Task.async_stream(
        fn id -> Repo.put_submission(id, %{n: elem(id, 3)}) end,
        max_concurrency: 16,
        ordered: false
      )
      |> Enum.each(fn {:ok, :ok} -> :ok end)

      assert length(Repo.list_submissions()) == 50
    end

    test "concurrent puts to the same id serialise" do
      values = Enum.to_list(1..20)

      values
      |> Task.async_stream(
        fn v -> Repo.put_submission(@id1, %{score: v}) end,
        max_concurrency: 16,
        ordered: false
      )
      |> Enum.each(fn {:ok, :ok} -> :ok end)

      %{score: final} = Repo.get_submission(@id1)
      assert final in values

      audit_count =
        Repo.history(@id1)
        |> Enum.count(fn {_, k, _} -> k == :audit end)

      assert audit_count == 20
    end
  end

  describe "durability across process restart" do
    test "live record survives killing the CubDB process",
         %{dir: dir} do
      :ok = Repo.put_submission(@id1, %{score: 42, note: "keep me"})
      :ok = Repo.sync()

      stop_supervised!(Repo)
      {:ok, _pid} = start_supervised({Repo, data_dir: dir})

      assert %{score: 42, note: "keep me"} = Repo.get_submission(@id1)
    end

    test "audit log survives restart", %{dir: dir} do
      :ok = Repo.put_submission(@id1, %{score: 1})
      :ok = Repo.put_submission(@id1, %{score: 2})
      :ok = Repo.put_submission(@id1, %{score: 3})
      :ok = Repo.sync()

      stop_supervised!(Repo)
      {:ok, _pid} = start_supervised({Repo, data_dir: dir})

      audits = Repo.history(@id1) |> Enum.filter(fn {_, k, _} -> k == :audit end)
      assert length(audits) == 3
    end
  end
end
