# SPDX-License-Identifier: PMPL-1.0-or-later
defmodule EtmaHandlerWeb.ErrorHTML do
  @moduledoc """
  Error pages for HTML requests.
  """

  use EtmaHandlerWeb, :html

  def render(template, _assigns) do
    Phoenix.Controller.status_message_from_template(template)
  end
end
