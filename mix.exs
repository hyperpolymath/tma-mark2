# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.MixProject do
  use Mix.Project

  @version "2.0.0"
  @description "eTMA Handler - Open University Marking Tool (BEAM Edition)"

  def project do
    [
      app: :etma_handler,
      version: @version,
      elixir: "~> 1.17",
      elixirc_paths: elixirc_paths(Mix.env()),
      start_permanent: Mix.env() == :prod,
      aliases: aliases(),
      deps: deps(),
      description: @description,
      package: package(),
      # Tier 2: The Burrito Wrapper (Cross-Platform Binaries)
      releases: releases()
    ]
  end

  defp package do
    [
      name: "etma_handler",
      licenses: ["AGPL-3.0-or-later"],
      links: %{
        "GitHub" => "https://github.com/hyperpolymath/tma-mark2"
      },
      files: ~w(lib config mix.exs README.adoc LICENSE.txt LICENSES)
    ]
  end

  def application do
    [
      mod: {EtmaHandler.Application, []},
      extra_applications: [:logger, :runtime_tools, :os_mon, :crypto]
    ]
  end

  defp elixirc_paths(:test), do: ["lib", "test/support"]
  defp elixirc_paths(_), do: ["lib"]

  defp deps do
    [
      # --- The Core Engine ---
      {:phoenix, "~> 1.7"},
      {:phoenix_live_view, "~> 1.0"},
      {:bandit, "~> 1.0"},
      {:phoenix_html, "~> 4.0"},
      {:phoenix_live_reload, "~> 1.5", only: :dev},
      {:floki, ">= 0.36.0", only: :test},
      {:esbuild, "~> 0.8", runtime: Mix.env() == :dev},
      {:tailwind, "~> 0.2", runtime: Mix.env() == :dev},
      {:heroicons, "~> 0.5"},
      {:jason, "~> 1.4"},
      {:telemetry_metrics, "~> 1.0"},
      {:telemetry_poller, "~> 1.1"},
      {:phoenix_live_dashboard, "~> 0.8"},

      # --- The "Magic Bullet" Data Layer ---
      # CubDB: Pure Elixir, crash-proof, append-only B-Tree database
      # Runs everywhere the BEAM runs (RISC-V, Minix, etc.)
      {:cubdb, "~> 2.0"},
      # Cachex: In-process caching (replaces DragonflyDB complexity)
      {:cachex, "~> 4.0"},
      # Ecto for validation logic (schemaless changesets)
      {:ecto, "~> 3.12"},
      # SweetXml for parsing legacy .fhi and .docx files
      {:sweet_xml, "~> 0.7"},
      # Timex for date parsing (FHI dates use "23-Nov-2025 23:52:29" format)
      {:timex, "~> 3.7"},

      # --- The Fort Knox Vault (Security) ---
      # Argon2id for password hashing (memory-hard, side-channel resistant)
      {:argon2_elixir, "~> 4.0"},
      # Rustler for Rust NIFs (crypto, NLP)
      {:rustler, "~> 0.35"},

      # --- Network & HTTP ---
      # Mint for HTTP/2 client (VirusTotal, OU APIs)
      {:mint, "~> 1.6"},
      {:castore, "~> 1.0"},
      # Req for high-level HTTP
      {:req, "~> 0.5"},

      # --- System Integration ---
      # Watch Downloads folder for automatic file ingestion
      {:file_system, "~> 1.0"},
      # RSS/Atom parsing for forum monitoring
      # TODO: Add when compatible parser available (feeder needs OTP update, feedraptor needs floki update)

      # --- Logic & Rules ---
      # Guesswork - Logic programming for learning rules (no LLMs)
      {:guesswork, "~> 0.8"},

      # --- Optional/Experimental ---
      # CBOR encoding for efficient serialization
      {:cbor, "~> 1.0"},
      # Binary wrapper for cross-platform distribution
      {:burrito, "~> 1.0", only: [:dev, :prod]}
    ]
  end

  # Burrito Configuration (The "Chimichanga")
  defp releases do
    [
      etma_handler: [
        steps: [:assemble] ++ burrito_steps(),
        burrito: [
          targets: [
            # Linux (x86_64) - For Fedora/Ubuntu/Debian
            linux: [os: :linux, cpu: :x86_64],
            # Linux (ARM64) - For Raspberry Pi, Apple Silicon VMs
            linux_arm: [os: :linux, cpu: :aarch64],
            # Linux (RISC-V) - For development boards
            riscv: [os: :linux, cpu: :riscv64],
            # Windows (x86_64) - For work laptops
            windows: [os: :windows, cpu: :x86_64],
            # macOS (Intel)
            macos_intel: [os: :darwin, cpu: :x86_64],
            # macOS (Apple Silicon)
            macos_arm: [os: :darwin, cpu: :aarch64]
          ]
        ]
      ]
    ]
  end

  defp burrito_steps do
    if Code.ensure_loaded?(Burrito) do
      [&Burrito.wrap/1]
    else
      []
    end
  end

  defp aliases do
    [
      setup: ["deps.get", "assets.setup", "assets.build"],
      "assets.setup": ["tailwind.install --if-missing", "esbuild.install --if-missing"],
      "assets.build": ["tailwind default", "esbuild default"],
      "assets.deploy": ["tailwind default --minify", "esbuild default --minify", "phx.digest"],
      test: ["test"]
    ]
  end
end
