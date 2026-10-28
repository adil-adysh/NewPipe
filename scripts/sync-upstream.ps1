[CmdletBinding()]
param([switch]$Push)

$ErrorActionPreference = 'Stop'
function Invoke-Git {
    & git @args
    if ($LASTEXITCODE -ne 0) { throw "git $($args -join ' ') failed ($LASTEXITCODE)" }
}

$root = & git rev-parse --show-toplevel
if ($LASTEXITCODE -ne 0) { throw 'Run this command inside the NewPipe repository.' }
Push-Location $root
try {
    $branch = & git branch --show-current
    if ($branch -ne 'dev') { throw 'Switch to the local dev branch before syncing.' }
    $dirty = & git status --porcelain
    if ($LASTEXITCODE -ne 0 -or $dirty) {
        throw 'The working tree must be clean. Commit or stash your changes first.'
    }
    Invoke-Git fetch upstream dev
    Invoke-Git fetch origin dev
    & git merge-base --is-ancestor origin/dev HEAD
    $forkInLocal = $LASTEXITCODE -eq 0
    & git merge-base --is-ancestor origin/dev upstream/dev
    $forkInUpstream = $LASTEXITCODE -eq 0
    if (-not $forkInLocal -and -not $forkInUpstream) {
        throw 'origin/dev contains commits missing locally. Integrate them before syncing.'
    }
    & git rebase upstream/dev
    if ($LASTEXITCODE -ne 0) {
        throw 'Rebase stopped. Resolve conflicts and run git rebase --continue, or git rebase --abort.'
    }
    if ($Push) {
        Invoke-Git push --force-with-lease origin dev
    } else {
        Write-Host 'Synced locally. Run again with -Push to update the fork.'
    }
} finally {
    Pop-Location
}
