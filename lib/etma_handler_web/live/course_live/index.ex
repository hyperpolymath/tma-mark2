defmodule EtmaHandlerWeb.CourseLive.Index do
  @moduledoc """
  Course management - list and configure courses.
  """

  use EtmaHandlerWeb, :live_view

  @impl true
  def mount(_params, _session, socket) do
    {:ok, assign(socket, :page_title, "Courses")}
  end

  @impl true
  def render(assigns) do
    ~H"""
    <div class="p-8">
      <h1 class="text-2xl font-bold mb-4">Course Management</h1>
      <p class="text-slate-600">Course configuration coming soon.</p>
    </div>
    """
  end
end
