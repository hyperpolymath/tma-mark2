// SPDX-License-Identifier: AGPL-3.0-or-later
//! tma-mobile - eTMA Handler Mobile Companion App
//!
//! A post-quantum secure mobile application for:
//! - Viewing marking progress
//! - Receiving notifications
//! - Secure document preview
//! - Offline-first data sync

#![allow(non_snake_case)]

mod components;
mod crypto;
mod state;
mod api;

use dioxus::prelude::*;
use tracing::info;

fn main() {
    // Initialize logging
    #[cfg(target_os = "android")]
    android_logger::init_once(
        android_logger::Config::default()
            .with_max_level(log::LevelFilter::Info)
            .with_tag("tma-mobile"),
    );

    #[cfg(target_os = "ios")]
    oslog::OsLogger::new("com.hyperpolymath.tma-mobile")
        .level_filter(log::LevelFilter::Info)
        .init()
        .unwrap();

    #[cfg(not(any(target_os = "android", target_os = "ios")))]
    tracing_subscriber::fmt::init();

    info!("Starting eTMA Handler Mobile");

    dioxus::launch(App);
}

/// Main application component
fn App() -> Element {
    // Global state
    let mut auth_state = use_signal(|| state::AuthState::default());
    let mut sync_status = use_signal(|| state::SyncStatus::Idle);

    rsx! {
        document::Link { rel: "stylesheet", href: asset!("/assets/main.css") }

        div {
            class: "app-container",

            // Header
            components::Header {
                sync_status: sync_status.read().clone()
            }

            // Main content
            main {
                class: "main-content",

                match auth_state.read().clone() {
                    state::AuthState::Authenticated { user } => rsx! {
                        components::Dashboard {
                            user: user.clone(),
                            on_logout: move |_| auth_state.set(state::AuthState::Unauthenticated)
                        }
                    },
                    state::AuthState::Unauthenticated => rsx! {
                        components::LoginScreen {
                            on_login: move |user| auth_state.set(state::AuthState::Authenticated { user })
                        }
                    },
                    state::AuthState::Loading => rsx! {
                        components::LoadingScreen {}
                    }
                }
            }

            // Navigation bar
            components::NavBar {}
        }
    }
}
