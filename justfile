# tma-mark2 - Elixir Development Tasks
set shell := ["bash", "-uc"]
set dotenv-load := true

project := "tma-mark2"

# Show all recipes
default:
    @just --list --unsorted

# Get dependencies
deps:
    mix deps.get

# Compile
build:
    mix compile

# Run tests
test:
    mix test

# Run tests verbose
test-verbose:
    mix test --trace

# Format code
fmt:
    mix format

# Check formatting
fmt-check:
    mix format --check-formatted

# Run credo lints
lint:
    mix credo --strict

# Clean
clean:
    mix clean

# Run the app
run:
    mix run

# Start IEx session
iex:
    iex -S mix

# Generate docs
doc:
    mix docs

# All checks before commit
pre-commit: fmt-check lint test
    @echo "All checks passed!"
