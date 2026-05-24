// SPDX-License-Identifier: MPL-2.0
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
