// SPDX-License-Identifier: AGPL-3.0-or-later
//! Bottom navigation bar

use dioxus::prelude::*;

#[component]
pub fn NavBar() -> Element {
    let mut active_tab = use_signal(|| "dashboard");

    rsx! {
        nav {
            class: "nav-bar",

            button {
                class: if active_tab() == "dashboard" { "nav-item active" } else { "nav-item" },
                onclick: move |_| active_tab.set("dashboard"),

                svg {
                    class: "nav-icon",
                    view_box: "0 0 24 24",
                    fill: "none",
                    stroke: "currentColor",
                    path {
                        d: "M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"
                    }
                }
                span { "Home" }
            }

            button {
                class: if active_tab() == "submissions" { "nav-item active" } else { "nav-item" },
                onclick: move |_| active_tab.set("submissions"),

                svg {
                    class: "nav-icon",
                    view_box: "0 0 24 24",
                    fill: "none",
                    stroke: "currentColor",
                    path {
                        d: "M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                    }
                }
                span { "Marking" }
            }

            button {
                class: if active_tab() == "settings" { "nav-item active" } else { "nav-item" },
                onclick: move |_| active_tab.set("settings"),

                svg {
                    class: "nav-icon",
                    view_box: "0 0 24 24",
                    fill: "none",
                    stroke: "currentColor",
                    path {
                        d: "M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"
                    }
                    circle { cx: "12", cy: "12", r: "3" }
                }
                span { "Settings" }
            }
        }
    }
}
