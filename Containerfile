# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT
#
# eTMA Handler - Container Image
# ==============================
#
# The container IS the distribution. User needs only Podman.
#
# Build:   podman build -t etma-handler .
# Run:     podman run -p 4000:4000 -v ~/.local/share/etma_handler:/data etma-handler
# Or:      just do-it
#
# Multi-arch build:
#   podman build --platform linux/amd64,linux/arm64 -t etma-handler .
#

# =============================================================================
# Stage 1: Build Environment
# =============================================================================
FROM cgr.dev/chainguard/wolfi-base:latest AS builder

# Install ALL build dependencies
# This layer is cached, so adding everything here is fine
RUN apk add --no-cache \
    erlang-26 \
    elixir \
    nodejs-20 \
    npm \
    git \
    build-base \
    ca-certificates

# Set build environment
ENV MIX_ENV=prod \
    MIX_HOME=/opt/mix \
    HEX_HOME=/opt/hex \
    LANG=C.UTF-8 \
    ERL_FLAGS="+JPperf true"

WORKDIR /build

# Install Elixir tooling (cached layer)
RUN mix local.hex --force && \
    mix local.rebar --force

# Copy dependency manifests first (for Docker layer caching)
COPY mix.exs mix.lock ./
COPY config config

# Fetch and compile dependencies (cached until mix.lock changes)
RUN mix deps.get --only prod && \
    mix deps.compile

# Copy assets and build them
COPY assets assets
COPY priv priv
RUN mix assets.deploy

# Copy application source
COPY lib lib

# Compile and build release
RUN mix compile --warnings-as-errors && \
    mix release

# =============================================================================
# Stage 2: Runtime (Minimal)
# =============================================================================
FROM cgr.dev/chainguard/wolfi-base:latest AS runtime

# Install ONLY runtime dependencies (minimal attack surface)
RUN apk add --no-cache \
    ncurses-libs \
    libstdc++ \
    openssl \
    ca-certificates \
    tini

# Create non-root user
RUN addgroup -g 1000 etma && \
    adduser -u 1000 -G etma -h /app -D etma

WORKDIR /app

# Copy release from builder (owned by etma user)
COPY --from=builder --chown=etma:etma /build/_build/prod/rel/etma_handler ./

# Create data directory with correct permissions
RUN mkdir -p /data && chown etma:etma /data

# Switch to non-root user
USER etma

# Runtime configuration
ENV PHX_HOST=0.0.0.0 \
    PHX_PORT=4000 \
    PHX_SERVER=true \
    ETMA_DATA_DIR=/data \
    LANG=C.UTF-8 \
    RELEASE_TMP=/tmp \
    # Disable BEAM distribution (not needed, reduces attack surface)
    RELEASE_DISTRIBUTION=none

# Expose the web port
EXPOSE 4000

# Health check endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:4000/health || exit 1

# Mount point for persistent data
VOLUME ["/data"]

# OCI Image Labels (for registries and tooling)
LABEL org.opencontainers.image.title="eTMA Handler" \
      org.opencontainers.image.description="Open University Marking Tool - Mark assignments without the pain" \
      org.opencontainers.image.version="2.0.0" \
      org.opencontainers.image.vendor="eTMA Handler Contributors" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.source="https://github.com/Hyperpolymath/tma-mark2" \
      org.opencontainers.image.documentation="https://github.com/Hyperpolymath/tma-mark2#readme" \
      org.opencontainers.image.url="https://github.com/Hyperpolymath/tma-mark2"

# Use tini as init system (proper signal handling, zombie reaping)
ENTRYPOINT ["/sbin/tini", "--"]

# Start the application
CMD ["bin/etma_handler", "start"]
