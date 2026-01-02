# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandlerWeb.SettingsLive do
  @moduledoc """
  Application settings.

  Allows configuration of:
  - Download folder monitoring
  - Quality check thresholds
  - Display preferences
  - Security settings
  """

  use EtmaHandlerWeb, :live_view

  @impl true
  def mount(_params, _session, socket) do
    {:ok, assign(socket, :page_title, "Settings")}
  end

  @impl true
  def render(assigns) do
    ~H"""
    <div class="p-8 max-w-4xl mx-auto">
      <h1 class="text-2xl font-bold mb-4">Settings</h1>

      <div class="space-y-6">
        <div class="bg-white rounded-lg shadow p-6">
          <h2 class="text-lg font-bold mb-4">File Monitoring</h2>
          <p class="text-slate-500">Configure the Downloads folder watcher.</p>
        </div>

        <div class="bg-white rounded-lg shadow p-6">
          <h2 class="text-lg font-bold mb-4">Quality Checks</h2>
          <p class="text-slate-500">Set minimum word counts and other thresholds.</p>
        </div>

        <div class="bg-white rounded-lg shadow p-6">
          <h2 class="text-lg font-bold mb-4">Display</h2>
          <p class="text-slate-500">Adjust fonts, colors, and accessibility options.</p>
        </div>
      </div>
    </div>
    """
  end
end
