# SPDX-License-Identifier: AGPL-3.0-or-later

defmodule EtmaHandler do
  @moduledoc """
  eTMA Handler — High-Assurance Marking Infrastructure.

  This application provides a "crash-proof" environment for the marking 
  and auditing of student submissions. It is designed to be resilient 
  to both software failures and hardware power loss.

  ## Reliability Architecture
  1. **Persistence**: Uses `CubDB` (append-only storage) to ensure that 
     marking data is never corrupted, even during a system crash.
  2. **Auditability**: Maintains a "Time Machine" of every change, 
     allowing for granular undo/redo and formal verification of results.
  3. **Verification**: Employs a symbolic "Quality Bot" to enforce 
     academic rigor and consistency across multiple markers.
  """

  @doc """
  Returns the current system version.
  """
  def version do
    case :application.get_key(:etma_handler, :vsn) do
      {:ok, vsn} -> List.to_string(vsn)
      _ -> "dev"
    end
  end

  @doc """
  Resolves the physical path for data persistence.
  """
  def data_dir do
    Application.get_env(:etma_handler, :data_dir)
  end
end
