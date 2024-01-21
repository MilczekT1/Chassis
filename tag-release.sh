#!/usr/bin/env bash
set -euo pipefail -o errtrace

# sh tag-release.sh 0.4.2

declare TAG_VERSION

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


SHA=$(git log -2 --format="%H")
echo "Tagging commit $SHA"
git tag -a "v$TAG_VERSION" $SHA -m "Release $TAG_VERSION"
echo "Push tag to repo"
git push -u --follow-tags origin master
