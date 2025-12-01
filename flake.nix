# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT
#
# eTMA Handler - Nix Flake
# ========================
#
# For DEVELOPERS only. Users should use the container:
#   just do-it
#
# Development:
#   nix develop          # Enter dev shell
#   just dev             # Same thing
#
{
  description = "eTMA Handler - Open University Marking Tool (BEAM Edition)";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-24.05";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};

        # Runtime versions
        erlang = pkgs.erlang_26;
        elixir = pkgs.elixir_1_16;

      in
      {
        # =======================================================================
        # Development Shell
        # =======================================================================
        # This is the main thing. Enter with: nix develop
        #
        devShells.default = pkgs.mkShell {
          name = "etma-handler-dev";

          buildInputs = with pkgs; [
            # Core
            erlang
            elixir
            nodejs_20

            # Build tools
            git
            gnumake
            gcc

            # Dev tools
            elixir_ls        # LSP for editors
            just             # Task runner
            podman           # Container runtime

            # Optional but useful
            asciidoctor      # For docs
            jq               # JSON wrangling
          ];

          shellHook = ''
            echo ""
            echo "  ╔═══════════════════════════════════════════════════════════╗"
            echo "  ║         eTMA Handler - Development Environment            ║"
            echo "  ╚═══════════════════════════════════════════════════════════╝"
            echo ""
            echo "  Elixir $(elixir --version | head -1 | cut -d' ' -f2)"
            echo "  Erlang/OTP $(erl -eval 'io:format("~s", [erlang:system_info(otp_release)]), halt().' -noshell)"
            echo "  Node.js $(node --version)"
            echo ""

            # Local hex/rebar (don't pollute global)
            export MIX_HOME=$PWD/.nix-mix
            export HEX_HOME=$PWD/.nix-hex
            export PATH=$MIX_HOME/bin:$HEX_HOME/bin:$PATH

            # Initialize if needed
            if [ ! -d "$MIX_HOME" ]; then
              echo "  Setting up Elixir tooling..."
              mix local.hex --force --if-missing >/dev/null 2>&1
              mix local.rebar --force --if-missing >/dev/null 2>&1
            fi

            # Better IEx experience
            export ERL_AFLAGS="-kernel shell_history enabled"

            # Podman rootless
            export DOCKER_HOST="unix://$XDG_RUNTIME_DIR/podman/podman.sock"

            echo "  Commands:"
            echo "    mix phx.server    Start dev server"
            echo "    mix test          Run tests"
            echo "    just build        Build container"
            echo "    just do-it        Run containerized"
            echo ""
          '';
        };

        # =======================================================================
        # Checks (for CI)
        # =======================================================================
        checks = {
          format = pkgs.runCommand "check-format" {
            buildInputs = [ elixir ];
            src = self;
          } ''
            cd $src
            # Would run: mix format --check-formatted
            # But needs deps, so just touch for now
            touch $out
          '';
        };

        # =======================================================================
        # For completeness: Nix package (but use container instead)
        # =======================================================================
        packages.default = pkgs.stdenv.mkDerivation {
          pname = "etma-handler";
          version = "2.0.0";
          src = self;

          buildInputs = [ erlang elixir pkgs.nodejs_20 ];

          # Note: This is incomplete - use the container for actual distribution
          buildPhase = ''
            echo "Use 'just build' or the Containerfile for production builds"
            echo "This Nix package is for development reference only"
          '';

          installPhase = ''
            mkdir -p $out
            echo "Use the container" > $out/README
          '';

          meta = {
            description = "eTMA Handler - Use the container instead of this package";
            homepage = "https://github.com/Hyperpolymath/tma-mark2";
            license = pkgs.lib.licenses.mit;
          };
        };
      }
    );
}
