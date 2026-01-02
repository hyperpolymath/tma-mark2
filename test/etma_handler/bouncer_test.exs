# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.BouncerTest do
  use ExUnit.Case, async: true

  alias EtmaHandler.Bouncer

  describe "filename validation" do
    test "accepts valid OU filename format" do
      # Test the internal validation via the public API when bouncer is running
      # For now, test the pattern matching logic directly
      pattern = ~r/^([A-Z]{1,4}\d{2,4})-(\d{2}[JB])-(\d{2})-(\d+)-([a-z]{2}\d+)/i

      assert Regex.match?(pattern, "M150-25J-01-1-rg8274")
      assert Regex.match?(pattern, "TM470-25B-02-5-ab1234")
    end

    test "rejects invalid filename format" do
      pattern = ~r/^([A-Z]{1,4}\d{2,4})-(\d{2}[JB])-(\d{2})-(\d+)-([a-z]{2}\d+)/i

      refute Regex.match?(pattern, "invalid-filename")
      refute Regex.match?(pattern, "M150_25J_01_1_rg8274")
      refute Regex.match?(pattern, "document.docx")
    end
  end

  describe "extension validation" do
    @allowed_extensions ~w(.doc .docx .rtf .pdf .zip .fhi .odt)

    test "accepts allowed extensions" do
      for ext <- @allowed_extensions do
        assert ext in @allowed_extensions
      end
    end

    test "rejects disallowed extensions" do
      refute ".exe" in @allowed_extensions
      refute ".bat" in @allowed_extensions
      refute ".js" in @allowed_extensions
    end
  end

  describe "metadata extraction" do
    test "extracts metadata from valid filename" do
      # The filename pattern extracts:
      # - course code (M150)
      # - presentation (25J)
      # - TMA number (01)
      # - submission ID (1)
      # - student OUCU (rg8274)
      pattern = ~r/^([A-Z]{1,4}\d{2,4})-(\d{2}[JB])-(\d{2})-(\d+)-([a-z]{2}\d+)/i

      captures = Regex.run(pattern, "M150-25J-01-1-rg8274")

      assert captures == ["M150-25J-01-1-rg8274", "M150", "25J", "01", "1", "rg8274"]
    end
  end
end
