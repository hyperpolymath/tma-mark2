// SPDX-License-Identifier: AGPL-3.0-or-later
//! Circular progress indicator component

use dioxus::prelude::*;

#[component]
pub fn ProgressRing(
    #[props(default = 0.0)]
    progress: f64,
    #[props(default = 100)]
    size: i32,
    #[props(default = 8)]
    stroke_width: i32,
) -> Element {
    let radius = (size / 2) - stroke_width;
    let circumference = 2.0 * std::f64::consts::PI * (radius as f64);
    let offset = circumference - (progress / 100.0) * circumference;

    let center = size / 2;

    rsx! {
        svg {
            class: "progress-ring",
            width: "{size}",
            height: "{size}",

            // Background circle
            circle {
                class: "progress-ring-bg",
                stroke: "#e5e7eb",
                stroke_width: "{stroke_width}",
                fill: "transparent",
                r: "{radius}",
                cx: "{center}",
                cy: "{center}"
            }

            // Progress circle
            circle {
                class: "progress-ring-progress",
                stroke: "#3b82f6",
                stroke_width: "{stroke_width}",
                stroke_linecap: "round",
                fill: "transparent",
                r: "{radius}",
                cx: "{center}",
                cy: "{center}",
                stroke_dasharray: "{circumference}",
                stroke_dashoffset: "{offset}",
                transform: "rotate(-90 {center} {center})"
            }

            // Text
            text {
                class: "progress-ring-text",
                x: "50%",
                y: "50%",
                text_anchor: "middle",
                dominant_baseline: "middle",
                "{progress:.0}%"
            }
        }
    }
}
