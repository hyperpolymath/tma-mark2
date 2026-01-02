// SPDX-License-Identifier: AGPL-3.0-or-later
//! Header component with sync status

use dioxus::prelude::*;
use crate::state::SyncStatus;

#[component]
pub fn Header(sync_status: SyncStatus) -> Element {
    rsx! {
        header {
            class: "header",

            div {
                class: "header-logo",
                "eTMA Mobile"
            }

            div {
                class: "header-status",

                match sync_status {
                    SyncStatus::Idle => rsx! {
                        span { class: "status-dot status-idle" }
                        span { "Synced" }
                    },
                    SyncStatus::Syncing => rsx! {
                        span { class: "status-dot status-syncing animate-pulse" }
                        span { "Syncing..." }
                    },
                    SyncStatus::Error(ref msg) => rsx! {
                        span { class: "status-dot status-error" }
                        span { title: "{msg}", "Error" }
                    },
                    SyncStatus::Offline => rsx! {
                        span { class: "status-dot status-offline" }
                        span { "Offline" }
                    }
                }
            }
        }
    }
}
