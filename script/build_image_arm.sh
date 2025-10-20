#!/bin/bash

# 设置镜像名称和标签
IMAGE_NAME="query-service-arm-1"
IMAGE_TAG="latest"

# 构建项目
echo "正在构建 query 项目..."
mvn clean package -DskipTests

# 构建 Docker 镜像
echo "正在构建 Docker 镜像..."
docker build -f script/dockerfile.arm -t ${IMAGE_NAME}:${IMAGE_TAG} .

# 显示构建的镜像
echo "构建完成的镜像："
docker images | grep ${IMAGE_NAME}

echo "构建完成！"