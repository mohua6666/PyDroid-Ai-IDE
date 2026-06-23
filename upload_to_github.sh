#!/bin/bash

# PyDroid AI IDE - GitHub 上传脚本
# 使用方法：./upload_to_github.sh <你的GitHub用户名> <仓库名>

if [ $# -ne 2 ]; then
    echo "使用方法: $0 <GitHub用户名> <仓库名>"
    echo "示例: $0 yourname PyDroidAIIDE"
    exit 1
fi

USERNAME=$1
REPO_NAME=$2

echo "================================================"
echo "PyDroid AI IDE - GitHub 上传工具"
echo "================================================"
echo ""
echo "GitHub 用户名: $USERNAME"
echo "仓库名称: $REPO_NAME"
echo ""

# 检查是否已安装 git
if ! command -v git &> /dev/null; then
    echo "❌ 错误: 未安装 Git"
    echo "请先安装 Git: https://git-scm.com/downloads"
    exit 1
fi

# 初始化 Git 仓库
echo "📦 初始化 Git 仓库..."
git init
git add .
git commit -m "Initial commit: PyDroid AI IDE with GitHub Actions"

# 添加远程仓库
echo ""
echo "🔗 添加 GitHub 远程仓库..."
git remote remove origin 2>/dev/null
git remote add origin https://github.com/$USERNAME/$REPO_NAME.git

# 推送到 GitHub
echo ""
echo "🚀 准备推送到 GitHub..."
echo ""
echo "⚠️  注意事项："
echo "1. 请确保已在 GitHub 上创建仓库: $REPO_NAME"
echo "2. 如果是第一次使用，需要输入 GitHub 用户名和密码"
echo "3. 建议使用 Personal Access Token (PAT) 作为密码"
echo ""
read -p "按 Enter 继续，或按 Ctrl+C 取消..."

git branch -M main
git push -u origin main

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 成功推送到 GitHub!"
    echo ""
    echo "📱 下一步："
    echo "1. 访问: https://github.com/$USERNAME/$REPO_NAME"
    echo "2. 点击 'Actions' 标签页"
    echo "3. 等待构建完成（约 5-10 分钟）"
    echo "4. 下载 APK: Actions → 最新构建 → Artifacts"
else
    echo ""
    echo "❌ 推送失败"
    echo ""
    echo "💡 可能的原因："
    echo "1. 仓库不存在 - 请先在 GitHub 上创建"
    echo "2. 认证失败 - 请使用 Personal Access Token"
    echo "3. 网络问题 - 请检查网络连接"
    echo ""
    echo "🔗 手动创建仓库: https://github.com/new"
fi
