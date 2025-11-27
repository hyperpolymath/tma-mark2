defmodule EtmaHandlerWeb.CourseLive.Show do
  @moduledoc """
  Course details view.
  """

  use EtmaHandlerWeb, :live_view

  @impl true
  def mount(_params, _session, socket) do
    {:ok, assign(socket, :page_title, "Course Details")}
  end

  @impl true
  def render(assigns) do
    ~H"""
    <div class="p-8">
      <h1 class="text-2xl font-bold mb-4">Course Details</h1>
      <p class="text-slate-600">Course details coming soon.</p>
    </div>
    """
  end
end
