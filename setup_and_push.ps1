# ============================================================
# KidLearn Android - 一鍵推送到 GitHub + 觸發雲端構建
# ============================================================
# 使用方法：
#   1. 保存此文件為 setup_and_push.ps1（編碼：UTF-8 with BOM）
#   2. 右鍵 → 使用 PowerShell 運行
#   3. 首次需要 GitHub Personal Access Token（見說明）
# ============================================================

param(
    [string]$RepoName = "kidlearn-android",   # 倉庫名稱，可自定義
    [string]$Description = "面向 2-6 岁儿童的 Android 学习娱乐应用"
)

$ErrorActionPreference = "Stop"
$repoOwner = "0klaus0"  # ⚠️  如果你的 GitHub 用戶名不同，請修改這裡

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  ☀️  KidLearn Android 一鍵部署腳本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# ---- 檢查 Git ----
Write-Host "[1/5] 檢查 Git..." -ForegroundColor Yellow
try {
    $gitVersion = git --version 2>&1
    Write-Host "  ✅ Git 已安裝: $gitVersion" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Git 未找到，請先安裝 Git: https://git-scm.com" -ForegroundColor Red
    exit 1
}

# ---- 檢查 curl ----
Write-Host "[2/5] 檢查 curl..." -ForegroundColor Yellow
try {
    $curlVersion = curl --version 2>&1 | Select-Object -First 1
    Write-Host "  ✅ curl 可用: $curlVersion" -ForegroundColor Green
} catch {
    Write-Host "  ❌ curl 不可用" -ForegroundColor Red
    exit 1
}

# ---- 獲取 GitHub Token ----
Write-Host ""
Write-Host "[3/5] GitHub 認證" -ForegroundColor Yellow
$token = $env:GITHUB_TOKEN
if ([string]::IsNullOrEmpty($token)) {
    Write-Host ""
    Write-Host "  📋 請提供 GitHub Personal Access Token" -ForegroundColor Cyan
    Write-Host "  ------------------------------------------------" -ForegroundColor Cyan
    Write-Host "  創建方法：" -ForegroundColor White
    Write-Host "    1. 訪問 https://github.com/settings/tokens/new" -ForegroundColor White
    Write-Host "    2. Note: kidlearn-deploy" -ForegroundColor White
    Write-Host "    3. Expiration: 30 days（或自定）" -ForegroundColor White
    Write-Host "    4. Scopes: 勾選 ✅ repo（全部）" -ForegroundColor White
    Write-Host "    5. 點擊 Generate token，複製 token" -ForegroundColor White
    Write-Host ""
    Write-Host "  或訪問 https://github.com/settings/tokens 查看已有 token" -ForegroundColor White
    Write-Host ""
    $token = Read-Host "  粘貼 GitHub Token（不會顯示）" -AsSecureString
    $token = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto(
        [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($token)
    )
}

# 測試 Token 是否有效
Write-Host "  測試 Token..." -ForegroundColor Gray
$testResult = curl -s -o /dev/null -w "%{http_code}" `
    -H "Authorization: token $token" `
    https://api.github.com/user
if ($testResult -ne "200") {
    Write-Host "  ❌ Token 無效或已過期（HTTP $testResult）" -ForegroundColor Red
    Write-Host "  請重新創建 Token 或檢查權限" -ForegroundColor Red
    exit 1
}
Write-Host "  ✅ Token 驗證通過" -ForegroundColor Green

# ---- 創建 GitHub 倉庫 ----
Write-Host ""
Write-Host "[4/5] 創建 GitHub 倉庫 '$repoOwner/$RepoName'..." -ForegroundColor Yellow

$createBody = @{
    name        = $RepoName
    description = $Description
    homepage    = "https://github.com/$repoOwner/$RepoName"
    private     = $false
    has_issues  = $true
    has_wiki    = $false
    has_downloads = $false
    auto_init   = $false  # 我們已有代碼，不初始化 README
} | ConvertTo-Json

$createResult = curl -s -w "%{http_code}" -o "$env:TEMP\gh_create.json" `
    -X POST `
    -H "Authorization: token $token" `
    -H "Content-Type: application/json" `
    -d $createBody `
    https://api.github.com/user/repos

$httpCode = $createResult
$response = Get-Content "$env:TEMP\gh_create.json" -Raw -ErrorAction SilentlyContinue | ConvertFrom-Json

if ($httpCode -eq "201") {
    Write-Host "  ✅ 倉庫創建成功！" -ForegroundColor Green
    $cloneUrl = $response.clone_url
    Write-Host "  📍 URL: $cloneUrl" -ForegroundColor Cyan
} elseif ($httpCode -eq "422") {
    Write-Host "  ⚠️  倉庫已存在（422 Unprocessable Entity），將直接推送..." -ForegroundColor Yellow
    $cloneUrl = "https://github.com/$repoOwner/$RepoName.git"
} else {
    Write-Host "  ❌ 創建倉庫失敗 (HTTP $httpCode)" -ForegroundColor Red
    Write-Host "  回應: $response" -ForegroundColor Red
    exit 1
}

# ---- 推送代碼 ----
Write-Host ""
Write-Host "[5/5] 推送代碼到 GitHub..." -ForegroundColor Yellow

Set-Location $PSScriptRoot

# 添加遠程倉庫
git remote remove origin 2>$null | Out-Null
git remote add origin "$cloneUrl" 2>$null | Out-Null

# 設置倉庫 URL 包含 Token（用於 HTTPS 推送）
git remote set-url origin "https://x-access-token:$token@github.com/$repoOwner/$RepoName.git" 2>$null | Out-Null

# 推送
Write-Host "  正在推送 $((git log --oneline | Measure-Object).Count) 個 commit..." -ForegroundColor Gray
$pushResult = git push -u origin main 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "  ✅ 推送成功！" -ForegroundColor Green
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "  🎉 部署完成！" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  🌐 倉庫地址：" -ForegroundColor White
    Write-Host "    https://github.com/$repoOwner/$RepoName" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  ⏳ GitHub Actions 將自動開始構建..." -ForegroundColor Yellow
    Write-Host "    大約 5-10 分鐘後可在以下地址下載 APK：" -ForegroundColor Yellow
    Write-Host "    https://github.com/$repoOwner/$RepoName/actions" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  📥 APK 下載步驟：" -ForegroundColor White
    Write-Host "    1. 打開上面的 Actions 鏈接" -ForegroundColor White
    Write-Host "    2. 點擊 '🔨 Build Debug APK'" -ForegroundColor White
    Write-Host "    3. 點擊綠色 ✅ 的 Run" -ForegroundColor White
    Write-Host "    4. 滾動到底部，點擊 'app-debug-apk' 下載" -ForegroundColor White
    Write-Host ""
    Write-Host "  📋 首次 Release 構建（如需上架）：" -ForegroundColor Yellow
    Write-Host "    見 DEPLOY_GUIDE.md 或 README.md 中的說明" -ForegroundColor Yellow
    Write-Host ""
} else {
    Write-Host "  ❌ 推送失敗！" -ForegroundColor Red
    Write-Host "  $pushResult" -ForegroundColor Red
    Write-Host ""
    Write-Host "  常見問題：" -ForegroundColor Yellow
    Write-Host "    - Token 權限不足 → 確保勾選了 repo scope" -ForegroundColor White
    Write-Host "    - 倉庫名衝突 → 嘗試修改腳本中的 \$RepoName" -ForegroundColor White
    Write-Host "    - Token 過期 → 重新創建並重新運行腳本" -ForegroundColor White
    exit 1
}
