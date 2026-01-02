// SPDX-License-Identifier: AGPL-3.0-or-later
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
