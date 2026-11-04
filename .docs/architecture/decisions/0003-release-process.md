# 3. Release Process

Date: 2026-07-17

## Status

Accepted

Amended by [4. Publish Releases From Master](0004-publish-releases-from-master.md)

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

`master` is branch-protected and requires a green build check, so release changes reach it
through a pull request rather than a direct push. GitHub also withholds the triggering user's
credentials from a workflow and never starts a new workflow run from an event raised by the
default `GITHUB_TOKEN` — both facts shape the token and attribution decisions below.

## Decision

Releases run in a single CI-driven `workflow_dispatch` workflow
(`.github/workflows/release.yml`), and only an **allowlisted set of users** may trigger it: the
first job step checks `github.actor` against a hardcoded, space-separated `RELEASE_ALLOWED_ACTORS`
list and fails fast before any side effect. `workflow_dispatch` already limits triggering to
users with repo write access; the allowlist narrows that to the intended set.

The job then: guards inputs → creates branch `release/v<version>` → `./mvnw versions:set` →
commits `Release Chassis <version>` (with a `Co-authored-by:` trailer crediting the triggerer)
and tags `v<version>` on it → checks whether the version is already published (idempotency) →
`./mvnw deploy` → bumps to `<next>-SNAPSHOT` and commits → atomically pushes the branch + tag →
creates the GitHub Release → opens a PR (`release/v<version>` → `master`) and enables auto-merge
(rebase merge, delete branch after). The PR lands automatically once the required build check
passes.

Deploy still happens inline in this same job rather than via a workflow triggered off the
created Release: GitHub does not start new workflow runs from `GITHUB_TOKEN`-raised events, so a
Release created by this workflow cannot trigger `publish-java.yml`; relying on that trigger would
silently publish nothing.

The release PR is created with a **GitHub App installation token**
(`actions/create-github-app-token`, secrets `RELEASE_APP_ID` / `RELEASE_APP_PRIVATE_KEY`), not
`GITHUB_TOKEN`. A PR created by the default token cannot trigger `build-java.yml`, so its
required build check would never arrive and auto-merge would hang; dropping that required check
to compensate would weaken the gate for every PR to `master`. An App token is a non-default
identity, so the PR runs CI and `master` protection stays fully intact.

`publish-java.yml` is retained as a manual-only (`workflow_dispatch`) re-publish escape hatch.
The old `prepare-release.sh` / `tag-release.sh` scripts are deleted.

Tests are skipped in the deploy step (`-DskipTests=true`); the release PR's required build check
is the pre-merge gate.

## Consequences

Positive:

* The tag is created on the release commit at creation time — mis-tagging is structurally
  impossible.
* Publishing cannot silently fail to trigger — it runs in the same job.
* Only allowlisted users can start a release; unauthorized triggers fail before any side effect.
* The release reaches `master` through an auto-merging PR that runs the real build check, so
  `master` branch protection is respected and never bypassed.
* The triggerer is credited durably (PR body `@mention` + commit `Co-authored-by:` trailer that
  survives the merge), even though they cannot be the literal PR author.
* Releases run on a clean, synced CI checkout with the pinned Maven wrapper; no machine-local
  state.

Negative:

* The literal PR **Author** is the GitHub App bot, not the person who triggered the release —
  GitHub does not expose the triggerer's credentials to the workflow, and PR authorship cannot
  be set to an arbitrary user. Attribution (above) is the mitigation.
* Requires one-time maintainer setup that this repo cannot self-provision: a GitHub App install
  with Contents + Pull-requests write, its two secrets, "Allow auto-merge" enabled, and the
  build check kept required on `master`.
* A failure after a successful deploy but before merge leaves artifacts published while the PR
  is still open; recovery means completing/re-running the merge or moving to the next version
  (GitHub Packages rejects re-deploying an existing version).
* The README changelog is still updated by hand.
