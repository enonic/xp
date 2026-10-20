#!/usr/bin/env bash
# Usage: npm-publish.sh <package-dir> <version> [--require-dependencies]
set -euo pipefail

DIR=$1
VERSION=$2
REQUIRE_DEPENDENCIES=${3:-}
NAME=$(jq -r .name "$DIR/package.json")

if [[ "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  NPM_TAG=latest
else
  NPM_TAG=beta
fi

# Only E404 means the version is missing; any other failure must not fall through to a publish.
is_published() {
  local output
  if output=$(npm view "$1" version --prefer-online 2>&1); then
    return 0
  fi
  if [[ "$output" == *E404* ]]; then
    return 1
  fi
  echo "::error::npm view $1 failed"
  echo "$output"
  exit 1
}

if is_published "$NAME@$VERSION"; then
  echo "$NAME@$VERSION is already published"
  exit 0
fi

if [[ "$REQUIRE_DEPENDENCIES" == --require-dependencies ]]; then
  DEPENDENCIES=()
  while IFS= read -r dependency; do
    DEPENDENCIES+=("$dependency")
  done < <(jq -r '.dependencies // {} | to_entries[] | "\(.key)@\(.value)"' "$DIR/package.json")
  for attempt in 1 2 3 4 5; do
    MISSING=()
    for dependency in "${DEPENDENCIES[@]}"; do
      is_published "$dependency" || MISSING+=("$dependency")
    done
    if [[ ${#MISSING[@]} -eq 0 ]]; then
      break
    fi
    echo "Attempt $attempt: ${#MISSING[@]} dependencies are not on the registry yet"
    if [[ $attempt -lt 5 ]]; then
      sleep 15
    fi
  done
  if [[ ${#MISSING[@]} -gt 0 ]]; then
    echo "::error::$NAME@$VERSION depends on packages that are not on the registry"
    printf '%s\n' "${MISSING[@]}"
    exit 1
  fi
fi

npm publish "$DIR" --provenance --access public --tag "$NPM_TAG"
