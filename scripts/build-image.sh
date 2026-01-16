#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
PROJECT_ROOT=$(cd "${SCRIPT_DIR}/.." && pwd)
cd "${PROJECT_ROOT}"

MVN_CMD=${MVN_CMD:-mvn}
IMAGE_NAME=${IMAGE_NAME:-query-service}
IMAGE_TAG=${IMAGE_TAG:-latest}
PLATFORM=${PLATFORM:-linux/amd64}  # 默认为 amd64 架构
FULL_IMAGE_NAME="${IMAGE_NAME}:${IMAGE_TAG}"
USE_CACHE=${USE_CACHE:-true}  # 默认使用缓存

# 显示使用方法
usage() {
  echo "用法: $0 [选项]"
  echo "选项:"
  echo "  --platform <platform>  指定构建平台 (默认: linux/amd64)"
  echo "                        可选值: linux/amd64, linux/arm64, linux/amd64,linux/arm64"
  echo "  --push                 推送镜像到仓库"
  echo "  --no-cache             不使用缓存构建"
  echo "  --offline              离线模式，仅使用本地镜像"
  echo "  --help                 显示此帮助信息"
  exit 1
}

# 解析命令行参数
PUSH_IMAGE=false
OFFLINE_MODE=false
while [[ $# -gt 0 ]]; do
  case $1 in
    --platform)
      PLATFORM="$2"
      shift 2
      ;;
    --push)
      PUSH_IMAGE=true
      shift
      ;;
    --no-cache)
      USE_CACHE=false
      shift
      ;;
    --offline)
      OFFLINE_MODE=true
      shift
      ;;
    --help)
      usage
      ;;
    *)
      echo "未知选项: $1"
      usage
      ;;
  esac
done

echo "[1/2] Building application artifact with ${MVN_CMD}..."
${MVN_CMD} -B clean package -DskipTests

JAR_FILE=$(find target -maxdepth 1 -type f -name '*.jar' ! -name 'original-*' | head -n 1)
if [[ -z "${JAR_FILE}" ]]; then
  echo "无法在 target 目录中找到可用的 jar 包" >&2
  exit 1
fi

echo "[2/2] Building Docker image ${FULL_IMAGE_NAME} for platform ${PLATFORM} (using ${JAR_FILE})..."
BUILD_ARGS="--platform ${PLATFORM} --build-arg JAR_FILE=${JAR_FILE} -t ${FULL_IMAGE_NAME}"
if [ "$USE_CACHE" = false ]; then
  BUILD_ARGS="${BUILD_ARGS} --no-cache"
fi

# 如果是离线模式，添加额外参数
if [ "$OFFLINE_MODE" = true ]; then
  BUILD_ARGS="${BUILD_ARGS} --no-cache"
  echo "使用离线模式构建..."
fi

if [ "$PUSH_IMAGE" = true ]; then
  # 使用 buildx 构建多平台镜像并推送
  docker buildx build ${BUILD_ARGS} . --push
else
  # 本地构建
  docker build ${BUILD_ARGS} .
fi

echo "镜像构建完成：${FULL_IMAGE_NAME} (${PLATFORM})"