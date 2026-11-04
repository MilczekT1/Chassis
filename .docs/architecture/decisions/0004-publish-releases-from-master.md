# 4. Publish Releases From Master

Date: 2026-11-04

## Status

Accepted

Amends [3. Release Process](0003-release-process.md)

## Context

ADR 3 tagged `v<version>` and deployed from the `release/v<version>` branch, then rebase-merged
the release PR. A rebase merge re-creates every commit with a new SHA, so the tag points at a
commit that never reaches `master`: `v0.5.3` points at `b7e1679`, which no branch contains, while
the `Release Chassis 0.5.3` commit on `master` is `cac8137`. The deployed tree was also never
built on its own: the release PR's checks run on the snapshot-bump commit above it, and the
deploy skipped tests.

ADR 3 kept deploy inline because a Release created with `GITHUB_TOKEN` cannot trigger another
workflow. A push made by the GitHub App can, and the App already enables auto-merge on the
release PR, so the merge itself is the App's push.

## Decision

`release.yml` only prepares the release: it commits the release version and the snapshot bump on
`release/v<version>`, opens the PR and enables rebase auto-merge. It tags and deploys nothing.

A new `publish-release.yml` runs on a push to `master` whose head commit starts with
`Prepare next release Chassis `. It takes the version from the parent commit's
`Release Chassis <version>` subject, checks out that commit, asserts the project version, runs
`./mvnw clean verify`, deploys to GitHub Packages, tags the commit `v<version>` and creates the
GitHub Release. Each step is skipped when already done, and the workflow can be dispatched from
`master` with `version` to retry; the dispatch path keeps the `RELEASE_ALLOWED_ACTORS` check.

## Consequences

Positive:

* The tag points at the release commit on `master`.
* The deployed tree is verified before deploy.
* A failed publish is retried by re-dispatching one idempotent workflow.

Negative:

* Release PRs must be rebase-merged. A squash folds both commits into one and the publish step
  fails rather than guessing.
* Publishing now depends on the App performing the merge. If someone merges the release PR by
  hand, the push still triggers the workflow; if the push came from `GITHUB_TOKEN`, it would not,
  and the release must be dispatched manually.
* Tags `v0.5.1`–`v0.5.3` created under ADR 3 still point at release-branch commits.
