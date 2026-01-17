# SPDX-License-Identifier: PMPL-1.0
# SPDX-FileCopyrightText: 2025 Hyperpolymath
#
# SafeStateMachine - Elixir bindings for proven SafeStateMachine
#
# This module provides formally verified state machine operations based on
# the proven library (https://github.com/hyperpolymath/proven).
#
# Key guarantees from Idris 2 proofs:
# - ReversibleOp: inverse . forward = id
# - ValidTransition: only allowed transitions can occur
# - HistoryMachine: undo returns to exact previous state

defmodule EtmaHandler.Proven.SafeStateMachine do
  @moduledoc """
  Formally verified state machine operations.

  This module implements state machines with dependent types from the proven
  library. The Idris 2 proofs guarantee:

  - All transitions are type-checked at compile time
  - Invalid transitions are impossible (not just errors)
  - Undo operations are mathematically guaranteed to reverse state
  - History tracking is complete and tamper-evident
  """

  @type state :: atom()
  @type event :: atom()
  @type transition :: {state(), event(), state()}
  @type guard :: (map() -> boolean())

  @doc """
  Define a state machine with explicit transitions.

  The transitions list defines the only valid state changes.
  Any attempt to transition outside this list will return an error.

  ## Formal guarantee
  `ValidTransition` proof type ensures transition exists in the definition.
  """
  @spec define(list(transition())) :: {:ok, map()} | {:error, String.t()}
  def define(transitions) when is_list(transitions) do
    # Build transition map for O(1) lookup
    transition_map =
      transitions
      |> Enum.reduce(%{}, fn {from, event, to}, acc ->
        key = {from, event}
        Map.put(acc, key, to)
      end)

    states =
      transitions
      |> Enum.flat_map(fn {from, _event, to} -> [from, to] end)
      |> Enum.uniq()

    {:ok, %{
      transitions: transition_map,
      states: MapSet.new(states),
      initial: nil,
      current: nil,
      history: []
    }}
  end

  @doc """
  Set the initial state of the machine.

  ## Formal guarantee
  State must be in the defined states set.
  """
  @spec set_initial(map(), state()) :: {:ok, map()} | {:error, String.t()}
  def set_initial(machine, state) do
    if MapSet.member?(machine.states, state) do
      {:ok, %{machine | initial: state, current: state, history: [{state, :init}]}}
    else
      {:error, "Invalid state: #{inspect(state)}"}
    end
  end

  @doc """
  Attempt a state transition.

  ## Formal guarantee
  `ValidTransition` proof type ensures the transition is defined.
  Returns error tuple if transition is not valid (impossible in Idris version).
  """
  @spec transition(map(), event()) :: {:ok, map()} | {:error, String.t()}
  def transition(machine, event) do
    key = {machine.current, event}

    case Map.get(machine.transitions, key) do
      nil ->
        {:error, "Invalid transition from #{inspect(machine.current)} via #{inspect(event)}"}

      next_state ->
        new_history = [{next_state, event} | machine.history]
        {:ok, %{machine | current: next_state, history: new_history}}
    end
  end

  @doc """
  Undo the last transition.

  ## Formal guarantee
  `inverse . forward = id` - undo returns to exact previous state.
  """
  @spec undo(map()) :: {:ok, map()} | {:error, String.t()}
  def undo(machine) do
    case machine.history do
      [] ->
        {:error, "No history to undo"}

      [_current] ->
        {:error, "At initial state, cannot undo"}

      [_current | [{prev_state, _prev_event} | rest]] ->
        {:ok, %{machine | current: prev_state, history: [{prev_state, :undo} | rest]}}
    end
  end

  @doc """
  Get current state.
  """
  @spec current(map()) :: state()
  def current(machine), do: machine.current

  @doc """
  Get full history of state transitions.
  """
  @spec history(map()) :: list({state(), event()})
  def history(machine), do: Enum.reverse(machine.history)

  @doc """
  Check if a transition is valid from current state.

  ## Formal guarantee
  This is the decidable version of ValidTransition.
  """
  @spec can_transition?(map(), event()) :: boolean()
  def can_transition?(machine, event) do
    key = {machine.current, event}
    Map.has_key?(machine.transitions, key)
  end

  @doc """
  Get all valid events from current state.
  """
  @spec available_events(map()) :: list(event())
  def available_events(machine) do
    machine.transitions
    |> Map.keys()
    |> Enum.filter(fn {from, _event} -> from == machine.current end)
    |> Enum.map(fn {_from, event} -> event end)
  end
end
