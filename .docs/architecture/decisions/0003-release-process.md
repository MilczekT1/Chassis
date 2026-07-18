# 3. Release Process

Date: 2026-07-17

## Status

Accepted

## Context

Releases were driven by two local bash scripts (`prepare-release.sh`, `tag-release.sh`) with
a manual merge to `master` in between. This had two correctness bugs and several safety gaps:

* `tag-release.sh` picked the commit to tag positionally (`git log -2 | tail -1`). After the
  release branch was merged to `master` via PR, this tagged the wrong commit — verified: tag
  `v0.5.0` landed on a `-SNAPSHOT` commit, not the release commit.
* Pushing the tag did not trigger publishing. `publish-java.yml` triggers on
  `release: [created]` / `workflow_dispatch`, not on tag push, so the documented "push to
  trigger release" step published nothing.
* The scripts used ambient `mvn` (not `./mvnw`), never fast-forwarded `master` before
  branching, and had no guard against an existing tag.

`master` has no branch protection, so CI can push to it directly with the default
`GITHUB_TOKEN`.

## Decision

Replace the scripts with a single CI-driven `workflow_dispatch` workflow
(`.github/workflows/release.yml`) that runs the whole release in one job: guard inputs →
`./mvnw versions:set` → commit → tag the release commit → `./mvnw deploy` → bump to next
`-SNAPSHOT` → commit → atomically push `master` and tag → create the GitHub Release.

Publishing is done inline in the same job rather than by a separate workflow triggered off
the created Release. GitHub deliberately does not start new workflow runs from events raised
by the default `GITHUB_TOKEN`, so a Release created by this workflow cannot trigger
`publish-java.yml`; relying on that trigger would silently publish nothing.

`publish-java.yml` is retained as a manual-only (`workflow_dispatch`) re-publish escape
hatch. The scripts are deleted.

Tests are skipped at release time (`-DskipTests=true`), matching prior behaviour; `master`
is already gated by the Build Pipeline before a release is cut.

## Consequences

Positive:

* The tag is created on the release commit at creation time — mis-tagging is structurally
  impossible.
* Publishing cannot silently fail to trigger — it runs in the same job.
* Releases run on a clean, synced CI checkout with the pinned Maven wrapper; no
  machine-local state.
* A failure at or before the deploy step leaves `master` and tags untouched, so the release
  can be re-run safely; the `master` + tag push is atomic (`git push --atomic`).
* A failure *after* a successful deploy (the push or GitHub Release step) is not recoverable
  by a plain re-run — the artifacts are already published and GitHub Packages rejects
  re-deploying an existing version. Recovery means deleting the published package version and
  re-running, or moving to the next version.
* One click, two inputs — no local scripts, no manual merge step.

Negative:

* The version-bump commits are authored by `github-actions[bot]`. If `master` branch
  protection is added later, the bot must be allowed to push or the flow must switch to a
  release-PR model.
* No release-time test gate; a regression merged to `master` without CI catching it could be
  released.
* The README changelog is still updated by hand.
