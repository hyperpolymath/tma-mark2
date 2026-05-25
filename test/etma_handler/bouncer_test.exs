# SPDX-License-Identifier: MPL-2.0
defmodule EtmaHandler.BouncerTest do
  use ExUnit.Case, async: true

  alias EtmaHandler.Bouncer

  describe "filename validation" do
    test "accepts valid OU filename format" do
      pattern = Bouncer.filename_pattern()

      assert Regex.match?(pattern, "M150-25J-01-1-rg8274")
      assert Regex.match?(pattern, "TM470-25B-02-5-ab1234")
    end

    test "rejects invalid filename format" do
      pattern = Bouncer.filename_pattern()

      refute Regex.match?(pattern, "invalid-filename")
      refute Regex.match?(pattern, "M150_25J_01_1_rg8274")
      refute Regex.match?(pattern, "document.docx")
    end
  end

  describe "extension validation" do
    test "document extensions are exposed" do
      docs = Bouncer.document_extensions()

      assert ".docx" in docs
      assert ".pdf" in docs
      assert ".odt" in docs
    end

    test "code extensions are exposed" do
      code = Bouncer.code_extensions()

      assert ".ex" in code
      assert ".rs" in code
    end

    test "executables are not in any allowlist" do
      all = Bouncer.document_extensions() ++ Bouncer.code_extensions()

      refute ".exe" in all
      refute ".bat" in all
      refute ".sh" in all
    end
  end

  describe "metadata extraction" do
    test "extracts metadata from valid filename" do
      # course code, presentation, TMA number, submission ID, student OUCU
      captures = Regex.run(Bouncer.filename_pattern(), "M150-25J-01-1-rg8274")

      assert captures == ["M150-25J-01-1-rg8274", "M150", "25J", "01", "1", "rg8274"]
    end
  end

  describe "supervision tree behaviour" do
    test "init/1 stops with :not_implemented" do
      assert {:stop, :not_implemented} = Bouncer.init([])
    end
  end
end
