# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandlerWeb.RefineryLive do
  @moduledoc """
  The "Refinery" - Comment Bank Management.

  Allows tutors to:
  - Create and edit feedback templates
  - Tag comments for easy searching
  - Import/export comment banks
  """

  use EtmaHandlerWeb, :live_view

  @impl true
  def mount(_params, _session, socket) do
    {:ok, assign(socket, :page_title, "Comment Bank")}
  end

  @impl true
  def render(assigns) do
    ~H"""
    <div class="p-8 max-w-4xl mx-auto">
      <h1 class="text-2xl font-bold mb-4">Comment Bank (Refinery)</h1>
      <p class="text-slate-600 mb-8">
        Manage your feedback templates here. Comments can be tagged and searched
        for quick insertion during marking.
      </p>

      <div class="bg-white rounded-lg shadow p-6">
        <h2 class="text-lg font-bold mb-4">Your Comments</h2>
        <p class="text-slate-500">Comment management coming soon.</p>
      </div>
    </div>
    """
  end
end
