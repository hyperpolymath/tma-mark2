// SPDX-License-Identifier: AGPL-3.0-or-later
//! Login screen component

use dioxus::prelude::*;
use crate::state::User;

#[component]
pub fn LoginScreen(on_login: EventHandler<User>) -> Element {
    let mut username = use_signal(String::new);
    let mut password = use_signal(String::new);
    let mut error = use_signal(|| None::<String>);
    let mut loading = use_signal(|| false);

    let handle_login = move |_| {
        loading.set(true);
        error.set(None);

        // Simulate login (in real app, this would call the API)
        let user = User {
            id: "user-123".to_string(),
            name: username.read().clone(),
            email: format!("{}@open.ac.uk", username.read()),
            role: crate::state::UserRole::Tutor,
        };

        on_login.call(user);
        loading.set(false);
    };

    rsx! {
        div {
            class: "login-screen",

            div {
                class: "login-card",

                // Logo
                div {
                    class: "login-logo",
                    h1 { "eTMA" }
                    p { "Mobile Companion" }
                }

                // Form
                form {
                    class: "login-form",
                    onsubmit: handle_login,

                    div {
                        class: "form-group",
                        label { r#for: "username", "OUCU / Username" }
                        input {
                            r#type: "text",
                            id: "username",
                            placeholder: "e.g. ab1234",
                            value: "{username}",
                            oninput: move |e| username.set(e.value())
                        }
                    }

                    div {
                        class: "form-group",
                        label { r#for: "password", "Password" }
                        input {
                            r#type: "password",
                            id: "password",
                            placeholder: "Your password",
                            value: "{password}",
                            oninput: move |e| password.set(e.value())
                        }
                    }

                    if let Some(err) = error.read().as_ref() {
                        div {
                            class: "error-message",
                            "{err}"
                        }
                    }

                    button {
                        r#type: "submit",
                        class: "btn-primary",
                        disabled: loading(),

                        if loading() {
                            "Signing in..."
                        } else {
                            "Sign In"
                        }
                    }
                }

                // Security notice
                p {
                    class: "security-notice",
                    "🔒 Post-quantum encrypted connection"
                }
            }
        }
    }
}
