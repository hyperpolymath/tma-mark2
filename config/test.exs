import Config

# We don't run a server during test
config :etma_handler, EtmaHandlerWeb.Endpoint,
  http: [ip: {127, 0, 0, 1}, port: 4002],
  secret_key_base: "test_secret_key_base_for_testing_only_do_not_use_in_production_environment_please",
  server: false

# Print only warnings and errors during test
config :logger, level: :warning

# Initialize plugs at runtime for faster test compilation
config :phoenix, :plug_init_mode, :runtime

# Test-specific configuration
config :etma_handler,
  data_dir: Path.expand("../priv/test_data", __DIR__),
  downloads_dir: Path.expand("../priv/test_downloads", __DIR__),
  auto_ingest: false
