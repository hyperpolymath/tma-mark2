defmodule EtmaHandler.Logic.Calculator do
  @moduledoc """
  The "What-If" Calculator Engine.

  Handles mark calculations in two directions:
  1. **Forward**: "I got 10+5 on Q1" → "= 15"
  2. **Backward**: "I want 85 total, I have 60. What do I need?" → "25 more"

  The legacy Java app let users type expressions like "1+4+5" in mark fields.
  This module preserves that behavior and adds the "What-If" feature.

  ## Example

      # Forward calculation (standard marking)
      Calculator.evaluate("10 + 2 + 3")
      # => {:ok, 15}

      # What-If calculation
      Calculator.what_if_grade([10, 20, 15], 85)
      # => {:ok, 40, "You need 40 more marks to reach 85"}

      # Validate against max score
      Calculator.evaluate_with_max("10 + 15", 20)
      # => {:error, "Result (25) exceeds maximum score (20)"}
  """

  @type eval_result :: {:ok, number()} | {:error, String.t()}

  @doc """
  Safely evaluates a simple arithmetic expression.

  Only allows digits, +, -, *, /, and parentheses.
  No arbitrary code execution.

  ## Examples

      iex> Calculator.evaluate("10 + 5")
      {:ok, 15}

      iex> Calculator.evaluate("1 + 2 + 3")
      {:ok, 6}

      iex> Calculator.evaluate("invalid")
      {:error, "Invalid expression"}
  """
  @spec evaluate(String.t() | nil) :: eval_result()
  def evaluate(nil), do: {:ok, 0}
  def evaluate(""), do: {:ok, 0}

  def evaluate(expression) when is_binary(expression) do
    # Clean the expression
    cleaned = expression |> String.trim() |> String.replace(~r/\s+/, "")

    # Validate: only allow safe characters
    if Regex.match?(~r/^[\d\+\-\*\/\(\)\.\s]+$/, cleaned) do
      try do
        # Parse and evaluate safely (no Code.eval!)
        result = safe_eval(cleaned)
        {:ok, result}
      rescue
        _ -> {:error, "Invalid expression"}
      end
    else
      {:error, "Invalid characters in expression"}
    end
  end

  def evaluate(number) when is_number(number), do: {:ok, number}

  @doc """
  Evaluates an expression and validates against a maximum score.

  Returns an error if the result exceeds the maximum.
  """
  @spec evaluate_with_max(String.t() | nil, number()) :: eval_result()
  def evaluate_with_max(expression, max_score) do
    case evaluate(expression) do
      {:ok, result} when result > max_score ->
        {:error, "Result (#{result}) exceeds maximum score (#{max_score})"}

      {:ok, result} when result < 0 ->
        {:error, "Score cannot be negative (#{result})"}

      {:ok, result} ->
        {:ok, result}

      error ->
        error
    end
  end

  @doc """
  Calculates what score is needed to reach a target grade.

  ## Examples

      iex> Calculator.what_if_grade([10, 20], 85)
      {:ok, 55, "You need 55 more marks to reach 85"}

      iex> Calculator.what_if_grade([50, 40], 85)
      {:ok, -5, "You've already exceeded 85 by 5 marks"}
  """
  @spec what_if_grade([number()], number()) :: {:ok, number(), String.t()} | {:error, String.t()}
  def what_if_grade(current_marks, target_total) when is_list(current_marks) do
    current_total = Enum.sum(current_marks)
    needed = target_total - current_total

    message =
      cond do
        needed > 0 -> "You need #{needed} more marks to reach #{target_total}"
        needed < 0 -> "You've already exceeded #{target_total} by #{abs(needed)} marks"
        true -> "You've exactly reached #{target_total}"
      end

    {:ok, needed, message}
  end

  @doc """
  Calculates what score is needed on a specific question to reach a target.

  ## Examples

      iex> Calculator.what_if_question([10, 20, nil], 85, 2, 50)
      {:ok, 55, "You need 55/50 on Question 3 - impossible without extra credit"}
  """
  @spec what_if_question([number() | nil], number(), non_neg_integer(), number()) ::
          {:ok, number(), String.t()} | {:error, String.t()}
  def what_if_question(marks, target_total, question_index, max_for_question)
      when is_list(marks) do
    # Sum all marks except the target question
    known_total =
      marks
      |> Enum.with_index()
      |> Enum.reject(fn {_, idx} -> idx == question_index end)
      |> Enum.map(fn {mark, _} -> mark || 0 end)
      |> Enum.sum()

    needed = target_total - known_total

    message =
      cond do
        needed <= 0 ->
          "You don't need any marks on Question #{question_index + 1} to reach #{target_total}"

        needed > max_for_question ->
          "You need #{needed}/#{max_for_question} on Question #{question_index + 1} - impossible without extra credit"

        true ->
          "You need #{needed}/#{max_for_question} on Question #{question_index + 1}"
      end

    {:ok, needed, message}
  end

  @doc """
  Calculates the weighted average (for overall module grades).

  ## Examples

      iex> Calculator.weighted_average([{80, 0.2}, {70, 0.3}, {90, 0.5}])
      {:ok, 83.0}
  """
  @spec weighted_average([{number(), number()}]) :: {:ok, float()} | {:error, String.t()}
  def weighted_average(scores_and_weights) when is_list(scores_and_weights) do
    total_weight = scores_and_weights |> Enum.map(fn {_, w} -> w end) |> Enum.sum()

    if abs(total_weight - 1.0) > 0.001 do
      {:error, "Weights must sum to 1.0 (got #{total_weight})"}
    else
      result =
        scores_and_weights
        |> Enum.map(fn {score, weight} -> score * weight end)
        |> Enum.sum()

      {:ok, Float.round(result, 2)}
    end
  end

  @doc """
  Parses a mark expression and returns both the original string and calculated value.

  Useful for preserving "working out" in the UI while showing the result.
  """
  @spec parse_with_history(String.t() | nil) :: %{
          original: String.t() | nil,
          result: number() | nil,
          error: String.t() | nil
        }
  def parse_with_history(expression) do
    case evaluate(expression) do
      {:ok, result} ->
        %{original: expression, result: result, error: nil}

      {:error, msg} ->
        %{original: expression, result: nil, error: msg}
    end
  end

  # --- Safe Expression Evaluation ---

  # This is a simple recursive descent parser for arithmetic expressions.
  # It's much safer than Code.eval_string because it only handles numbers and operators.

  defp safe_eval(expr) do
    {result, ""} = parse_expression(expr)
    result
  end

  # Parse addition/subtraction (lowest precedence)
  defp parse_expression(expr) do
    {left, rest} = parse_term(expr)
    parse_expression_rest(left, rest)
  end

  defp parse_expression_rest(left, "+" <> rest) do
    {right, rest2} = parse_term(rest)
    parse_expression_rest(left + right, rest2)
  end

  defp parse_expression_rest(left, "-" <> rest) do
    {right, rest2} = parse_term(rest)
    parse_expression_rest(left - right, rest2)
  end

  defp parse_expression_rest(left, rest), do: {left, rest}

  # Parse multiplication/division (higher precedence)
  defp parse_term(expr) do
    {left, rest} = parse_factor(expr)
    parse_term_rest(left, rest)
  end

  defp parse_term_rest(left, "*" <> rest) do
    {right, rest2} = parse_factor(rest)
    parse_term_rest(left * right, rest2)
  end

  defp parse_term_rest(left, "/" <> rest) do
    {right, rest2} = parse_factor(rest)

    if right == 0 do
      raise "Division by zero"
    end

    parse_term_rest(left / right, rest2)
  end

  defp parse_term_rest(left, rest), do: {left, rest}

  # Parse numbers and parentheses (highest precedence)
  defp parse_factor("(" <> rest) do
    {result, ")" <> rest2} = parse_expression(rest)
    {result, rest2}
  end

  defp parse_factor(expr) do
    {number_str, rest} = parse_number(expr)
    {parse_number_value(number_str), rest}
  end

  defp parse_number(expr) do
    case Regex.run(~r/^(\d+\.?\d*)(.*)$/, expr) do
      [_, num, rest] -> {num, rest}
      nil -> raise "Expected number"
    end
  end

  defp parse_number_value(str) do
    if String.contains?(str, ".") do
      String.to_float(str)
    else
      String.to_integer(str)
    end
  end
end
