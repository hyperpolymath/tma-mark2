# SPDX-License-Identifier: AGPL-3.0-or-later
#
# config/default.ncl - Default Configuration for tma-mark2
#
# This file defines the default configuration values.
# Override in environment-specific files or runtime.
#
# Usage: nickel export config/default.ncl --format json

let security = import "../must/contracts/security.ncl" in
let platform = import "../must/contracts/platform.ncl" in

{
  # ============================================================
  # APPLICATION IDENTITY
  # ============================================================

  app = {
    name = "tma-mark2",
    display_name = "eTMA Handler",
    version = "2.0.0",
    environment = "development",
  },

  # ============================================================
  # SERVER CONFIGURATION
  # ============================================================

  server = {
    host = "127.0.0.1",
    port = 4000,

    # Only bind to localhost unless explicitly configured
    public = false,

    # Secret key base - MUST be overridden in production
    secret_key_base = "DEVELOPMENT_ONLY_REPLACE_IN_PRODUCTION",
  },

  # ============================================================
  # DATABASE (CubDB)
  # ============================================================

  database = {
    path = "data/db",
    auto_compact = true,

    # Encryption at rest
    encryption = {
      enabled = true,
      algorithm = security.contracts.data_protection.encryption_at_rest.algorithm,
    },
  },

  # ============================================================
  # CACHE (Cachex)
  # ============================================================

  cache = {
    # Default cache TTL in seconds
    default_ttl_seconds = 3600,

    # Maximum cache entries
    max_entries = 10000,

    # Cache cleanup interval
    cleanup_interval_seconds = 600,
  },

  # ============================================================
  # OPEN UNIVERSITY INTEGRATION
  # ============================================================

  open_university = {
    # Base URLs for OU services
    endpoints = {
      learn2 = "https://learn2.open.ac.uk",
      css3 = "https://css3.open.ac.uk",
      msds = "https://msds.open.ac.uk",
    },

    # Rate limiting (requests per minute)
    rate_limit = security.contracts.network.allowed_destinations.open_university.rate_limit_requests_per_minute,

    # Session timeout (seconds)
    session_timeout = 1800,

    # Enable scraping fallback when API unavailable
    scraping_fallback = true,
  },

  # ============================================================
  # VIRUSTOTAL INTEGRATION
  # ============================================================

  virustotal = {
    enabled = false,  # Requires user consent

    # API endpoint
    endpoint = "https://www.virustotal.com",

    # Rate limiting
    rate_limit = security.contracts.network.allowed_destinations.virustotal.rate_limit_requests_per_minute,

    # Maximum file size for upload (bytes)
    max_file_size_bytes = 33554432,  # 32 MB

    # Privacy warning (must be shown to user)
    privacy_warning = security.contracts.network.allowed_destinations.virustotal.privacy_warning,
  },

  # ============================================================
  # FILE HANDLING
  # ============================================================

  files = {
    # Supported input formats
    input_formats = [".fhi", ".docx", ".pdf", ".rtf", ".odt", ".txt"],

    # Watch directory for automatic ingestion
    watch_directory = "~/Downloads",

    # Temporary file location
    temp_directory = "/tmp/tma-mark2",

    # Automatic cleanup of temp files
    temp_cleanup_hours = 24,

    # Maximum file size (bytes)
    max_file_size_bytes = 104857600,  # 100 MB
  },

  # ============================================================
  # SECURITY
  # ============================================================

  security = {
    # Enable paranoid mode (extra checks, verbose logging)
    paranoid_mode = true,

    # Crypto settings (from security contract)
    crypto = security.contracts.crypto,

    # HTTP headers (from security contract)
    http_headers = security.contracts.http_headers,

    # Network policy
    network = {
      default_policy = security.contracts.network.default_policy,
      vpn = security.contracts.network.vpn,
    },

    # Container security
    container = security.contracts.container,
  },

  # ============================================================
  # FEEDBACK MODULE (Feedback-o-tron)
  # ============================================================

  feedback = {
    # Enable the feedback system
    enabled = true,

    # Comment bank location
    comment_bank_path = "data/comment_bank",

    # Template directory
    templates_path = "data/templates",

    # Learning mode (uses Erlog/MiniKanren)
    learning = {
      enabled = true,
      min_samples = 10,
      confidence_threshold = 0.8,
    },

    # Plagiarism detection
    plagiarism = {
      enabled = true,
      similarity_threshold = 0.7,
      check_external = false,  # Requires network access
    },
  },

  # ============================================================
  # LOGGING
  # ============================================================

  logging = {
    level = "info",
    format = "json",

    # Redact sensitive fields (from security contract)
    redact = security.contracts.audit.redact,

    # Log retention
    retention_days = security.contracts.audit.retention_days,
  },

  # ============================================================
  # PLATFORM-SPECIFIC
  # ============================================================

  platform = {
    # Container runtime preference
    container_runtime = "nerdctl",

    # Package manager preference
    package_manager = "guix",
    package_manager_fallback = "nix",

    # Build targets
    targets = platform.tier1,
  },

  # ============================================================
  # UPDATES
  # ============================================================

  updates = {
    enabled = true,
    auto_check = true,
    check_interval_hours = 24,

    # Update server
    server = "https://updates.tma-mark2.org",

    # Signature verification required
    signature_required = true,
  },
}
