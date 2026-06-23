# PyDroid AI IDE

一个功能完整的 Android Python IDE，支持代码编辑、终端运行、AI助手对话和文件管理。

## 功能特性

- **代码编辑器**：语法高亮、行号显示、自动补全
- **Python 终端**：直接在手机上运行 Python 脚本
- **AI 助手**：智能代码建议和错误修复（支持接入真实AI）
- **文件管理**：完整的文件浏览器

## 技术架构

- **双包结构**：
  - `com.pydroid.app` - 旧版 Fragment 架构
  - `com.example.pydroidide` - 新版 Activity 架构
- **编译版本**：API 28 (Android 9.0)
- **构建工具**：Gradle 7.2 + AGP 7.2.0

## 快速开始

### 方式一：使用 GitHub Actions 自动编译（推荐）

1. **创建 GitHub 仓库**
   ```bash
   # 在项目根目录执行
   git init
   git add .
   git commit -m "Initial commit: PyDroid AI IDE"
   ```

2. **推送到 GitHub**
   - 在 GitHub 上创建新仓库（例如 `PyDroidAIIDE`）
   - 推送代码：
     ```bash
     git remote add origin https://github.com/你的用户名/PyDroidAIIDE.git
     git branch -M main
     git push -u origin main
     ```

3. **触发自动编译**
   - 推送后自动触发 GitHub Actions
   - 或手动触发：GitHub 仓库 → Actions → Build Android APK → Run workflow

4. **下载 APK**
   - 进入 GitHub Actions 页面
   - 点击最新的构建任务
   - 在 "Artifacts" 区域下载 `PyDroid-AI-IDE-Debug`

### 方式二：本地 Android Studio 编译

1. 安装 [Android Studio](https://developer.android.com/studio)
2. 打开项目（选择 `Application` 文件夹）
3. 等待 Gradle 同步完成（自动下载依赖）
4. 点击 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
5. APK 生成在 `app/build/outputs/apk/debug/`

## 已知问题

- ✅ 已修复：TextWatcher 递归导致崩溃
- ✅ 已修复：R 类跨包引用错误
- ⚠️ 待完善：撤销/重做功能（当前为存根）
- ⚠️ 待完善：AI 助手需接入真实 LLM API

## 项目结构

```
Application/
├── app/
│   ├── src/main/java/
│   │   ├── com.pydroid.app/          # 旧版架构
│   │   └── com.example.pydroidide/   # 新版架构
│   ├── res/                          # 资源文件
│   └── build.gradle                  # 模块配置
├── build.gradle                      # 项目配置
├── gradle.properties                 # Gradle 属性
└── .github/workflows/build.yml       # GitHub Actions 配置
```

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！
