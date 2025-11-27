# This file is responsible for configuring your application
# and its dependencies with the aid of the Config module.
import Config

# Configures the endpoint
config :etma_handler, EtmaHandlerWeb.Endpoint,
  url: [host: "localhost"],
  adapter: Bandit.PhoenixAdapter,
  render_errors: [
    formats: [html: EtmaHandlerWeb.ErrorHTML, json: EtmaHandlerWeb.ErrorJSON],
    layout: false
  ],
  pubsub_server: EtmaHandler.PubSub,
  live_view: [signing_salt: "etma_handler_salt"]

# Configure esbuild (the version is required)
config :esbuild,
  version: "0.17.11",
  default: [
    args:
      ~w(js/app.js --bundle --target=es2017 --outdir=../priv/static/assets --external:/fonts/* --external:/images/*),
    cd: Path.expand("../assets", __DIR__),
    env: %{"NODE_PATH" => Path.expand("../deps", __DIR__)}
  ]

# Configure tailwind (the version is required)
config :tailwind,
  version: "3.3.2",
  default: [
    args: ~w(
      --config=tailwind.config.js
      --input=css/app.css
      --output=../priv/static/assets/app.css
    ),
    cd: Path.expand("../assets", __DIR__)
  ]

# Configures Elixir's Logger
config :logger, :console,
  format: "$time $metadata[$level] $message\n",
  metadata: [:request_id]

# Use Jason for JSON parsing in Phoenix
config :phoenix, :json_library, Jason

# eTMA Handler specific configuration
config :etma_handler,
  # The directory where student files are stored (encrypted)
  data_dir: Path.expand("~/.etma_handler/data"),
  # The directory to watch for incoming student downloads
  downloads_dir: Path.expand("~/Downloads"),
  # Default course configuration
  default_course: nil,
  # Enable/disable automatic file ingestion
  auto_ingest: true,
  # Minimum feedback word count (Quality Bot)
  min_feedback_words: 50,
  # Minimum in-text comments (Quality Bot)
  min_comments: 5

# Import environment specific config. This must remain at the bottom
# of this file so it overrides the configuration defined above.
import_config "#{config_env()}.exs"
