# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler do
  @moduledoc """
  eTMA Handler - Open University Marking Tool (BEAM Edition)

  A modern, crash-proof marking application built on the Erlang VM (BEAM).
  Designed to run on any platform: Linux, Windows, macOS, RISC-V, and even Minix.

  ## Features

  - **Crash-Proof Storage**: Uses CubDB (append-only B-tree) - never lose marking data
  - **Time Machine**: Every save creates a snapshot - undo/redo at any point
  - **What-If Calculator**: "How much do I need on Q3 to pass?" - solved instantly
  - **Quality Bot**: Prevents empty summaries, thin feedback, wrong names
  - **Automatic Ingestion**: Drop files in Downloads - they're validated immediately
  - **Post-Quantum Security**: Student data encrypted with modern cryptography

  ## Quick Start

      # Start the application
      mix phx.server

      # Open in browser
      open http://localhost:4000

  ## Architecture

  The application is structured as follows:

  - `EtmaHandler.Repo` - CubDB-backed data storage with versioning
  - `EtmaHandler.Bouncer` - File watcher and validator
  - `EtmaHandler.Marking.Audit` - Feedback quality checks
  - `EtmaHandler.Logic.Calculator` - Smart mark calculations
  - `EtmaHandlerWeb` - Phoenix LiveView interface
  """

  @doc """
  Returns the current version of the application.
  """
  def version do
    case :application.get_key(:etma_handler, :vsn) do
      {:ok, vsn} -> List.to_string(vsn)
      _ -> "dev"
    end
  end

  @doc """
  Returns the data directory path.
  """
  def data_dir do
    Application.get_env(:etma_handler, :data_dir)
  end

  @doc """
  Returns the downloads directory path.
  """
  def downloads_dir do
    Application.get_env(:etma_handler, :downloads_dir)
  end
end
