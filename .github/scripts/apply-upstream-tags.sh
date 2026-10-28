#!/usr/bin/env bash
set -euo pipefail

# Origin's tags are the checkpoints: a tag is copied only with its patched branch.
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
cp patches/*.patch "$tmp/"
git ls-remote --tags --refs upstream > "$tmp/upstream-tags"
git ls-remote --tags --refs origin > "$tmp/origin-tags"
git fetch --no-tags upstream '+refs/tags/*:refs/upstream-tags/*'
git config user.name 'github-actions[bot]'
git config user.email '41898282+github-actions[bot]@users.noreply.github.com'

declare -A origin_tags
while read -r sha ref; do
    origin_tags["$ref"]="$sha"
done < "$tmp/origin-tags"

while read -r sha ref; do
    if [[ -n "${origin_tags[$ref]+present}" ]]; then
        if [[ "${origin_tags[$ref]}" != "$sha" ]]; then
            echo "Tag $ref differs between upstream and the fork; refusing to overwrite it." >&2
            exit 1
        fi
        continue
    fi
    tag=${ref#refs/tags/}
    branch="refs/heads/patched/$tag"
    if [[ -n "$(git ls-remote --heads origin "$branch")" ]]; then
        echo "Branch $branch already exists; refusing to overwrite it." >&2
        exit 1
    fi
    echo "Applying patches to upstream tag $tag"
    git checkout --detach "refs/upstream-tags/$tag^{commit}"
    if ! git am "$tmp"/*.patch; then
        echo "Patches failed for $tag; no branch or tag was pushed." >&2
        exit 1
    fi
    git push --atomic origin "HEAD:$branch" "refs/upstream-tags/$tag:$ref"
done < "$tmp/upstream-tags"
