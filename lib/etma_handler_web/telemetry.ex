defmodule EtmaHandlerWeb.Telemetry do
  @moduledoc """
  Telemetry metrics and supervision for eTMA Handler.

  Collects metrics about:
  - HTTP request performance
  - LiveView mount/handle times
  - Database operations
  - File ingestion events
  """

  use Supervisor
  import Telemetry.Metrics

  def start_link(arg) do
    Supervisor.start_link(__MODULE__, arg, name: __MODULE__)
  end

  @impl true
  def init(_arg) do
    children = [
      # Telemetry poller will execute the given period measurements
      {:telemetry_poller, measurements: periodic_measurements(), period: 10_000}
    ]

    Supervisor.init(children, strategy: :one_for_one)
  end

  def metrics do
    [
      # Phoenix Metrics
      summary("phoenix.endpoint.start.system_time",
        unit: {:native, :millisecond}
      ),
      summary("phoenix.endpoint.stop.duration",
        unit: {:native, :millisecond}
      ),
      summary("phoenix.router_dispatch.start.system_time",
        tags: [:route],
        unit: {:native, :millisecond}
      ),
      summary("phoenix.router_dispatch.stop.duration",
        tags: [:route],
        unit: {:native, :millisecond}
      ),

      # LiveView Metrics
      summary("phoenix.live_view.mount.start.system_time",
        unit: {:native, :millisecond}
      ),
      summary("phoenix.live_view.mount.stop.duration",
        unit: {:native, :millisecond}
      ),
      summary("phoenix.live_view.handle_event.start.system_time",
        unit: {:native, :millisecond}
      ),
      summary("phoenix.live_view.handle_event.stop.duration",
        tags: [:event],
        unit: {:native, :millisecond}
      ),

      # VM Metrics
      summary("vm.memory.total", unit: {:byte, :kilobyte}),
      summary("vm.total_run_queue_lengths.total"),
      summary("vm.total_run_queue_lengths.cpu"),
      summary("vm.total_run_queue_lengths.io"),

      # Custom eTMA Metrics
      counter("etma.submission.saved.count"),
      counter("etma.file.ingested.count"),
      counter("etma.file.rejected.count"),
      summary("etma.audit.duration", unit: {:native, :millisecond})
    ]
  end

  defp periodic_measurements do
    [
      # A module, function and arguments to be invoked periodically.
      {__MODULE__, :count_submissions, []}
    ]
  end

  @doc false
  def count_submissions do
    # This would emit telemetry about the number of submissions in the system
    # Useful for monitoring and dashboards
    :telemetry.execute(
      [:etma, :submissions],
      %{count: 0},
      %{}
    )
  end
end
