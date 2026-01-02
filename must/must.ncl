# SPDX-License-Identifier: AGPL-3.0-or-later
#
# must/must.ncl - Physical State Contract for tma-mark2
#
# This file defines what MUST be true for the project to be
# considered valid, buildable, and deployable.
#
# Validation: just must-validate
# Strict:     just must-validate-strict

let security = import "./contracts/security.ncl" in
let platform = import "./contracts/platform.ncl" in

{
  # ============================================================
  # PROJECT IDENTITY
  # ============================================================

  project = {
    name = "tma-mark2",
    version = "2.0.0",
    description = "eTMA Handler - Open University Marking Tool",

    license = {
      spdx = "AGPL-3.0-or-later",
      additional = "LicenseRef-Palimpsest-0.5",
      philosophy = "Copyleft with compassion",
    },

    maintainers = [
      { name = "hyperpolymath", email = "hyperpolymath@users.noreply.github.com" }
    ],

    repository = "https://github.com/hyperpolymath/tma-mark2",
  },

  # ============================================================
  # PHYSICAL STATE REQUIREMENTS
  # ============================================================

  must = {
    # Files that MUST exist for valid build
    files_present = [
      # --- Legal & Compliance ---
      "LICENSES/AGPL-3.0-or-later.txt",
      "LICENSES/Palimpsest-0.5.txt",
      "SECURITY.md",
      "CODE_OF_CONDUCT.adoc",

      # --- Build System (Guix primary, Nix fallback) ---
      "guix.scm",
      "flake.nix",
      "flake.lock",
      "mix.exs",

      # --- Container ---
      "Containerfile.hardened",

      # --- Orchestration ---
      "Justfile",
      "must/must.ncl",

      # --- Documentation ---
      "README.adoc",
      "CONTRIBUTING.adoc",

      # --- State Files (Guile Scheme) ---
      "STATE.scm",
      "META.scm",
      "ECOSYSTEM.scm",

      # --- Configuration ---
      "config/default.ncl",

      # --- Core Application ---
      "lib/etma_handler.ex",
      "lib/etma_handler/application.ex",
    ],

    # Files that MUST NOT exist (banned patterns)
    files_absent = [
      # --- Banned by RSR Language Policy ---
      "package.json",
      "package-lock.json",
      "yarn.lock",
      "pnpm-lock.yaml",
      "bun.lockb",
      "go.mod",
      "go.sum",
      "Makefile",              # Use Just instead
      "tsconfig.json",         # Use ReScript

      # --- Security Risks ---
      ".env",                  # Use secrets management
      ".env.local",
      ".env.production",
      "credentials.json",
      "secrets.json",
      ".npmrc",                # No npm

      # --- Private Keys (never commit) ---
      "*.pem",
      "*.key",
      "*.p12",

      # --- CI/CD Secrets ---
      ".github/secrets.yml",
    ],

    # Content requirements (strings that MUST appear in files)
    content_requirements = [
      {
        file = "mix.exs",
        contains = "AGPL-3.0-or-later",
        reason = "License must be declared in mix.exs",
      },
      {
        file = "Containerfile.hardened",
        contains = "cgr.dev/chainguard/wolfi-base",
        reason = "Must use Wolfi security-hardened base image",
      },
      {
        file = "Containerfile.hardened",
        contains = "USER",
        reason = "Container must run as non-root user",
      },
      {
        file = "Containerfile.hardened",
        contains = "HEALTHCHECK",
        reason = "Container must have health check",
      },
      {
        file = "Justfile",
        contains = "SPDX-License-Identifier",
        reason = "All files must have SPDX headers",
      },
      {
        file = "guix.scm",
        contains = "SPDX-License-Identifier",
        reason = "All files must have SPDX headers",
      },
    ],

    # Build artifacts required for release
    artifacts = {
      container_images = [
        {
          name = "ghcr.io/hyperpolymath/tma-mark2",
          platforms = ["linux/amd64", "linux/arm64", "linux/riscv64"],
          signed = true,
          sbom_attached = true,
        },
      ],

      binaries = [
        {
          name = "tma-mark2",
          platforms = platform.all_desktop,
          type = "burrito",
          signed = true,
        },
      ],

      mobile_apps = [
        {
          name = "tma-mark2-companion",
          platforms = ["ios", "android"],
          framework = "dioxus",
          signed = true,
        },
      ],
    },
  },

  # ============================================================
  # SECURITY CONTRACTS
  # ============================================================

  security = security.contracts,

  # ============================================================
  # PLATFORM CONTRACTS
  # ============================================================

  platforms = platform.contracts,

  # ============================================================
  # VALIDATION HOOKS
  # ============================================================

  hooks = {
    # Run before every commit
    pre_commit = [
      "just format",
      "just lint",
      "just must-check-files",
      "just must-check-banned",
    ],

    # Run before release
    pre_release = [
      "just must-validate-strict",
      "just test",
      "just security-audit",
      "just sbom-generate",
    ],

    # Run in CI
    ci = [
      "just build",
      "just test",
      "just lint",
      "just must-validate",
      "just security-audit",
    ],
  },

  # ============================================================
  # ERGONOMICS
  # ============================================================

  ergonomics = {
    # Default commands should be intuitive
    default_commands = {
      build = "just build",
      test = "just test",
      dev = "just dev",
      lint = "just lint",
      release = "just release VERSION",
    },

    # Error messages should be helpful
    error_guidance = {
      missing_guix = "Install Guix: https://guix.gnu.org/manual/en/html_node/Installation.html",
      missing_nix = "Install Nix: https://nixos.org/download.html",
      missing_container = "Install nerdctl/podman/docker",
    },
  },
}
