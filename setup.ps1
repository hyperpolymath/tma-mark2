# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT
#
# eTMA Handler - Windows Setup Script (PowerShell)
# ================================================
#
# Run with:
#   irm https://raw.githubusercontent.com/Hyperpolymath/tma-mark2/main/setup.ps1 | iex
#
# Or download and run:
#   Invoke-WebRequest -Uri https://raw.githubusercontent.com/Hyperpolymath/tma-mark2/main/setup.ps1 -OutFile setup.ps1
#   .\setup.ps1
#
# This script:
#   1. Installs Podman Desktop (if needed)
#   2. Installs Just (if needed)
#   3. Pulls the eTMA Handler container
#   4. Shows you how to run it
#

#Requires -Version 5.1

$ErrorActionPreference = "Stop"

# Colors
function Write-Info { Write-Host "ℹ $args" -ForegroundColor Cyan }
function Write-Success { Write-Host "✓ $args" -ForegroundColor Green }
function Write-Warn { Write-Host "⚠ $args" -ForegroundColor Yellow }
function Write-Err { Write-Host "✗ $args" -ForegroundColor Red; exit 1 }

# Header
Write-Host ""
Write-Host "╔═══════════════════════════════════════════════════════════╗" -ForegroundColor Magenta
Write-Host "║              eTMA Handler - Setup Script                  ║" -ForegroundColor Magenta
Write-Host "║            Open University Marking Tool                   ║" -ForegroundColor Magenta
Write-Host "║                   (Windows Edition)                       ║" -ForegroundColor Magenta
Write-Host "╚═══════════════════════════════════════════════════════════╝" -ForegroundColor Magenta
Write-Host ""

# Check if running as admin (warn but don't block)
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $isAdmin) {
    Write-Warn "Not running as Administrator. Some installations may require elevation."
}

# Check for winget
function Test-Winget {
    try {
        $null = Get-Command winget -ErrorAction Stop
        return $true
    } catch {
        return $false
    }
}

# Install Podman
function Install-Podman {
    $podmanPath = Get-Command podman -ErrorAction SilentlyContinue
    if ($podmanPath) {
        $version = & podman --version 2>$null
        Write-Success "Podman already installed: $version"
        return
    }

    Write-Info "Installing Podman..."

    if (Test-Winget) {
        Write-Info "Using winget to install Podman Desktop..."
        winget install -e --id RedHat.Podman

        # Refresh PATH
        $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
    } else {
        Write-Warn "winget not found. Please install Podman manually:"
        Write-Host "  https://podman.io/getting-started/installation#windows" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "  Or install winget from Microsoft Store (App Installer)" -ForegroundColor Yellow
        exit 1
    }

    # Initialize Podman machine
    Write-Info "Initializing Podman machine..."
    try {
        & podman machine init 2>$null
        & podman machine start 2>$null
    } catch {
        Write-Warn "Podman machine may need manual initialization"
    }

    Write-Success "Podman installed"
}

# Install Just
function Install-Just {
    $justPath = Get-Command just -ErrorAction SilentlyContinue
    if ($justPath) {
        $version = & just --version 2>$null
        Write-Success "Just already installed: $version"
        return
    }

    Write-Info "Installing Just..."

    if (Test-Winget) {
        winget install -e --id Casey.Just

        # Refresh PATH
        $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
    } else {
        # Alternative: use cargo or scoop
        if (Get-Command cargo -ErrorAction SilentlyContinue) {
            Write-Info "Using cargo to install just..."
            cargo install just
        } elseif (Get-Command scoop -ErrorAction SilentlyContinue) {
            Write-Info "Using scoop to install just..."
            scoop install just
        } else {
            Write-Warn "Please install just manually:"
            Write-Host "  winget install Casey.Just" -ForegroundColor Yellow
            Write-Host "  OR" -ForegroundColor Yellow
            Write-Host "  scoop install just" -ForegroundColor Yellow
            exit 1
        }
    }

    Write-Success "Just installed"
}

# Create data directory
function New-DataDirectory {
    $dataDir = "$env:APPDATA\etma_handler"
    if (-not (Test-Path $dataDir)) {
        New-Item -ItemType Directory -Path $dataDir -Force | Out-Null
    }
    Write-Success "Data directory: $dataDir"
    return $dataDir
}

# Pull container
function Get-Container {
    Write-Info "Pulling eTMA Handler container..."

    $image = "ghcr.io/hyperpolymath/etma-handler:latest"

    try {
        & podman pull $image
        Write-Success "Container pulled successfully"
    } catch {
        Write-Warn "Could not pull container (might not be published yet)"
        Write-Info "You can build it locally after cloning the repository"
    }
}

# Main
function Main {
    Install-Podman
    Install-Just
    $dataDir = New-DataDirectory
    Get-Container

    Write-Host ""
    Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Green
    Write-Host "                    Setup Complete!                        " -ForegroundColor Green
    Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Green
    Write-Host ""
    Write-Host "  To run eTMA Handler:" -ForegroundColor White
    Write-Host ""
    Write-Host "    Option 1 - Direct with Podman:" -ForegroundColor White
    Write-Host "      podman run -p 4000:4000 -v ${dataDir}:/data ghcr.io/hyperpolymath/etma-handler" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "    Option 2 - Clone and use Just:" -ForegroundColor White
    Write-Host "      git clone https://github.com/Hyperpolymath/tma-mark2.git" -ForegroundColor Cyan
    Write-Host "      cd tma-mark2" -ForegroundColor Cyan
    Write-Host "      just do-it" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  Then open: http://localhost:4000" -ForegroundColor Yellow
    Write-Host ""

    # Ask if they want to run now
    $run = Read-Host "Run eTMA Handler now? (y/N)"
    if ($run -eq 'y' -or $run -eq 'Y') {
        Write-Info "Starting eTMA Handler..."
        Write-Host "  Opening http://localhost:4000 in 5 seconds..." -ForegroundColor Yellow
        Start-Process "http://localhost:4000" -ErrorAction SilentlyContinue
        & podman run -p 4000:4000 -v "${dataDir}:/data" ghcr.io/hyperpolymath/etma-handler:latest
    }
}

Main
