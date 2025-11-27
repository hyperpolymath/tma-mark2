// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! Panoptes - AI-powered local file scanner
//!
//! A comprehensive file organization tool using vision models via Ollama.

#![forbid(unsafe_code)]
#![warn(clippy::all, clippy::pedantic, clippy::nursery)]

use std::path::PathBuf;

use clap::{Parser, Subcommand, ValueEnum, Args};
use clap_complete::Shell;
use colored::Colorize;
use tracing::{info, warn, error, Level};
use tracing_subscriber::{fmt, EnvFilter};

mod analyzers;
mod config;
mod db;
mod error;
mod history;
mod ollama;
mod watcher;

#[cfg(feature = "web")]
mod web;

use crate::config::AppConfig;
use crate::error::Result;

/// Panoptes - AI-powered local file scanner
///
/// Intelligent file organization using vision models via Ollama.
/// All processing happens locally with no cloud dependencies.
#[derive(Parser, Debug)]
#[command(
    name = "panoptes",
    author = "Panoptes Contributors",
    version = env!("CARGO_PKG_VERSION"),
    about = "AI-powered local file scanner for intelligent file organization",
    long_about = None,
    propagate_version = true,
    arg_required_else_help = true,
    after_help = "For more information, see: https://github.com/panoptes/panoptes"
)]
struct Cli {
    /// Configuration file path
    #[arg(
        short = 'c',
        long = "config",
        value_name = "FILE",
        global = true,
        env = "PANOPTES_CONFIG"
    )]
    config: Option<PathBuf>,

    /// Increase verbosity level (-v, -vv, -vvv)
    #[arg(
        short = 'v',
        long = "verbose",
        action = clap::ArgAction::Count,
        global = true
    )]
    verbose: u8,

    /// Suppress all output except errors
    #[arg(short = 'q', long = "quiet", global = true)]
    quiet: bool,

    /// Output results as JSON
    #[arg(long = "json", global = true)]
    json: bool,

    /// Show what would be done without making changes
    #[arg(long = "dry-run", global = true)]
    dry_run: bool,

    /// Disable colored output
    #[arg(long = "no-color", global = true, env = "NO_COLOR")]
    no_color: bool,

    /// Number of parallel processing jobs
    #[arg(
        short = 'j',
        long = "jobs",
        value_name = "N",
        default_value = "4",
        global = true,
        env = "PANOPTES_JOBS"
    )]
    jobs: usize,

    /// Ollama API URL
    #[arg(
        long = "ollama-url",
        value_name = "URL",
        default_value = "http://localhost:11434",
        global = true,
        env = "OLLAMA_HOST"
    )]
    ollama_url: String,

    /// Database file path
    #[arg(
        long = "database",
        value_name = "FILE",
        global = true,
        env = "PANOPTES_DATABASE"
    )]
    database: Option<PathBuf>,

    #[command(subcommand)]
    command: Commands,
}

#[derive(Subcommand, Debug)]
enum Commands {
    /// Scan files in directories
    #[command(visible_aliases = ["s", "analyze"])]
    Scan(ScanArgs),

    /// Watch directories for new files
    #[command(visible_aliases = ["w", "monitor"])]
    Watch(WatchArgs),

    /// Rename files based on AI analysis
    #[command(visible_aliases = ["r", "mv"])]
    Rename(RenameArgs),

    /// Manage file tags
    #[command(visible_aliases = ["t"])]
    Tag(TagArgs),

    /// Search files by tags or content
    #[command(visible_aliases = ["find", "query"])]
    Search(SearchArgs),

    /// View and manage operation history
    #[command(visible_aliases = ["hist", "log"])]
    History(HistoryArgs),

    /// Undo recent operations
    #[command(visible_aliases = ["u", "revert"])]
    Undo(UndoArgs),

    /// Manage configuration
    #[command(visible_aliases = ["cfg"])]
    Config(ConfigArgs),

    /// Database operations
    #[command(visible_aliases = ["database"])]
    Db(DbArgs),

