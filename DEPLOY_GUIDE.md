# 推送 GitHub 與構建 APK 完整指南

> 適用於本機（用戶電腦）執行的步驟。當前 Trae 沙箱環境網絡受限、無法直接推送。

---

## 一、推送項目到 GitHub

### 方式 A：使用 GitHub CLI（推薦，最簡單）

#### 1. 安裝 GitHub CLI（僅需一次）
- **Windows（推薦）**：
  ```powershell
  winget install --id GitHub.cli
  ```
  或從 https://cli.github.com/ 下載安裝包

- **macOS**：
  ```bash
  brew install gh
  ```

#### 2. 登錄 GitHub
```powershell
gh auth login
```
按提示選擇：
- `GitHub.com`
- `HTTPS`
- `Login with a web browser`（推薦）或 `Paste an authentication token`

#### 3. 創建倉庫並推送
在 `d:\childapp` 目錄下執行：
```powershell
cd d:\childapp
gh repo create kidlearn-android --public --source=. --remote=origin --push
```

倉庫名稱可自定，例如：
- `kidlearn-android`（推薦）
- `KidLearn`
- `happy-learning`

#### 4. 完成！訪問您的倉庫
推送成功後會輸出類似：
```
✓ Created repository 0klaus0/kidlearn-android on GitHub
✓ Added remote https://github.com/0klaus0/kidlearn-android.git
✓ Pushed commits to https://github.com/0klaus0/kidlearn-android.git
```
訪問 https://github.com/0klaus0/kidlearn-android 即可看到項目。

---

### 方式 B：使用 Git 命令行 + Personal Access Token

#### 1. 創建 GitHub 倉庫
1. 打開 https://github.com/new
2. 填寫：
   - Repository name: `kidlearn-android`（或自定）
   - Description: `面向 2-6 岁儿童的 Android 学习娱乐应用`
   - 選擇 `Public`（公開）
   - **不要**勾選 "Add a README" / "Add .gitignore" / "Choose a license"
3. 點擊 `Create repository`

#### 2. 推送本地倉庫
在 `d:\childapp` 目錄下執行：
```powershell
cd d:\childapp
git remote add origin https://github.com/0klaus0/kidlearn-android.git
git push -u origin main
```

#### 3. 認證
首次推送會要求輸入 GitHub 用戶名和密碼：
- **Username**: `0klaus0`
- **Password**: 輸入 Personal Access Token（不是登錄密碼）

#### 4. 創建 Personal Access Token（如需要）
1. 訪問 https://github.com/settings/tokens/new
2. Note: `kidlearn-push`
3. Expiration: `No expiration` 或自定
4. Scopes: 勾選 `repo`
5. 點擊 `Generate token`
6. 複製 token（只顯示一次），粘貼為密碼

---

## 二、構建 APK 文件

### 方式 A：使用 Android Studio（最簡單，強烈推薦）

#### 1. 下載並安裝 Android Studio
- 官網：https://developer.android.com/studio
- 下載後雙擊安裝包，按默認設置安裝
- 首次啟動會自動下載 Android SDK（耗時 10-30 分鐘）

#### 2. 打開項目
1. 啟動 Android Studio
2. 點擊 `File` → `Open` → 選擇 `d:\childapp` 目錄
3. 等待 Gradle 同步完成（首次會下載約 300MB 依賴）

#### 3. 構建 Debug APK
1. 菜單 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
2. 等待構建完成（首次 3-5 分鐘）
3. 構建完成後會彈出通知："APK(s) generated successfully"
4. 點擊 `locate` 打開文件夾
5. APK 位置：`d:\childapp\app\build\outputs\apk\debug\app-debug.apk`

#### 4. 構建 Release APK（未簽名）
1. 菜單 `Build` → `Generate Signed Bundle / APK`
2. 選擇 `APK`，點擊 `Next`
3. 點擊 `Create new...` 創建 keystore：
   - Path: `D:\keystore\kidlearn.jks`
   - Password: 設置一個強密碼
   - Validity: 25
   - 填寫姓名、組織等信息
4. 點擊 `Next`，選擇 `release`
5. 點擊 `Create`，等待構建
6. APK 位置：`d:\childapp\app\release\app-release.apk`

---

### 方式 B：使用命令行 Gradle

#### 1. 安裝 JDK 17
```powershell
winget install Microsoft.OpenJDK.17
```
或從 https://adoptium.net/ 下載

#### 2. 設置環境變量
```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot"
$env:Path += ";$env:JAVA_HOME\bin"
[Environment]::SetEnvironmentVariable("JAVA_HOME", $env:JAVA_HOME, "User")
[Environment]::SetEnvironmentVariable("Path", $env:Path, "User")
```

