#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
PROJECT_ROOT=$(cd "${SCRIPT_DIR}/.." && pwd)
cd "${PROJECT_ROOT}"

MVN_CMD=${MVN_CMD:-mvn}
IMAGE_NAME=${IMAGE_NAME:-query-service}
IMAGE_TAG=${IMAGE_TAG:-latest}
FULL_IMAGE_NAME="${IMAGE_NAME}:${IMAGE_TAG}"

echo "[1/2] Building application artifact with ${MVN_CMD}..."
${MVN_CMD} -B clean package -DskipTests

JAR_FILE=$(find target -maxdepth 1 -type f -name '*.jar' ! -name 'original-*' | head -n 1)
if [[ -z "${JAR_FILE}" ]]; then
  echo "无法在 target 目录中找到可用的 jar 包" >&2
  exit 1
fi

echo "[2/2] Building Docker image ${FULL_IMAGE_NAME} (using ${JAR_FILE})..."
docker build --build-arg JAR_FILE="${JAR_FILE}" -t "${FULL_IMAGE_NAME}" .

echo "镜像构建完成：${FULL_IMAGE_NAME}"
