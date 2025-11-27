# SPDX-FileCopyrightText: 2024 Panoptes Contributors
# SPDX-License-Identifier: MIT

# Panoptes Configuration - Nickel Format
# https://nickel-lang.org/

# Type definitions
let OllamaConfig = {
  url | String | default = "http://localhost:11434",
  model | String | default = "moondream",
  timeout_secs | Number | default = 120,
}

let RetryConfig = {
  max_retries | Number | default = 3,
  initial_backoff_ms | Number | default = 1000,
  max_backoff_ms | Number | default = 30000,
  multiplier | Number | default = 2.0,
}

let NamingStyle = [| 'KebabCase, 'SnakeCase, 'PascalCase, 'CamelCase |]

let NamingConfig = {
  style | NamingStyle | default = 'KebabCase,
  max_length | Number | default = 50,
  prefix | String | optional,
  suffix | String | optional,
  include_date | Bool | default = false,
  date_format | String | default = "%Y-%m-%d",
  preserve_original_as_tag | Bool | default = true,
}

let TagConfig = {
  auto_generate | Bool | default = true,
  max_auto_tags | Number | default = 5,
  default_tags | Array String | default = [],
  separator | String | default = "_",
}

let WatchConfig = {
  debounce_ms | Number | default = 500,
  recursive | Bool | default = true,
  process_existing | Bool | default = false,
  ignore_patterns | Array String | default = [
    ".*",
    "*~",
    "*.tmp",
    "*.swp",
  ],
}

let WebConfig = {
  address | String | default = "127.0.0.1",
  port | Number | default = 8080,
  enable_api | Bool | default = true,
  api_key | String | optional,
}

let LogLevel = [| 'trace, 'debug, 'info, 'warn, 'error |]

let LoggingConfig = {
  level | LogLevel | default = 'info,
  file | String | optional,
  json | Bool | default = false,
}

let FilterConfig = {
  min_size | Number | optional,
  max_size | Number | optional,
  include_extensions | Array String | default = [],
  exclude_extensions | Array String | default = [],
  exclude_hidden | Bool | default = true,
  exclude_patterns | Array String | default = [],
}

# Main configuration schema
let Config = {
  # Ollama settings
  ollama | OllamaConfig | default = {},

  # Processing settings
  max_jobs | Number | default = 4,

  # Database path (null for default)
  database_path | String | optional,

  # History file path (null for default)
  history_path | String | optional,

  # Retry configuration
  retry | RetryConfig | default = {},

  # Naming configuration
  naming | NamingConfig | default = {},

  # Tag configuration
  tags | TagConfig | default = {},

  # Watch configuration
  watch | WatchConfig | default = {},

  # Web configuration
  web | WebConfig | default = {},

  # Logging configuration
  logging | LoggingConfig | default = {},

  # File filter configuration
  filter | FilterConfig | default = {},
}

# Default configuration
{
  # Override any defaults here
  ollama = {
    url = "http://localhost:11434",
    model = "moondream",
    timeout_secs = 120,
  },

  max_jobs = 4,

  retry = {
    max_retries = 3,
    initial_backoff_ms = 1000,
  },

  naming = {
    style = 'KebabCase,
    max_length = 50,
    preserve_original_as_tag = true,
  },

  tags = {
    auto_generate = true,
    max_auto_tags = 5,
  },

  watch = {
    debounce_ms = 500,
    recursive = true,
    ignore_patterns = [
      ".*",
      "*~",
      "*.tmp",
      "*.swp",
      "*.part",
      ".git",
      "node_modules",
    ],
  },

  web = {
    address = "127.0.0.1",
    port = 8080,
    enable_api = true,
  },

  logging = {
    level = 'info,
    json = false,
  },

  filter = {
    exclude_hidden = true,
    exclude_patterns = [
      "*.tmp",
      "*.bak",
      ".git/**",
      "node_modules/**",
      "target/**",
    ],
  },
} | Config
