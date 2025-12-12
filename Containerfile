# eTMA Handler - Wolfi Container
# Secure, minimal container using Chainguard's Wolfi base
#
# Build:
#   podman build -t etma-handler:latest .
#
# Run:
#   podman run -p 4000:4000 etma-handler:latest
#
# Features:
#   - Wolfi base (secure, minimal)
#   - Multi-stage build (small final image)
#   - Non-root user (security best practice)
#   - Health check included

# ===========================================
# STAGE 1: Builder
# ===========================================
FROM cgr.dev/chainguard/wolfi-base AS builder

# Install build dependencies
RUN apk add --no-cache --no-cache \
    elixir \
    erlang \
    erlang-dev \
    git \
    build-base \
    nodejs \
    npm

WORKDIR /app

# Install Hex and Rebar
RUN mix local.hex --force && \
    mix local.rebar --force

# Set build environment
ENV MIX_ENV=prod

# Cache dependencies (mix.lock generated during build if not present)
COPY mix.exs ./
RUN mix deps.get --only $MIX_ENV

# Copy config (needed for deps.compile)
COPY config config

# Compile dependencies
RUN mix deps.compile

# Copy application code
COPY lib lib
COPY priv priv
COPY assets assets

# Install Node dependencies and build assets
WORKDIR /app/assets
RUN npm install

WORKDIR /app

# Build assets
RUN mix assets.deploy

# Compile application
RUN mix compile

# Build release
RUN mix release

# ===========================================
# STAGE 2: Runner
# ===========================================
FROM cgr.dev/chainguard/wolfi-base AS runner

# Install runtime dependencies
RUN apk add --no-cache --no-cache \
    libstdc++ \
    ncurses \
    openssl

WORKDIR /app

# Create non-root user for security
RUN addgroup -S etma && adduser -S etma -G etma

# Copy release from builder
COPY --from=builder --chown=etma:etma /app/_build/prod/rel/etma_handler ./

# Create data directory
RUN mkdir -p /app/data && chown etma:etma /app/data

# Switch to non-root user
USER etma

# Environment configuration
ENV HOME=/app \
    PORT=4000 \
    PHX_HOST=localhost \
    MIX_ENV=prod \
    ETMA_DATA_DIR=/app/data

# Expose port
EXPOSE 4000

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:4000/api/health || exit 1

# Start the application
CMD ["bin/etma_handler", "start"]