    /// Start web dashboard
    #[cfg(feature = "web")]
    #[command(visible_aliases = ["serve", "dashboard"])]
    Web(WebArgs),

    /// Generate shell completions
    #[command(visible_aliases = ["complete"])]
    Shell(ShellArgs),

    /// Show system information
    #[command(visible_aliases = ["info", "status"])]
    System(SystemArgs),
}

// ============================================================================
// Scan Command Arguments
// ============================================================================

#[derive(Args, Debug)]
struct ScanArgs {
    /// Directories or files to scan
    #[arg(required = true, num_args = 1..)]
    paths: Vec<PathBuf>,

    /// Scan subdirectories recursively
    #[arg(short = 'r', long = "recursive")]
    recursive: bool,

    /// Rename files after analysis
    #[arg(short = 'R', long = "rename")]
    rename: bool,

    /// Auto-tag files based on analysis
    #[arg(short = 't', long = "tag")]
    tag: bool,

    /// Find and report duplicate files
    #[arg(short = 'd', long = "dedupe")]
    dedupe: bool,

    /// Minimum file size in bytes
    #[arg(long = "min-size", value_name = "BYTES", default_value = "1024")]
    min_size: u64,

    /// Maximum file size in bytes
    #[arg(long = "max-size", value_name = "BYTES", default_value = "104857600")]
    max_size: u64,

    /// Filter by file extensions (comma-separated)
    #[arg(short = 'e', long = "ext", value_name = "EXT", value_delimiter = ',')]
    extensions: Option<Vec<String>>,

    /// Exclude patterns (glob syntax)
    #[arg(long = "exclude", value_name = "PATTERN", action = clap::ArgAction::Append)]
    exclude: Vec<String>,

    /// Include patterns (glob syntax)
    #[arg(long = "include", value_name = "PATTERN", action = clap::ArgAction::Append)]
    include: Vec<String>,

    /// Files modified since date (YYYY-MM-DD or relative like "7d")
    #[arg(long = "since", value_name = "DATE")]
    since: Option<String>,

    /// Files modified until date (YYYY-MM-DD or relative)
    #[arg(long = "until", value_name = "DATE")]
    until: Option<String>,

    /// Output format
    #[arg(short = 'o', long = "output", value_name = "FORMAT", default_value = "table")]
    output: OutputFormat,

    /// Sort results by field
    #[arg(long = "sort", value_name = "FIELD")]
    sort: Option<SortField>,

    /// Limit number of results
    #[arg(long = "limit", value_name = "N")]
    limit: Option<usize>,

    /// Vision model to use for image analysis
    #[arg(long = "model", value_name = "MODEL", default_value = "moondream")]
    model: String,

    /// Skip already analyzed files
    #[arg(long = "skip-analyzed")]
    skip_analyzed: bool,

    /// Force re-analysis of all files
    #[arg(long = "force")]
    force: bool,

    /// Show progress bar
    #[arg(long = "progress", default_value = "true")]
    progress: bool,
}

#[derive(ValueEnum, Clone, Debug, Default)]
enum OutputFormat {
    #[default]
    Table,
    Json,
    Csv,
    Jsonl,
    Yaml,
    Plain,
}

#[derive(ValueEnum, Clone, Debug)]
enum SortField {
    Name,
    Size,
    Date,
    Type,
    Score,
}

// ============================================================================
// Watch Command Arguments
// ============================================================================

#[derive(Args, Debug)]
struct WatchArgs {
    /// Directories to watch
    #[arg(required = true, num_args = 1..)]
    paths: Vec<PathBuf>,

    /// Automatically rename new files
    #[arg(long = "auto-rename")]
    auto_rename: bool,

    /// Automatically tag new files
    #[arg(long = "auto-tag")]
    auto_tag: bool,

    /// Debounce interval in milliseconds
    #[arg(long = "debounce", value_name = "MS", default_value = "2000")]
    debounce: u64,

    /// Use polling instead of native filesystem events
    #[arg(long = "poll")]
    poll: bool,

