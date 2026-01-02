# SPDX-License-Identifier: AGPL-3.0-or-later
#
# Nix Flake for tma-mark2
# Fallback when Guix is unavailable
#
# Usage:
#   nix build .#default
#   nix develop
#   nix run .#default
#
# Multi-arch:
#   nix build .#packages.x86_64-linux.default
#   nix build .#packages.aarch64-linux.default
#   nix build .#packages.riscv64-linux.default

{
  description = "tma-mark2 - eTMA Handler for Open University Tutors";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-24.11";
    flake-utils.url = "github:numtide/flake-utils";
    rust-overlay = {
      url = "github:oxalica/rust-overlay";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs = { self, nixpkgs, flake-utils, rust-overlay }:
    flake-utils.lib.eachSystem [
      "x86_64-linux"
      "aarch64-linux"
      "riscv64-linux"
      "x86_64-darwin"
      "aarch64-darwin"
    ] (system:
      let
        overlays = [ (import rust-overlay) ];
        pkgs = import nixpkgs {
          inherit system overlays;
          config.allowUnfree = false;  # FOSS only
        };

        # Rust toolchain for NIFs
        rustToolchain = pkgs.rust-bin.stable.latest.default.override {
          extensions = [ "rust-src" "rust-analyzer" ];
          targets = [ "wasm32-unknown-unknown" ];  # For future WASM plugins
        };

        # Elixir with specific OTP version
        beamPackages = pkgs.beam.packages.erlang_27;
        elixir = beamPackages.elixir_1_17;
        erlang = beamPackages.erlang;

        # Build inputs shared between package and devShell
        commonBuildInputs = with pkgs; [
          openssl
          ncurses
          zlib
        ];

        # Security tools (runtime)
        securityInputs = with pkgs; [
          clamav
          wireguard-tools
          nftables
        ];

        # Document processing (runtime)
        docInputs = with pkgs; [
          tesseract
          poppler_utils
        ];

      in {
        packages = {
          default = pkgs.stdenv.mkDerivation {
            pname = "tma-mark2";
            version = "2.0.0";

            src = ./.;

            nativeBuildInputs = with pkgs; [
              elixir
              erlang
              rustToolchain
              pkg-config
              git
            ];

            buildInputs = commonBuildInputs ++ securityInputs ++ docInputs;

            # Disable networking during build
            __noChroot = false;

            configurePhase = ''
              export HOME=$TMPDIR
              export MIX_ENV=prod
              export MIX_HOME=$TMPDIR/.mix
              export HEX_HOME=$TMPDIR/.hex

              # Install Hex and Rebar
              mix local.hex --force
              mix local.rebar --force
            '';

            buildPhase = ''
              # Build Rust NIFs if present
              if [ -d native/tma_crypto ]; then
                cargo build --release --manifest-path native/tma_crypto/Cargo.toml
              fi

              if [ -d native/tma_nlp ]; then
                cargo build --release --manifest-path native/tma_nlp/Cargo.toml
              fi

              # Build Elixir application
              mix deps.get --only prod
              mix deps.compile
              mix compile

              # Build assets if they exist
              if [ -d assets ]; then
                mix assets.deploy || true
              fi

              # Create release
              mix release
            '';

            installPhase = ''
              mkdir -p $out
              cp -r _build/prod/rel/etma_handler/* $out/

              # Create wrapper script
              mkdir -p $out/bin
              cat > $out/bin/tma-mark2 << 'EOF'
              #!/bin/sh
              exec "$(dirname "$0")/../bin/etma_handler" "$@"
              EOF
              chmod +x $out/bin/tma-mark2
            '';

            meta = with pkgs.lib; {
              description = "eTMA Handler - Open University Marking Tool";
              homepage = "https://github.com/hyperpolymath/tma-mark2";
              license = licenses.agpl3Plus;
              maintainers = [ ];
              platforms = platforms.unix;
            };
          };

          # Container image
          container = pkgs.dockerTools.buildLayeredImage {
            name = "ghcr.io/hyperpolymath/tma-mark2";
            tag = "latest";

            contents = [
              self.packages.${system}.default
              pkgs.cacert
              pkgs.clamav
              pkgs.tesseract
              pkgs.wireguard-tools
            ];

            config = {
              Cmd = [ "/bin/etma_handler" "start" ];
              ExposedPorts = { "4000/tcp" = {}; };
              Env = [
                "PORT=4000"
                "MIX_ENV=prod"
                "ETMA_DATA_DIR=/data"
              ];
              Volumes = { "/data" = {}; };
              User = "65532:65532";
            };
          };
        };

        # Development shell
        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            # Elixir/Erlang
            elixir
            erlang
            erlang-ls

            # Rust
            rustToolchain
            rust-analyzer

            # Build tools
            pkg-config
            gnumake

            # Security tools (for testing)
            clamav

            # Document processing (for testing)
            tesseract
            poppler_utils

            # Nickel (for mustfile)
            nickel

            # Just (task runner)
            just
          ] ++ commonBuildInputs;

          shellHook = ''
            echo "========================================="
            echo "  tma-mark2 Development Environment"
            echo "========================================="
            echo ""
            echo "Build: Nix (fallback mode)"
            echo "Note: Guix is preferred when available"
            echo ""
            echo "Commands:"
            echo "  just --list     Show available tasks"
            echo "  mix phx.server  Start development server"
            echo "  nix build       Build release"
            echo ""

            export MIX_HOME=$PWD/.mix
            export HEX_HOME=$PWD/.hex

            # Install Hex/Rebar if not present
            if [ ! -d "$MIX_HOME" ]; then
              mix local.hex --force
              mix local.rebar --force
            fi
          '';
        };

        # Checks
        checks = {
          build = self.packages.${system}.default;

          # Formatting check
          format = pkgs.runCommand "check-format" {
            buildInputs = [ elixir rustToolchain ];
          } ''
            cd ${./.}
            mix format --check-formatted
            cargo fmt --manifest-path native/tma_crypto/Cargo.toml -- --check || true
            touch $out
          '';
        };
      }
    );
}
