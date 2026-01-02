// SPDX-License-Identifier: AGPL-3.0-or-later
//! Dashboard showing marking progress overview

use dioxus::prelude::*;
use crate::state::User;

#[component]
pub fn Dashboard(
    user: User,
    on_logout: EventHandler<()>
) -> Element {
    rsx! {
        div {
            class: "dashboard",

            // Welcome section
            section {
                class: "welcome-section",

                h1 { "Welcome back, {user.name}" }
                p { class: "text-muted", "Here's your marking overview" }
            }

            // Stats cards
            section {
                class: "stats-grid",

                div {
                    class: "stat-card",
                    div { class: "stat-value", "12" }
                    div { class: "stat-label", "Pending" }
                }

                div {
                    class: "stat-card",
                    div { class: "stat-value", "8" }
                    div { class: "stat-label", "In Progress" }
                }

                div {
                    class: "stat-card",
                    div { class: "stat-value", "45" }
                    div { class: "stat-label", "Completed" }
                }

                div {
                    class: "stat-card",
                    div { class: "stat-value", "69%" }
                    div { class: "stat-label", "Complete" }
                }
            }

            // Recent activity
            section {
                class: "recent-section",

                h2 { "Recent Submissions" }

                div {
                    class: "submission-list",

                    for i in 0..5 {
                        super::SubmissionCard {
                            key: "{i}",
                            student_name: format!("Student {}", i + 1),
                            course_code: "M150-25J".to_string(),
                            tma_number: format!("TMA0{}", (i % 3) + 1),
                            status: if i % 3 == 0 { "pending" } else if i % 3 == 1 { "in_progress" } else { "completed" }.to_string()
                        }
                    }
                }
            }

            // Logout button
            button {
                class: "btn-logout",
                onclick: move |_| on_logout.call(()),
                "Sign Out"
            }
        }
    }
}