    /// Polling interval in seconds (when --poll is used)
    #[arg(long = "poll-interval", value_name = "SECONDS", default_value = "5")]
    poll_interval: u64,

    /// Run as background daemon
    #[arg(long = "daemon", short = 'D')]
    daemon: bool,

    /// PID file for daemon mode
    #[arg(long = "pid-file", value_name = "FILE")]
    pid_file: Option<PathBuf>,

    /// Log file for daemon mode
    #[arg(long = "log-file", value_name = "FILE")]
    log_file: Option<PathBuf>,

    /// Watch subdirectories recursively
    #[arg(short = 'r', long = "recursive", default_value = "true")]
    recursive: bool,

    /// File extensions to watch (comma-separated, empty = all)
    #[arg(short = 'e', long = "ext", value_delimiter = ',')]
    extensions: Option<Vec<String>>,

    /// Patterns to ignore (glob syntax)
    #[arg(long = "ignore", action = clap::ArgAction::Append)]
    ignore: Vec<String>,
}

// ============================================================================
// Rename Command Arguments
// ============================================================================

#[derive(Args, Debug)]
struct RenameArgs {
    /// Files to rename
    #[arg(required = true, num_args = 1..)]
    files: Vec<PathBuf>,

    /// Custom prompt for AI
    #[arg(short = 'p', long = "prompt", value_name = "PROMPT")]
    prompt: Option<String>,

    /// Naming template (e.g., "{date}_{description}")
    #[arg(long = "template", value_name = "TEMPLATE")]
    template: Option<String>,

    /// Maximum filename length
    #[arg(long = "max-length", value_name = "N", default_value = "100")]
    max_length: usize,

    /// Convert to lowercase
    #[arg(long = "lowercase")]
    lowercase: bool,

    /// Replace spaces with this character
    #[arg(long = "replace-spaces", value_name = "CHAR")]
    replace_spaces: Option<char>,

    /// Preserve original extension
    #[arg(long = "preserve-ext", default_value = "true")]
    preserve_ext: bool,

    /// Add prefix to filename
    #[arg(long = "prefix", value_name = "PREFIX")]
    prefix: Option<String>,

    /// Add suffix to filename (before extension)
    #[arg(long = "suffix", value_name = "SUFFIX")]
    suffix: Option<String>,

    /// Confirm each rename interactively
    #[arg(short = 'i', long = "interactive")]
    interactive: bool,

    /// Skip files that already have good names
    #[arg(long = "skip-good")]
    skip_good: bool,
}

// ============================================================================
// Tag Command Arguments
// ============================================================================

#[derive(Args, Debug)]
struct TagArgs {
    #[command(subcommand)]
    action: TagAction,
}

#[derive(Subcommand, Debug)]
enum TagAction {
    /// Add tags to files
    Add {
        /// Files to tag
        #[arg(required = true, num_args = 1..)]
        files: Vec<PathBuf>,

        /// Tags to add (comma-separated)
        #[arg(short = 't', long = "tags", value_delimiter = ',', required = true)]
        tags: Vec<String>,
    },

    /// Remove tags from files
    Remove {
        /// Files to untag
        #[arg(required = true, num_args = 1..)]
        files: Vec<PathBuf>,

        /// Tags to remove (comma-separated, empty = all)
        #[arg(short = 't', long = "tags", value_delimiter = ',')]
        tags: Option<Vec<String>>,
    },

    /// List all tags
    List {
        /// Show tag usage counts
        #[arg(long = "counts")]
        counts: bool,

        /// Filter tags by pattern
        #[arg(long = "filter", value_name = "PATTERN")]
        filter: Option<String>,
    },

    /// Show tags for files
    Show {
        /// Files to show tags for
        #[arg(required = true, num_args = 1..)]
        files: Vec<PathBuf>,
    },

    /// Create a new tag
    Create {
        /// Tag name
        name: String,

        /// Tag color (hex)
        #[arg(long = "color", value_name = "HEX")]
        color: Option<String>,

        /// Tag description
        #[arg(long = "description", value_name = "DESC")]
        description: Option<String>,
    },

