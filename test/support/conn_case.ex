# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandlerWeb.ConnCase do
  @moduledoc """
  Test case for connection/controller tests.

  Sets up the Plug.Test connection for each test.
  """

  use ExUnit.CaseTemplate

  using do
    quote do
      import Plug.Conn
      import Phoenix.ConnTest
      import EtmaHandlerWeb.ConnCase

      alias EtmaHandlerWeb.Router.Helpers, as: Routes

      @endpoint EtmaHandlerWeb.Endpoint
    end
  end

  setup _tags do
    {:ok, conn: Phoenix.ConnTest.build_conn()}
  end
end
