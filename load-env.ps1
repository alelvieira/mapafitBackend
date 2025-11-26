<#
PowerShell helper to load variables from a .env file into the current session.
Usage (from project root):
  .\load-env.ps1
This will set environment variables for the current PowerShell session.
#>

$envFile = Join-Path (Get-Location) '.env'
if (-not (Test-Path $envFile)) {
    Write-Host ".env file not found. Copy .env.example to .env and fill values." -ForegroundColor Yellow
    return
}

Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -eq '' -or $line.StartsWith('#')) { return }
    $parts = $line -split '=', 2
    if ($parts.Count -ne 2) { return }
    $name = $parts[0].Trim()
    $value = $parts[1].Trim()
    # Remove surrounding quotes if present
    if ($value.StartsWith('"') -and $value.EndsWith('"')) {
        $value = $value.Trim('"')
    }
    if ($value.StartsWith("'") -and $value.EndsWith("'")) {
        $value = $value.Trim("'")
    }
    Write-Host "Setting $name" -ForegroundColor Green
    Set-Item -Path Env:\$name -Value $value
}

Write-Host "Loaded environment variables from .env into current session." -ForegroundColor Green

