# SPDX-License-Identifier: AGPL-3.0-or-later
#
# must/contracts/security.ncl - Security Contract Definitions
#
# Defines cryptographic requirements, network policies, and
# security controls for tma-mark2

{
  contracts = {
    # ========================================================
    # CRYPTOGRAPHIC REQUIREMENTS
    # ========================================================

    crypto = {
      # Algorithms we MUST support
      required_algorithms = [
        # Hashing
        "BLAKE3",           # Primary hash (fast, secure)
        "SHAKE-256",        # XOF for key derivation
        "SHA-256",          # Compatibility (VirusTotal, etc.)

        # Signatures
        "Ed448",            # Primary signatures (Curve448)
        "Dilithium5",       # Post-quantum signatures

        # Key Exchange
        "Kyber-1024",       # Post-quantum KEM (ML-KEM Level 5)
        "X448",             # Classical ECDH fallback

        # Symmetric
        "ChaCha20-Poly1305", # AEAD cipher
        "AES-256-GCM",       # Hardware-accelerated alternative
      ],

      # Algorithms we MUST NOT use
      banned_algorithms = [
        "MD5",              # Broken
        "SHA1",             # Deprecated
        "DES",              # Broken
        "3DES",             # Deprecated
        "RC4",              # Broken
        "RSA-1024",         # Too weak
        "RSA-2048",         # Prefer Ed448/Dilithium
        "ECDSA-P256",       # Prefer Ed448
        "Blowfish",         # Deprecated
      ],

      # Minimum key sizes
      key_requirements = {
        symmetric_min_bits = 256,
        hash_min_bits = 256,
        asymmetric_min_bits = 4096,  # For classical RSA if ever used

        # Post-quantum specific
        kyber_level = "Kyber-1024",   # Level 5 (256-bit security)
        dilithium_level = "Dilithium5", # Level 5
      },

      # Key derivation
      kdf = {
        function = "BLAKE3-KDF",
        context_required = true,
        min_iterations = 1,  # BLAKE3 doesn't need iterations
      },

      # Password hashing
      password_hashing = {
        function = "Argon2id",
        min_memory_kb = 65536,    # 64 MB
        min_iterations = 3,
        min_parallelism = 4,
      },
    },

    # ========================================================
    # NETWORK SECURITY
    # ========================================================

    network = {
      # Default policy: DENY ALL
      default_policy = "deny",

      # Allowed destinations (with constraints)
      allowed_destinations = {
        virustotal = {
          hosts = ["www.virustotal.com"],
          ports = [443],
          protocol = "HTTPS",
          requires_user_consent = true,
          ephemeral = true,
          max_duration_seconds = 300,
          rate_limit_requests_per_minute = 4,
          privacy_warning = "Files may be shared publicly by VirusTotal",
        },

        open_university = {
          hosts = [
            "learn2.open.ac.uk",
            "css3.open.ac.uk",
            "msds.open.ac.uk",
            "*.open.ac.uk",
          ],
          ports = [443],
          protocol = "HTTPS",
          requires_user_consent = false,  # Core functionality
          ephemeral = true,
          max_duration_seconds = 1800,
          rate_limit_requests_per_minute = 20,
        },

        updates = {
          hosts = ["updates.tma-mark2.org", "github.com"],
          ports = [443],
          protocol = "HTTPS",
          requires_user_consent = false,
          ephemeral = true,
          max_duration_seconds = 120,
          signature_required = true,
        },
      },

      # VPN configuration
      vpn = {
        protocol = "WireGuard",
        required_for = ["sync", "institutional_network"],
        key_rotation_days = 30,
        keepalive_seconds = 25,
      },

      # mTLS requirements
      mtls = {
        required_for_sync = true,
        certificate_pinning = true,
        min_tls_version = "1.3",
      },
    },

    # ========================================================
    # CONTAINER SECURITY
    # ========================================================

    container = {
      # Base image requirements
      base_image = {
        registry = "cgr.dev/chainguard",
        image = "wolfi-base",
        rationale = "Minimal CVEs, security-hardened, SBOM included",
      },

      # Runtime security
      runtime = {
        run_as_root = false,
        user_id = 65532,
        group_id = 65532,
        read_only_rootfs = true,
        no_new_privileges = true,
      },

      # Capability controls
      capabilities = {
        drop = ["ALL"],
        add = [],  # None required
      },

      # Seccomp profile
      seccomp = {
        profile = "RuntimeDefault",
        # Could use custom profile for even stricter controls
      },

      # Resource limits
      resources = {
        memory_limit_mb = 512,
        cpu_limit = 1.0,
        pids_limit = 100,
      },
    },

    # ========================================================
    # HTTP SECURITY HEADERS
    # ========================================================

    http_headers = {
      required = [
        "Content-Security-Policy",
        "X-Content-Type-Options",
        "X-Frame-Options",
        "Referrer-Policy",
        "Permissions-Policy",
        "Strict-Transport-Security",
        "Cross-Origin-Embedder-Policy",
        "Cross-Origin-Opener-Policy",
        "Cross-Origin-Resource-Policy",
        "Cache-Control",
      ],

      # CSP policy (restrictive)
      content_security_policy = {
        default_src = "'none'",
        script_src = "'self'",
        style_src = "'self'",
        img_src = "'self' data:",
        font_src = "'self'",
        connect_src = "'self'",
        frame_ancestors = "'none'",
        form_action = "'self'",
        base_uri = "'self'",
        upgrade_insecure_requests = true,
        block_all_mixed_content = true,
      },

      # Permissions Policy (disable all browser APIs)
      permissions_policy = {
        accelerometer = [],
        camera = [],
        geolocation = [],
        microphone = [],
        payment = [],
        usb = [],
      },
    },

    # ========================================================
    # DATA PROTECTION
    # ========================================================

    data_protection = {
      # Sensitive data handling
      sensitive_fields = [
        "student_id",
        "student_name",
        "submission_content",
        "feedback_content",
        "ou_session_token",
      ],

      # Encryption at rest
      encryption_at_rest = {
        enabled = true,
        algorithm = "ChaCha20-Poly1305",
        key_derivation = "BLAKE3-KDF",
      },

      # Data retention
      retention = {
        submissions_days = 365,
        logs_days = 90,
        cache_hours = 24,
      },

      # Export formats
      export = {
        formats = ["encrypted-cbor", "encrypted-zip"],
        require_password = true,
      },
    },

    # ========================================================
    # AUDIT & LOGGING
    # ========================================================

    audit = {
      # Events to log
      log_events = [
        "network_access_granted",
        "network_access_revoked",
        "file_accessed",
        "submission_opened",
        "feedback_saved",
        "ou_login_attempt",
        "virustotal_upload",
        "crypto_key_generated",
      ],

      # Log format
      format = {
        structured = true,
        format = "json",
        include_timestamp = true,
        include_request_id = true,
      },

      # Log retention
      retention_days = 90,

      # No sensitive data in logs
      redact = [
        "password",
        "api_key",
        "session_token",
        "private_key",
      ],
    },
  },
}
