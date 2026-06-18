#!/bin/bash
# ============================================================
# 2. ビルド
#    Maven でアプリケーションをビルド (実行可能 JAR を生成)
# ============================================================
set -e

APP_DIR="/home/ec2-user/sample-app"

echo "=== Building application ==="
cd "$APP_DIR"
mvn clean package -DskipTests

echo "=== Build complete ==="
echo "JAR file: $APP_DIR/target/demo-1.0.0.jar"
ls -la target/demo-1.0.0.jar
