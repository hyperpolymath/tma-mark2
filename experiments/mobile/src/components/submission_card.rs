// SPDX-License-Identifier: MPL-2.0
//! Submission card component

use dioxus::prelude::*;

#[component]
pub fn SubmissionCard(
    student_name: String,
    course_code: String,
    tma_number: String,
    status: String,
) -> Element {
    let status_class = match status.as_str() {
        "pending" => "status-pending",
        "in_progress" => "status-in-progress",
        "completed" => "status-completed",
        _ => "status-unknown",
    };

    rsx! {
        div {
            class: "submission-card",

            div {
                class: "submission-info",

                h3 { "{student_name}" }
                p {
                    class: "submission-meta",
                    "{course_code} • {tma_number}"
                }
            }

            div {
                class: "submission-status {status_class}",
                "{status}"
            }
        }
    }
}
