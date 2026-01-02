# SPDX-License-Identifier: AGPL-3.0-or-later
#
# must/contracts/platform.ncl - Platform Support Definitions
#
# Defines supported platforms, build requirements, and
# deployment targets for tma-mark2

{
  # ============================================================
  # PLATFORM LISTS
  # ============================================================

  # All desktop platforms
  all_desktop = [
    "linux-x86_64",
    "linux-aarch64",
    "linux-riscv64",
    "darwin-x86_64",
    "darwin-aarch64",
    "windows-x86_64",
    "minix-x86_64",
  ],

  # All mobile platforms
  all_mobile = [
    "ios-aarch64",
    "android-aarch64",
    "android-x86_64",   # Emulator support
  ],

  # Tier 1: Full support, CI tested
  tier1 = [
    "linux-x86_64",
    "linux-aarch64",
    "darwin-aarch64",
  ],

  # Tier 2: Supported, periodically tested
  tier2 = [
    "darwin-x86_64",
    "windows-x86_64",
    "linux-riscv64",
  ],

  # Tier 3: Experimental
  tier3 = [
    "minix-x86_64",
  ],

  # ============================================================
  # PLATFORM CONTRACTS
  # ============================================================

  contracts = {
    # ========================================================
    # LINUX
    # ========================================================

    linux = {
      # Package managers (in preference order)
      package_managers = {
        primary = "guix",
        fallback = "nix",
        emergency = "apk",  # Alpine/Wolfi
      },

      # Container runtimes (in preference order)
      container_runtimes = ["nerdctl", "podman", "docker"],

      # Init systems we support
      init_systems = ["systemd", "openrc", "runit", "s6"],

      # Architectures
      architectures = {
        x86_64 = {
          tier = 1,
          guix_support = true,
          nix_support = true,
          container_support = true,
          burrito_support = true,
        },
        aarch64 = {
          tier = 1,
          guix_support = true,
          nix_support = true,
          container_support = true,
          burrito_support = true,
        },
        riscv64 = {
          tier = 2,
          guix_support = false,  # Experimental - see riscv-guix-buildsys
          nix_support = true,
          container_support = true,
          burrito_support = true,
        },
      },

      # Distribution-specific notes
      distributions = {
        fedora = { notes = "Primary development platform" },
        debian = { notes = "Well tested" },
        ubuntu = { notes = "Well tested" },
        nixos = { notes = "Native Nix support" },
        guix_system = { notes = "Native Guix support" },
        alpine = { notes = "Container base" },
      },
    },

    # ========================================================
    # MACOS
    # ========================================================

    darwin = {
      package_managers = {
        primary = "nix",
        fallback = "brew",
      },

      container_runtimes = ["podman", "docker"],

      # Code signing requirements
      code_signing = {
        required = true,
        notarization = true,
        developer_id = "required for distribution",
      },

      architectures = {
        x86_64 = {
          tier = 2,
          nix_support = true,
          burrito_support = true,
          notes = "Intel Macs (legacy)",
        },
        aarch64 = {
          tier = 1,
          nix_support = true,
          burrito_support = true,
          notes = "Apple Silicon",
        },
      },

      # macOS version requirements
      min_version = "12.0",  # Monterey
    },

    # ========================================================
    # WINDOWS
    # ========================================================

    windows = {
      package_managers = {
        primary = "winget",
        fallback = "scoop",
        nix_via_wsl = true,
      },

      container_runtimes = [
        "docker-desktop",
        "podman-desktop",
        "wsl2-nerdctl",
      ],

      # Code signing
      code_signing = {
        required = true,
        authenticode = true,
        ev_certificate = "recommended for SmartScreen",
      },

      architectures = {
        x86_64 = {
          tier = 2,
          burrito_support = true,
          wsl2_recommended = true,
        },
      },

      # Windows version requirements
      min_version = "10.0.19041",  # Windows 10 2004+
    },

    # ========================================================
    # MINIX
    # ========================================================

    minix = {
      # Minimal support - Burrito binary only
      deployment = "burrito_only",

      container_support = false,
      nix_support = false,
      guix_support = false,

      architectures = {
        x86_64 = {
          tier = 3,
          burrito_support = true,
          notes = "Experimental - static binary only",
        },
      },

      # Special build requirements
      build_notes = "Requires cross-compilation from Linux",
    },

    # ========================================================
    # IOS
    # ========================================================

    ios = {
      # Mobile framework
      framework = "dioxus",

      # Version requirements
      min_version = "15.0",

      # Code signing (mandatory for iOS)
      code_signing = {
        required = true,
        provisioning_profile = true,
        app_store_distribution = true,
      },

      # Capabilities needed
      capabilities = [
        "background-fetch",      # For sync
        "file-provider",         # For cloud drive access
        "push-notifications",    # For submission alerts
      ],

      architectures = {
        aarch64 = {
          tier = 2,
          notes = "All modern iOS devices",
        },
      },
    },

    # ========================================================
    # ANDROID
    # ========================================================

    android = {
      # Mobile framework
      framework = "dioxus",

      # SDK requirements
      min_sdk = 26,      # Android 8.0 Oreo
      target_sdk = 34,   # Android 14

      # Code signing
      code_signing = {
        required = true,
        play_store_signing = "recommended",
        keystore_required = true,
      },

      # Permissions needed
      permissions = [
        "INTERNET",
        "READ_EXTERNAL_STORAGE",
        "WRITE_EXTERNAL_STORAGE",
        "RECEIVE_BOOT_COMPLETED",
        "FOREGROUND_SERVICE",
      ],

      architectures = {
        aarch64 = {
          tier = 2,
          notes = "Primary Android architecture",
        },
        x86_64 = {
          tier = 3,
          notes = "Emulator support only",
        },
      },
    },
  },

  # ============================================================
  # BUILD MATRIX
  # ============================================================

  build_matrix = {
    # What gets built in CI
    ci_builds = [
      { platform = "linux-x86_64", method = "guix" },
      { platform = "linux-x86_64", method = "nix" },
      { platform = "linux-x86_64", method = "container" },
      { platform = "linux-aarch64", method = "nix" },
      { platform = "linux-aarch64", method = "container" },
      { platform = "darwin-aarch64", method = "nix" },
    ],

    # What gets built for releases
    release_builds = [
      { platform = "linux-x86_64", output = "container" },
      { platform = "linux-aarch64", output = "container" },
      { platform = "linux-riscv64", output = "container" },
      { platform = "linux-x86_64", output = "burrito" },
      { platform = "linux-aarch64", output = "burrito" },
      { platform = "darwin-x86_64", output = "burrito" },
      { platform = "darwin-aarch64", output = "burrito" },
      { platform = "windows-x86_64", output = "burrito" },
    ],
  },
}
