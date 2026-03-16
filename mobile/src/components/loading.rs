// SPDX-License-Identifier: PMPL-1.0-or-later
//! Loading screen component

use dioxus::prelude::*;

#[component]
pub fn LoadingScreen() -> Element {
    rsx! {
        div {
            class: "loading-screen",

            div {
                class: "loading-spinner"
            }

            p { "Loading..." }
        }
    }
}
