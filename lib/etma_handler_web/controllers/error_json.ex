# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandlerWeb.ErrorJSON do
  @moduledoc """
  Error responses for JSON API requests.
  """

  def render(template, _assigns) do
    %{errors: %{detail: Phoenix.Controller.status_message_from_template(template)}}
  end
end