    /// Delete a tag
    Delete {
        /// Tag name to delete
        name: String,

        /// Force deletion even if tag is in use
        #[arg(long = "force")]
        force: bool,
    },

    /// Auto-tag files using AI
    Auto {
        /// Files to auto-tag
        #[arg(required = true, num_args = 1..)]
        files: Vec<PathBuf>,

        /// Maximum tags per file
        #[arg(long = "max-tags", default_value = "5")]
        max_tags: usize,
    },
}

// ============================================================================
// Search Command Arguments
// ============================================================================

#[derive(Args, Debug)]
struct SearchArgs {
    /// Search query
    query: String,

    /// Search in tags only
    #[arg(long = "tags-only")]
    tags_only: bool,

    /// Search in filenames only
    #[arg(long = "names-only")]
    names_only: bool,

    /// Filter by file type
    #[arg(long = "type", value_name = "TYPE")]
    file_type: Option<String>,

    /// Filter by tags (comma-separated, AND logic)
    #[arg(long = "has-tags", value_delimiter = ',')]
    has_tags: Option<Vec<String>>,

    /// Exclude files with these tags
    #[arg(long = "without-tags", value_delimiter = ',')]
    without_tags: Option<Vec<String>>,

    /// Filter by minimum size
    #[arg(long = "min-size", value_name = "BYTES")]
    min_size: Option<u64>,

    /// Filter by maximum size
    #[arg(long = "max-size", value_name = "BYTES")]
    max_size: Option<u64>,

    /// Search in specific directories
    #[arg(long = "in", value_name = "DIR", action = clap::ArgAction::Append)]
    directories: Vec<PathBuf>,

    /// Output format
    #[arg(short = 'o', long = "output", default_value = "table")]
    output: OutputFormat,

    /// Limit results
    #[arg(long = "limit", value_name = "N")]
    limit: Option<usize>,
}

// ============================================================================
// History Command Arguments
// ============================================================================

#[derive(Args, Debug)]
struct HistoryArgs {
    /// Show history for specific file
    #[arg(long = "file", value_name = "PATH")]
    file: Option<PathBuf>,

    /// Show only operations of specific type
    #[arg(long = "type", value_name = "TYPE")]
    operation_type: Option<String>,

    /// Show operations since date
    #[arg(long = "since", value_name = "DATE")]
    since: Option<String>,

    /// Show operations until date
    #[arg(long = "until", value_name = "DATE")]
    until: Option<String>,

    /// Limit number of entries
    #[arg(short = 'n', long = "limit", value_name = "N", default_value = "50")]
    limit: usize,

    /// Export history to file
    #[arg(long = "export", value_name = "FILE")]
    export: Option<PathBuf>,

    /// Import history from file
    #[arg(long = "import", value_name = "FILE")]
    import: Option<PathBuf>,

    /// Replay operations from history
    #[arg(long = "replay")]
    replay: bool,

    /// Clear history (requires confirmation)
    #[arg(long = "clear")]
    clear: bool,

    /// Output format
    #[arg(short = 'o', long = "output", default_value = "table")]
    output: OutputFormat,
}

// ============================================================================
// Undo Command Arguments
// ============================================================================

#[derive(Args, Debug)]
struct UndoArgs {
    /// Number of operations to undo
    #[arg(long = "count", value_name = "N", default_value = "1")]
    count: usize,

    /// Undo specific operation by ID
    #[arg(long = "id", value_name = "ID")]
    id: Option<String>,

    /// Undo all operations for specific file
    #[arg(long = "file", value_name = "PATH")]
    file: Option<PathBuf>,

    /// Undo operations since timestamp
    #[arg(long = "since", value_name = "DATE")]
    since: Option<String>,

    /// Require confirmation for each undo
    #[arg(short = 'i', long = "interactive")]
    interactive: bool,
}