驗證：
```powershell
java -version
```
應輸出 `openjdk version "17.x.x"`

#### 3. 下載 Android SDK Command Line Tools
從 https://developer.android.com/studio#command-line-tools-only 下載，並解壓到 `D:\Android\Sdk`

#### 4. 安裝 Android SDK 組件
```powershell
$env:ANDROID_HOME = "D:\Android\Sdk"
& "$env:ANDROID_HOME\cmdline-tools\latest\bin\sdkmanager.bat" --licenses
& "$env:ANDROID_HOME\cmdline-tools\latest\bin\sdkmanager.bat" "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

#### 5. 下載 Gradle Wrapper
本項目已包含 `gradle/wrapper/gradle-wrapper.properties`，Android Studio 會自動下載。命令行方式：
```powershell
cd d:\childapp
# 下載 gradlew.bat
Invoke-WebRequest -Uri "https://services.gradle.org/distributions/gradle-8.4-bin.zip" -OutFile "$env:TEMP\gradle.zip"
Expand-Archive "$env:TEMP\gradle.zip" -DestinationPath "C:\Gradle"
$env:Path += ";C:\Gradle\gradle-8.4\bin"
```

#### 6. 構建 APK
```powershell
cd d:\childapp
gradle assembleDebug
# 或 release 版本
gradle assembleRelease
```

構建完成後：
- Debug APK: `app\build\outputs\apk\debug\app-debug.apk`
- Release APK: `app\build\outputs\apk\release\app-release.apk`

---

### 方式 C：使用 GitHub Actions 雲端構建（無需本地環境）

#### 1. 推送項目到 GitHub（先執行上面步驟一）

#### 2. 創建 GitHub Actions 工作流
在項目根目錄創建 `.github/workflows/build.yml`：

```yaml
name: Android Build

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
  workflow_dispatch:    # 允許手動觸發

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout source
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Build Debug APK
        run: ./gradlew assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/app-debug.apk
          retention-days: 30
```

#### 3. 提交並推送
```powershell
cd d:\childapp
git add .github\workflows\build.yml
git commit -m "ci: add GitHub Actions Android build workflow"
git push
```

#### 4. 下載構建好的 APK
1. 訪問 https://github.com/0klaus0/kidlearn-android/actions
2. 點擊最新的 workflow run
3. 滾動到底部 `Artifacts`，下載 `app-debug.zip`
4. 解壓後即可獲得 `app-debug.apk`

---

## 三、安裝到設備測試

### 使用 ADB（最簡單）
```powershell
# 1. 手機開啟"USB 調試"
# 2. 連接手機
adb devices   # 確認設備已連接
adb install app\build\outputs\apk\debug\app-debug.apk
```

### 直接安裝
1. 將 `app-debug.apk` 複製到手機
2. 手機打開"未知來源"安裝權限
3. 點擊 APK 文件安裝

---

## 四、常見問題

### Q1: 推送時報錯 "Permission denied (publickey)"
**A**: HTTPS 推送使用 Personal Access Token；SSH 推送需要在 https://github.com/settings/keys 添加 SSH 公鑰。

### Q2: 構建時報錯 "Could not resolve all dependencies"
**A**: 網絡問題，檢查：
- 是否配置了 Maven 鏡像（如阿里雲）
- 代理設置
- 防火牆是否阻擋 Maven Central 與 Google Maven

### Q3: Gradle 下載很慢
**A**: 在 `gradle.properties` 或 `~/.gradle/init.gradle` 中配置鏡像：
```gradle
allprojects {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'https://maven.aliyun.com/repository/public' }
        google()
        mavenCentral()
    }
}
```

### Q4: 構建成功但運行時崩潰
**A**: 檢查設備 Android 版本是否 >= 8.0（API 26）。可在 Android Studio 中按 `Shift+F10` 運行並查看 Logcat。

---

## 五、發布到 Google Play

完成上述步驟獲得簽名後的 release APK 後：

1. 訪問 https://play.google.com/console
2. 創建應用（付費帳號 $25 一次性）
3. 上傳 AAB（推薦）：用 `gradle bundleRelease` 生成 `app-release.aab`
4. 填寫應用商店資料：截圖、描述、年齡分級、隱私政策
5. 由於是兒童應用，需特別填寫：
   - **Target audience**: Children (5 and under) / Family
   - **Designed for Families**: Yes
   - **Ads**: No, no ads
   - **Data collection**: None
   - 提供 COPPA 合規聲明

---

**完成推送和構建後，您會獲得：**
1. 🌐 GitHub 倉庫 URL（用於分享、版本管理）
2. 📦 `app-debug.apk`（直接安裝到設備測試）
3. 📦 `app-release.apk`（用於上架應用商店）

祝您開發順利！🎉
