# KidLearn - Push to GitHub
# Run this script in PowerShell:
#   powershell -ExecutionPolicy Bypass -File push_to_github.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  KidLearn Android - Push to GitHub" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Set-Location "d:\childapp"

# Step 1: Check if .git exists
if (-not (Test-Path ".git")) {
    Write-Host "[1/4] Initializing git repository..." -ForegroundColor Yellow
    git init
    git remote add origin https://github.com/0klaus0/kidlearn-android.git
} else {
    Write-Host "[1/4] Git repository exists" -ForegroundColor Green
}

# Step 2: Fetch latest from GitHub
Write-Host "[2/4] Fetching latest from GitHub..." -ForegroundColor Yellow
git fetch origin

# Step 3: Reset to GitHub version and apply our workflow
try {
    git checkout origin/main -- .
    Write-Host "  Reset to GitHub version" -ForegroundColor Green
} catch {
    Write-Host "  Note: First push" -ForegroundColor Gray
}

# Step 4: Copy our updated workflow
$ourWorkflow = Get-Content ".github\workflows\android.yml" -Raw
if ($ourWorkflow -match "Create GitHub Release") {
    Write-Host "[3/4] Workflow already has Release feature" -ForegroundColor Green
} else {
    Write-Host "[3/4] Workflow needs update" -ForegroundColor Yellow
}

# Step 4: Commit and push
Write-Host "[4/4] Committing and pushing to GitHub..." -ForegroundColor Yellow
git add .github\workflows\android.yml
git commit -m "feat: auto-create GitHub Release with APKs" 2>&1 | Out-Null
git push -u origin main 2>&1

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Done! Check GitHub Actions page:" -ForegroundColor Green
Write-Host "  https://github.com/0klaus0/kidlearn-android/actions" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
