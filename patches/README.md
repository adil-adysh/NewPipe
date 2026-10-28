# Keeping the fork in sync

`upstream` is TeamNewPipe/NewPipe and `origin` is the personal fork. Both use
`dev` as their development branch. The numbered patches are snapshots of the
original accessibility and transcript commits. Keep the actual commits in the
fork: the sync command rebases those commits, rather than reapplying the patch
files on top of changes that are already present.

To update the local branch, first commit or stash all changes, then run:

```powershell
./scripts/sync-upstream.ps1
```

To also update the fork, use `./scripts/sync-upstream.ps1 -Push`. This rewrites
the fork's `dev` history with `--force-with-lease` after checking that the fork
has no commits missing from the local branch. Rebase conflicts stop the command;
resolve them and use `git rebase --continue`, or use `git rebase --abort`.
After resolving a conflict, rerun with `-Push` if you want to publish the result.

The GitHub Actions workflow checks upstream daily and can also be run manually.
When updates exist it proposes a pull request into the fork's `dev` branch;
it never updates `dev` directly. Merge conflicts fail the workflow and require
manual resolution. GitHub repository settings must allow Actions to create pull
requests and grant the workflow content and pull-request write permissions.

To apply the original patches to a fresh upstream checkout in PowerShell:

```powershell
$patchFiles = Get-ChildItem /path/to/patches -Filter '*.patch' |
    Sort-Object Name | ForEach-Object { $_.FullName }
git am $patchFiles
```

The patches are snapshots, not automatically updated by sync. Generate new
patches with `git format-patch` if you add or revise custom commits.