// ============================================================================
// Config Command Arguments
// ============================================================================

#[derive(Args, Debug)]
struct ConfigArgs {
    #[command(subcommand)]
    action: ConfigAction,
}

#[derive(Subcommand, Debug)]
enum ConfigAction {
    /// Show current configuration
    Show {
        /// Show only specific key
        #[arg(value_name = "KEY")]
        key: Option<String>,

        /// Output format
        #[arg(short = 'o', long = "output", default_value = "yaml")]
        output: OutputFormat,
    },

    /// Set configuration value
    Set {
        /// Configuration key
        key: String,

        /// Configuration value
        value: String,
    },

    /// Reset configuration to defaults
    Reset {
        /// Reset only specific key
        #[arg(value_name = "KEY")]
        key: Option<String>,

        /// Skip confirmation
        #[arg(long = "force")]
        force: bool,
    },

    /// Validate configuration file
    Validate {
        /// Configuration file to validate
        #[arg(value_name = "FILE")]
        file: Option<PathBuf>,
    },

    /// Initialize configuration file
    Init {
        /// Output path
        #[arg(value_name = "FILE")]
        path: Option<PathBuf>,

        /// Output format (json or ncl)
        #[arg(long = "format", default_value = "json")]
        format: String,

        /// Overwrite existing file
        #[arg(long = "force")]
        force: bool,
    },

    /// Show configuration file locations
    Paths,
}

// ============================================================================
// Database Command Arguments
// ============================================================================

#[derive(Args, Debug)]
struct DbArgs {
    #[command(subcommand)]
    action: DbAction,
}

#[derive(Subcommand, Debug)]
enum DbAction {
    /// Show database statistics
    Stats,

    /// Create database backup
    Backup {
        /// Output file
        #[arg(value_name = "FILE")]
        output: Option<PathBuf>,
    },

    /// Restore from backup
    Restore {
        /// Backup file to restore
        file: PathBuf,

        /// Skip confirmation
        #[arg(long = "force")]
        force: bool,
    },

    /// Export database to file
    Export {
        /// Output file
        #[arg(value_name = "FILE")]
        output: Option<PathBuf>,

        /// Export format (json, csv, sqlite)
        #[arg(short = 'f', long = "format", default_value = "json")]
        format: String,
    },

    /// Import data into database
    Import {
        /// Input file
        file: PathBuf,

        /// Merge with existing data
        #[arg(long = "merge")]
        merge: bool,
    },

    /// Optimize database
    Optimize {
        /// Also vacuum database
        #[arg(long = "vacuum")]
        vacuum: bool,
    },

    /// Reset database (delete all data)
    Reset {
        /// Skip confirmation
        #[arg(long = "force")]
        force: bool,
    },

    /// Run database migrations
    Migrate,
}

// ============================================================================
// Web Command Arguments
// ============================================================================

#[cfg(feature = "web")]
#[derive(Args, Debug)]
struct WebArgs {
    /// Host to bind to
    #[arg(long = "host", value_name = "HOST", default_value = "127.0.0.1")]
    host: String,

    /// Port to listen on
    #[arg(short = 'p', long = "port", value_name = "PORT", default_value = "8080")]
    port: u16,

    /// Open browser automatically
    #[arg(long = "open")]
    open: bool,

    /// Run as daemon
    #[arg(long = "daemon")]
    daemon: bool,

    /// Enable API only (no UI)
    #[arg(long = "api-only")]
    api_only: bool,

    /// Enable CORS for development
    #[arg(long = "cors")]
    cors: bool,
}

// ============================================================================
// Shell Completions Arguments
// ============================================================================

#[derive(Args, Debug)]
struct ShellArgs {
    /// Shell to generate completions for
    #[arg(value_enum)]
    shell: Shell,

    /// Output file (stdout if not specified)
    #[arg(short = 'o', long = "output", value_name = "FILE")]
    output: Option<PathBuf>,
}

// ============================================================================
// System Info Arguments
// ============================================================================

