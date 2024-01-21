#!/usr/bin/env bash
set -euo pipefail -o errtrace

#1. sh prepare-release.sh 0.4.2 0.4.3
#2. sh tag-release.sh 0.4.2

REPO_NAME="${PWD##*/}"

declare RELEASE_VERSION
declare NEXT_VERSION

function readParams() {
    RELEASE_VERSION="$1"
    NEXT_VERSION="$2"
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

function createBranch {
    local -r branchName="$1"
    local -r version="$2"
    local -r nextVersion="$3"
    local -r commitMsg="$4"

    echo "Create branch: $branchName"
    git checkout -b "$branchName"

    # Release commit
    echo "Set maven version to $version"
    mvn versions:set "-DnewVersion=$version" -DprocessAllModules=true -DgenerateBackupPoms=false
    git commit -a -m "$commitMsg"

    echo "commit sha to tag $(git log -1 --format="%H")"

    # Next release commit
    mvn versions:set "-DnewVersion=$nextVersion-SNAPSHOT" -DprocessAllModules=true -DgenerateBackupPoms=false
    git commit -a -m "Prepare next release $REPO_NAME $nextVersion-SNAPSHOT"
}

function prepareBranch {
    local -r branchName="$1"
    local -r version="$2"
    local -r nextVersion="$3"
    local -r commitMsg="$4"
    createBranch "$branchName" "$version" "$nextVersion" "$commitMsg"
 }

readParams "$@"
checkVersion "$RELEASE_VERSION"
checkVersion "$NEXT_VERSION"

echo "=================== Get $REPO_NAME master branch ==================="
git checkout master
git fetch origin master

#prepareBranch <branchName> <version> <commitMsg>

echo "=================== Prepare $REPO_NAME release $RELEASE_VERSION ==================="
prepareBranch "release/${RELEASE_VERSION}" "$RELEASE_VERSION" "$NEXT_VERSION" "Release $REPO_NAME $RELEASE_VERSION"
echo
