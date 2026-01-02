# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Logic.CalculatorTest do
  use ExUnit.Case, async: true

  alias EtmaHandler.Logic.Calculator

  describe "evaluate/1" do
    test "evaluates simple numbers" do
      assert Calculator.evaluate("10") == {:ok, 10}
      assert Calculator.evaluate("0") == {:ok, 0}
      assert Calculator.evaluate("100") == {:ok, 100}
    end

    test "evaluates addition" do
      assert Calculator.evaluate("10+5") == {:ok, 15}
      assert Calculator.evaluate("1+2+3") == {:ok, 6}
    end

    test "evaluates subtraction" do
      assert Calculator.evaluate("10-5") == {:ok, 5}
      assert Calculator.evaluate("20-10-5") == {:ok, 5}
    end

    test "evaluates multiplication" do
      assert Calculator.evaluate("5*3") == {:ok, 15}
    end

    test "evaluates division" do
      assert Calculator.evaluate("10/2") == {:ok, 5}
    end

    test "handles whitespace" do
      assert Calculator.evaluate(" 10 + 5 ") == {:ok, 15}
    end

    test "returns error for invalid expressions" do
      assert Calculator.evaluate("abc") == {:error, :invalid_expression}
      assert Calculator.evaluate("10++5") == {:error, :invalid_expression}
    end
  end

  describe "what_if_grade/2" do
    test "calculates needed marks for target grade" do
      current_marks = [15, 20, 30]  # Total 65
      {:ok, needed, _msg} = Calculator.what_if_grade(current_marks, 85)

      # Need 20 more marks to reach 85
      assert needed == 20
    end

    test "returns achievable message when target is reachable" do
      current_marks = [10, 10, 10]
      {:ok, _needed, msg} = Calculator.what_if_grade(current_marks, 50)

      assert is_binary(msg)
    end

    test "returns impossible message when target exceeds max" do
      current_marks = [10, 10, 10]  # 30 total
      result = Calculator.what_if_grade(current_marks, 150)

      assert match?({:error, _}, result)
    end
  end

  describe "total_marks/1" do
    test "sums all marks" do
      assert Calculator.total_marks([10, 20, 30]) == 60
      assert Calculator.total_marks([]) == 0
      assert Calculator.total_marks([100]) == 100
    end
  end

  describe "percentage/2" do
    test "calculates percentage" do
      assert Calculator.percentage(50, 100) == 50.0
      assert Calculator.percentage(75, 100) == 75.0
      assert Calculator.percentage(30, 60) == 50.0
    end

    test "handles zero maximum" do
      assert Calculator.percentage(10, 0) == 0.0
    end
  end
end
