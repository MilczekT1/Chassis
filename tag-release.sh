#!/usr/bin/env bash
set -euo pipefail -o errtrace

# sh tag-release.sh 0.4.2

declare TAG_VERSION

function readParams() {
    TAG_VERSION="$1"
}

function checkVersion {
    local version="$1"
    if [[ -z "$version" ]]; then
        echo "Version must be set" 1>&2
        exit 1
    fi

    if [[ ! "$version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        echo "Invalid version format: $version" 1>&2
        exit 1
    fi
}
readParams "$@"
checkVersion "$TAG_VERSION"


SHA="$(git log -2 --format="%h" | tail -1)"
echo "Creating tag v$TAG_VERSION on commit $SHA"
git tag "v$TAG_VERSION" "$SHA"

echo "Pushing tags to repo"
git push origin "v$TAG_VERSION"
echo Done
