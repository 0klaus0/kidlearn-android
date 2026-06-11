# ☀️ KidLearn 快乐学习 - 儿童 Android 应用

[![Android CI](https://github.com/0klaus0/kidlearn-android/actions/workflows/android.yml/badge.svg)](https://github.com/0klaus0/kidlearn-android/actions)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Min SDK](https://img.shields.io/badge/minSdk-26%20%7C%20Android%208.0-green.svg)](build.gradle)
[![Target SDK](https://img.shields.io/badge/targetSdk-34%20%7C%20Android%2014-orange.svg)](build.gradle)

一款面向 2-6 岁低龄儿童的 Android 学习娱乐应用，严格遵循儿童应用设计规范。

---

## 📥 最新 APK 下载

> **每次推送代码到 main 分支，GitHub Actions 会自动构建并生成 APK！**

### Debug APK（自动构建）
1. 访问 **Actions** 页面：https://github.com/0klaus0/kidlearn-android/actions
2. 点击左侧 **"🔨 Build Debug APK"** 工作流
3. 点击最新的 **Run**（绿色勾 ✅ 表示成功）
4. 滚动到页面底部 **Artifacts** 区域
5. 点击 **"app-debug-apk"** 下载 ZIP 包
6. 解压后得到 `app-debug.apk`，安装到手机即可测试

### Release APK（需配置签名，可选）
配置 keystore 后可通过手动触发 `workflow_dispatch` → 选择 `release` 构建。

---

## 🚀 快速开始

- **应用名称**：快乐学习（KidLearn）
- **目标年龄**：2-6 岁
- **最低 Android 版本**：Android 8.0（API 26）
- **目标 Android 版本**：Android 14（API 34）
- **应用包名**：com.kidlearn.app
- **开发语言**：Kotlin
- **应用大小**：约 30-40 MB（核心资源离线）

## ✨ 核心功能

### 1. 界面设计
- 所有按钮尺寸 >= 60dp，适合儿童小手操作
- 色彩鲜艳柔和，主色调（橙/黄/蓝/紫）
- 极简导航，单次点击直达功能模块
- 所有交互均有明确的视觉反馈（点击动画、颜色变化）

### 2. 核心学习内容
- **数字学习**（0-10）：配有标准中文发音、数字对应物品示例
- **字母学习**（A-Z）：配有英文发音、单词、配图 emoji
- **颜色学习**（8 种）：红橙黄绿青蓝紫粉，柔和饱和度
- **形状学习**（6 种）：圆、方形、三角、长方形、星形、心形

### 3. 年龄适配
- 2-3 岁：基础认知（0-5 数字、A-J 字母、4 种颜色、3 种形状）
- 4-5 岁：进阶内容（0-8 数字、A-R 字母、6 种颜色、5 种形状）
- 5-6 岁：全面挑战（所有内容）

### 4. 5 种互动游戏
- 🎯 **配对游戏**：数出物品数量并选择正确数字
- ✋ **拖拽游戏**：匹配颜色到目标区域
- 🧩 **拼图游戏**：按顺序点击数字完成拼图
- 🔊 **声音识别**：听发音选择正确的字母
- 🖍️ **涂色游戏**：选择颜色为图形上色

### 5. 奖励系统
- ⭐ 星星收集（学习任务/游戏均奖励）
- 🎁 贴纸解锁（按星星数量阶梯解锁）
- 🏆 成就解锁（学习里程碑）
- 🎉 每次完成任务触发正面鼓励动画

### 6. 家长控制（密码保护）
- 🔒 默认密码：1234（可修改 4-8 位数字）
- ⏱ 时间限制：每日/每周时长控制（10-120 分钟可选）
- 📊 学习进度：按知识点、时间维度生成报表
- 📚 内容管理：单独启用/禁用各模块
- 🔊 发音控制：可开关 TTS 语音

### 7. 音频与交互
- 使用系统 Text-To-Speech 引擎发音（中文/英文）
- 音量独立控制，支持完全静音
- 触摸响应延迟 < 100ms，动画帧率 30+ fps
- 所有音效为柔和音调，避免尖锐刺激

### 8. 性能与安全
- 包体大小控制在 50MB 以内
- 无第三方 SDK、无广告、无数据收集
- 完全离线运行，所有核心资源本地存储
- 符合 Google Play 儿童应用规范（COPPA）

## 📂 项目结构

```
childapp/
├── build.gradle                    # 项目级 Gradle 配置
├── settings.gradle                 # 模块设置
├── gradle.properties               # Gradle 属性
├── gradle/wrapper/                 # Gradle 包装器
├── README.md                       # 本文件
└── app/                            # 应用主模块
    ├── build.gradle                # 应用级 Gradle 配置
    └── src/main/
        ├── AndroidManifest.xml     # 应用清单（含所有 Activity 声明）
        ├── java/com/kidlearn/app/
        │   ├── DataModels.kt       # 数据模型（数字/字母/颜色/形状/成就）
        │   ├── AppPreferences.kt   # 偏好设置与数据持久化
        │   ├── AudioManager.kt     # TTS 音频管理
        │   ├── AgeSelectActivity.kt  # 年龄选择入口
        │   ├── MainMenuActivity.kt   # 主菜单（功能导航）
        │   ├── NumbersActivity.kt    # 数字学习
        │   ├── LettersActivity.kt    # 字母学习
        │   ├── ColorsActivity.kt     # 颜色学习
        │   ├── ShapesActivity.kt     # 形状学习
        │   ├── GamesActivity.kt      # 游戏选择菜单
        │   ├── RewardsActivity.kt    # 奖励/成就展示
        │   ├── games/
        │   │   ├── MatchingGameActivity.kt     # 配对游戏
        │   │   ├── DragDropGameActivity.kt     # 拖拽游戏
        │   │   ├── PuzzleGameActivity.kt       # 拼图游戏
        │   │   ├── SoundGameActivity.kt        # 声音识别
        │   │   └── ColorGameActivity.kt        # 涂色游戏
        │   └── parent/
        │       ├── ParentLoginActivity.kt      # 家长登录（密码）
        │       ├── ParentDashboardActivity.kt  # 家长控制面板
        │       ├── TimeLimitActivity.kt        # 时间限制设置
        │       ├── ProgressActivity.kt         # 学习进度报表
        │       └── ContentManageActivity.kt    # 内容管理
        └── res/
            ├── anim/                  # 动画资源（脉冲、弹跳、庆祝）
            ├── drawable/              # 背景与按钮样式
            ├── mipmap-anydpi-v26/     # 自适应应用图标
            ├── layout/                # 所有界面布局文件
            └── values/                # 颜色、字符串、尺寸、主题
                ├── colors.xml
                ├── strings.xml
                ├── dimens.xml
                └── themes.xml
```

## 🚀 构建与运行

### 前置条件
- Android Studio Giraffe / Iguana 或更高版本
- JDK 17 或更高（项目已配置为使用 JDK 17）
- Android SDK 26+（目标 SDK 34）
- 至少一台 Android 8.0+ 设备或模拟器

### 构建步骤
1. 使用 Android Studio 打开项目目录 `childapp/`
2. 等待 Gradle 同步完成
3. 连接 Android 设备或启动模拟器
4. 点击 Run（▶ 按钮）或执行：
   ```bash
   ./gradlew assembleDebug    # Linux/Mac
   # 或
   gradlew.bat assembleDebug  # Windows
   ```
5. 生成的 APK 位于：`app/build/outputs/apk/debug/app-debug.apk`

### 发布版本构建
```bash
./gradlew assembleRelease
# 生成文件：app/build/outputs/apk/release/app-release.apk
```

## 🧪 测试建议

1. **多设备兼容性**：
   - 手机：5.5" / 6.2" / 6.7"
   - 平板：10" / 11"
   - 密度：mdpi、hdpi、xxhdpi、xxxhdpi
   - Android 8.0 / 10 / 12 / 14

2. **用户体验测试**：
   - 邀请目标年龄段儿童使用 3 轮（每轮 30 分钟）
   - 观察操作流畅度、兴趣保持时间
   - 确认按钮大小是否易于点击
   - 记录完成每个任务所需时间

3. **功能测试**：
   - 所有学习模块内容显示与发音
   - 5 种游戏玩法正确性
   - 奖励系统正常触发
   - 家长密码保护（输入错误拦截）
   - 时间限制触发后提示
   - 离线模式可用性

4. **性能测试**：
   - 内存占用：启动后 < 150MB
   - 冷启动时间：< 2 秒
   - 动画帧率：> 30 fps
   - 触摸响应：< 100ms

5. **安全与合规**：
   - 无网络请求（核心功能）
   - 无第三方 SDK / 广告 SDK
   - 无个人数据收集
   - 所有界面元素符合儿童安全规范（无锐利边缘视觉、无暴力/惊悚内容）

## ⚠️ 使用说明

1. **首次启动**：选择儿童年龄组 → 自动进入主菜单
2. **切换年龄**：在主菜单点击右上角齿轮图标（⚙️），或在年龄选择页选择
3. **家长中心**：点击主菜单齿轮 → 输入家长密码（默认 1234）→ 可进行时间限制、进度查看、内容管理等设置
4. **音量控制**：每个界面均有 🔊 / 🔇 按钮，可切换语音发音
5. **重置数据**：卸载应用后重新安装即可清除所有数据与密码

## 📖 用户手册摘要

### 儿童使用指南
- 点击大按钮进入各学习模块
- 每个项目点击后会有语音讲解与动画奖励
- 完成任务会获得星星 ⭐
- 星星累积到一定数量会解锁新贴纸和成就

### 家长使用指南
- 首次使用请在年龄选择页选择合适年龄组
- 点击主菜单右上角齿轮图标进入家长中心
- 默认密码 1234，建议首次进入后立即修改
- 在"时间限制"中设置每日可用时长（推荐 30 分钟/天）
- 在"学习进度"中查看儿童学习数据与总体评价
- 在"内容管理"中可单独启用/禁用模块

### 常见问题
- **没有声音？** → 检查设备音量开关和应用内 🔊 按钮是否开启，确保系统 TTS 引擎可用
- **密码忘记？** → 默认密码 1234。如已修改，请卸载并重新安装应用
- **想要重置星星？** → 重新安装应用，或在家长中心"内容管理"页底部操作

## 📜 版权与许可

本应用所有内容原创：
- 图形、颜色、布局均为自有设计
- 使用系统 TTS 引擎发音（无需外部音频文件）
- Emoji 图标为 Unicode 字符

**无第三方版权内容，无广告，无数据收集。**

---

© 2025 KidLearn. 本项目为示例项目。
