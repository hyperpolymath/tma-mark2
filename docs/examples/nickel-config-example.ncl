# SPDX-License-Identifier: MPL-2.0
# Example: generic Nickel configuration. Not project config; included
# as a documentation reference for the Nickel language.
{
  name = "my-app",
  replicas =
    let env = "dev" in
    if env == "prod" then 5
    else if env == "staging" then 3
    else 1,
  port = 8080,
  env = "dev",
}
