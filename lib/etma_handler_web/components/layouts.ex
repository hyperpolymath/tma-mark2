# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandlerWeb.Layouts do
  @moduledoc """
  Layout components for the eTMA Handler.

  Provides the root and app layouts that wrap all pages.
  """

  use EtmaHandlerWeb, :html

  embed_templates "layouts/*"
end