#[derive(Args, Debug)]
struct SystemArgs {
    /// Check Ollama connectivity
    #[arg(long = "check-ollama")]
    check_ollama: bool,

    /// List available models
    #[arg(long = "list-models")]
    list_models: bool,

    /// Show analyzer information
    #[arg(long = "analyzers")]
    analyzers: bool,

    /// Check all dependencies
    #[arg(long = "check-deps")]
    check_deps: bool,

    /// Output format
    #[arg(short = 'o', long = "output", default_value = "table")]
    output: OutputFormat,
}

// ============================================================================
// Main Entry Point
// ============================================================================

#[tokio::main]
async fn main() -> Result<()> {
    let cli = Cli::parse();

    // Setup logging
    setup_logging(cli.verbose, cli.quiet, cli.json);

    // Setup colors
    if cli.no_color {
        colored::control::set_override(false);
    }

    // Load configuration
    let config = load_config(&cli).await?;

    // Handle graceful shutdown
    let shutdown = setup_shutdown_handler();

    // Execute command
    let result = match cli.command {
        Commands::Scan(args) => cmd_scan(args, &config, cli.dry_run).await,
        Commands::Watch(args) => cmd_watch(args, &config, shutdown).await,
        Commands::Rename(args) => cmd_rename(args, &config, cli.dry_run).await,
        Commands::Tag(args) => cmd_tag(args, &config).await,
        Commands::Search(args) => cmd_search(args, &config).await,
        Commands::History(args) => cmd_history(args, &config).await,
        Commands::Undo(args) => cmd_undo(args, &config, cli.dry_run).await,
        Commands::Config(args) => cmd_config(args, &config).await,
        Commands::Db(args) => cmd_db(args, &config).await,
        #[cfg(feature = "web")]
        Commands::Web(args) => cmd_web(args, &config, shutdown).await,
        Commands::Shell(args) => cmd_shell(args),
        Commands::System(args) => cmd_system(args, &config).await,
    };

    if let Err(e) = &result {
        error!("Error: {}", e);
        if cli.verbose > 0 {
            error!("Details: {:?}", e);
        }
    }

    result
}

// ============================================================================
// Setup Functions
// ============================================================================

fn setup_logging(verbose: u8, quiet: bool, json: bool) {
    let level = if quiet {
        Level::ERROR
    } else {
        match verbose {
            0 => Level::WARN,
            1 => Level::INFO,
            2 => Level::DEBUG,
            _ => Level::TRACE,
        }
    };

    let filter = EnvFilter::from_default_env()
        .add_directive(level.into());

    if json {
        fmt()
            .with_env_filter(filter)
            .json()
            .init();
    } else {
        fmt()
            .with_env_filter(filter)
            .with_target(false)
            .init();
    }
}

fn setup_shutdown_handler() -> tokio::sync::broadcast::Sender<()> {
    let (tx, _) = tokio::sync::broadcast::channel(1);
    let tx_clone = tx.clone();

    tokio::spawn(async move {
        tokio::signal::ctrl_c().await.ok();
        info!("Received shutdown signal");
        let _ = tx_clone.send(());
    });

    tx
}

async fn load_config(cli: &Cli) -> Result<AppConfig> {
    let config_path = cli.config.clone().unwrap_or_else(|| {
        directories::ProjectDirs::from("com", "panoptes", "panoptes")
            .map(|dirs| dirs.config_dir().join("config.json"))
            .unwrap_or_else(|| PathBuf::from("config.json"))
    });

    let mut config = if config_path.exists() {
        AppConfig::load(&config_path)?
    } else {
        info!("Using default configuration");
        AppConfig::default()
    };

    // Override with CLI options
    config.ai_engine.url = cli.ollama_url.clone();
    config.jobs = cli.jobs;

    if let Some(db_path) = &cli.database {
        config.database.path = db_path.clone();
    }

    Ok(config)
}

// ============================================================================
// Command Implementations (stubs for now)
// ============================================================================

