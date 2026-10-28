# Applying patches to upstream releases

`upstream` is TeamNewPipe/NewPipe and `origin` is the personal fork. The
numbered files in this directory are snapshots of the accessibility and
transcript commits, applied in filename order.

The GitHub Actions workflow checks upstream tags daily (09:30 India time), or
when run manually. GitHub does not send upstream tag events directly to a fork,
so the check must poll. If every upstream tag is already in the fork, the
workflow does nothing. The fork initially contains all upstream tags, so old
releases are not processed again.

For each new upstream tag, the workflow checks out the exact tagged commit,
applies the patches with `git am`, and atomically pushes the original tag and a
`patched/<tag>` branch to the fork. It does not change the fork's `dev` branch.
Existing tags and branches are never overwritten. If a patch fails, nothing is
pushed for that tag; inspect the failed Actions run and update the patches on
`dev` before retrying. The workflow requires GitHub Actions contents write
permission; it does not require permission to create pull requests.

The patches are snapshots, not automatically updated when custom code changes.
Update them deliberately on `dev` when adding or changing customizations.
To apply the snapshots manually in a fresh upstream checkout using PowerShell:

```powershell
$patchFiles = Get-ChildItem /path/to/patches -Filter '*.patch' |
    Sort-Object Name | ForEach-Object { $_.FullName }
git am $patchFiles
```
