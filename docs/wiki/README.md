<!-- SPDX-License-Identifier: MPL-2.0 -->

# docs/wiki/ — GitHub Wiki staging area

This directory holds the **source of truth** for the
<https://github.com/hyperpolymath/tma-mark2/wiki> pages. Files here are
plain Markdown using the conventions GitHub wikis expect:

| File | Role |
|------|------|
| `Home.md` | Wiki landing page (required by GitHub wikis). |
| `_Sidebar.md` | Sidebar — appears on every page. |
| `_Footer.md` | Footer — appears on every page. |
| `<Page-Name>.md` | A wiki page. The filename (sans `.md`) is the URL slug. |

## Why the staging area exists

GitHub wikis live in a separate Git repository
(`tma-mark2.wiki.git`) outside this repo's CI / signing scope. The
files are versioned here so:

* The wiki copy is reviewable through pull requests on the main repo.
* The wiki cannot drift silently — the canonical content is in this
  tree and tracked alongside the rest of the codebase.
* If the GitHub wiki is wiped or corrupted, restoring it is just a
  copy operation.

## Editing rules

1. Treat these pages as a **navigation layer** only. Authoritative
   content lives elsewhere in `docs/`, `README.adoc`, `TOPOLOGY.md`,
   etc. Wiki pages link to those — they do not duplicate the content.
2. If a wiki page disagrees with the repository, fix the wiki page,
   not the repository.
3. Every link should point to a stable file on `main` using the form
   `https://github.com/hyperpolymath/tma-mark2/blob/main/<path>`.

## Deploying the staged content to the wiki

The wiki repo lives at `https://github.com/hyperpolymath/tma-mark2.wiki.git`.
To push these files there:

```bash
# One-time clone of the wiki repo (next to this repo)
git clone https://github.com/hyperpolymath/tma-mark2.wiki.git ../tma-mark2.wiki

# Sync the staged files
cp docs/wiki/*.md ../tma-mark2.wiki/

# Commit and push
cd ../tma-mark2.wiki
git add -A
git commit -m "Sync wiki from docs/wiki/"
git push origin master
```

### Optional: automate via GitHub Actions

If you'd like the wiki to update automatically on every push to `main`
that touches `docs/wiki/**`, add a workflow that uses the
[`Andrew-Chen-Wang/github-wiki-action`](https://github.com/Andrew-Chen-Wang/github-wiki-action)
or similar published action. We don't ship that workflow by default
because it requires write permission on the wiki repo and the
project's signing/CI policy hasn't yet been extended to cover it.
