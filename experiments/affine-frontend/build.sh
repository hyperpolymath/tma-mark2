#!/usr/bin/env bash
# eTMA Handler - AffineScript Build Script
# SPDX-License-Identifier: MPL-2.0
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# Script lives at experiments/affine-frontend/build.sh — walk up two levels
# to reach the project root.
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
OUT_DIR="$PROJECT_ROOT/priv/static/wasm"
OUT_WASM="$OUT_DIR/etma_handler_frontend.wasm"
TMP_WASM="$OUT_DIR/.etma_handler_frontend.wasm.tmp"

mkdir -p "$OUT_DIR"

find_affinescript_repo() {
  local candidate
  for candidate in \
    "${AFFINESCRIPT_REPO:-}" \
    "$PROJECT_ROOT/../nextgen-languages/affinescript" \
    "$PROJECT_ROOT/../../nextgen-languages/affinescript" \
    "/var/mnt/eclipse/repos/nextgen-languages/affinescript"
  do
    [ -n "$candidate" ] || continue
    if [ -d "$candidate" ]; then
      printf '%s\n' "$candidate"
      return 0
    fi
  done
  return 1
}

compile_with_affinescript() {
  local compiler_repo="$1"
  rm -f "$TMP_WASM"
  
  if command -v affinescript >/dev/null 2>&1; then
    echo "Using affinescript from PATH (WASM GC)"
    affinescript compile "$SCRIPT_DIR/main.affine" --wasm-gc -o "$TMP_WASM"
    return $?
  fi

  if [ -n "$compiler_repo" ] && [ -x "$compiler_repo/_build/default/bin/main.exe" ]; then
    echo "Using affinescript from $compiler_repo/_build/default/bin/main.exe (WASM GC)"
    "$compiler_repo/_build/default/bin/main.exe" compile "$SCRIPT_DIR/main.affine" --wasm-gc -o "$TMP_WASM"
    return $?
  fi

  if [ -n "$compiler_repo" ]; then
    echo "Using dune exec affinescript from $compiler_repo (WASM GC)"
    ( cd "$compiler_repo" && dune exec affinescript -- compile "$SCRIPT_DIR/main.affine" --wasm-gc -o "$TMP_WASM" )
    return $?
  fi

  return 1
}

if REPO_PATH="$(find_affinescript_repo)"; then
  if ! compile_with_affinescript "$REPO_PATH"; then
    echo "AffineScript compilation failed." >&2
    exit 1
  fi
else
  echo "affinescript compiler not found." >&2
  exit 1
fi

mv "$TMP_WASM" "$OUT_WASM"
echo "Wrote $OUT_WASM"