async fn cmd_scan(_args: ScanArgs, _config: &AppConfig, _dry_run: bool) -> Result<()> {
    println!("{}", "Scan command - implementation in progress".yellow());
    Ok(())
}

async fn cmd_watch(_args: WatchArgs, _config: &AppConfig, _shutdown: tokio::sync::broadcast::Sender<()>) -> Result<()> {
    println!("{}", "Watch command - implementation in progress".yellow());
    Ok(())
}

async fn cmd_rename(_args: RenameArgs, _config: &AppConfig, _dry_run: bool) -> Result<()> {
    println!("{}", "Rename command - implementation in progress".yellow());
    Ok(())
}

async fn cmd_tag(_args: TagArgs, _config: &AppConfig) -> Result<()> {
    println!("{}", "Tag command - implementation in progress".yellow());
    Ok(())
}

async fn cmd_search(_args: SearchArgs, _config: &AppConfig) -> Result<()> {
    println!("{}", "Search command - implementation in progress".yellow());
    Ok(())
}

async fn cmd_history(_args: HistoryArgs, _config: &AppConfig) -> Result<()> {
    println!("{}", "History command - implementation in progress".yellow());
    Ok(())
}

async fn cmd_undo(_args: UndoArgs, _config: &AppConfig, _dry_run: bool) -> Result<()> {
    println!("{}", "Undo command - implementation in progress".yellow());
    Ok(())
}

async fn cmd_config(_args: ConfigArgs, _config: &AppConfig) -> Result<()> {
    println!("{}", "Config command - implementation in progress".yellow());
    Ok(())
}

async fn cmd_db(_args: DbArgs, _config: &AppConfig) -> Result<()> {
    println!("{}", "Database command - implementation in progress".yellow());
    Ok(())
}

#[cfg(feature = "web")]
async fn cmd_web(_args: WebArgs, _config: &AppConfig, _shutdown: tokio::sync::broadcast::Sender<()>) -> Result<()> {
    println!("{}", "Web command - implementation in progress".yellow());
    Ok(())
}

fn cmd_shell(args: ShellArgs) -> Result<()> {
    use clap::CommandFactory;
    use std::io;

    let mut cmd = Cli::command();

    if let Some(output) = args.output {
        let mut file = std::fs::File::create(&output)?;
        clap_complete::generate(args.shell, &mut cmd, "panoptes", &mut file);
        println!("Shell completions written to {}", output.display());
    } else {
        clap_complete::generate(args.shell, &mut cmd, "panoptes", &mut io::stdout());
    }

    Ok(())
}

async fn cmd_system(args: SystemArgs, config: &AppConfig) -> Result<()> {
    println!("{}", "=== Panoptes System Information ===".bold());
    println!();
    println!("Version: {}", env!("CARGO_PKG_VERSION"));
    println!("Rust Version: {}", env!("CARGO_PKG_RUST_VERSION"));
    println!();

    if args.check_ollama || args.list_models {
        println!("{}", "--- Ollama Status ---".bold());
        match ollama::check_health(&config.ai_engine.url).await {
            Ok(()) => println!("  Ollama: {} at {}", "Connected".green(), config.ai_engine.url),
            Err(e) => println!("  Ollama: {} - {}", "Not Available".red(), e),
        }

        if args.list_models {
            match ollama::list_models(&config.ai_engine.url).await {
                Ok(models) => {
                    println!("  Models:");
                    for model in models {
                        println!("    - {}", model);
                    }
                }
                Err(e) => println!("  Could not list models: {}", e),
            }
        }
        println!();
    }

    if args.analyzers {
        println!("{}", "--- Available Analyzers ---".bold());
        println!("  - Image (moondream vision)");
        println!("  - PDF (text extraction + LLM)");
        println!("  - Audio (metadata + transcription)");
        println!("  - Video (keyframe extraction)");
        println!("  - Code (tree-sitter analysis)");
        println!("  - Document (Office formats)");
        println!("  - Archive (ZIP/TAR inspection)");
        println!();
    }

    Ok(())
}
