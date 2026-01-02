# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.DataCase do
  @moduledoc """
  Test case for data layer tests.

  Provides helpers for setting up test data and cleaning up after tests.
  """

  use ExUnit.CaseTemplate

  using do
    quote do
      alias EtmaHandler.Repo

      import EtmaHandler.DataCase
    end
  end

  setup _tags do
    # Clean up test data directory if needed
    :ok
  end
end
