#!/bin/bash
# ============================================================
# 3. デプロイ
#    アプリケーションを起動 (既存プロセスがあれば停止してから起動)
# ============================================================
set -e

APP_DIR="/home/ec2-user/sample-app"
JAR_FILE="$APP_DIR/target/demo-1.0.0.jar"
LOG_FILE="/home/ec2-user/app.log"

# --- RDS接続情報 (環境に合わせて変更してください) ---
export DB_HOST="YOUR-RDS-ENDPOINT"
export DB_NAME="mydb"
export DB_USER="admin"
export DB_PASS="password"

echo "=== Stopping existing application (if running) ==="
pkill -f "demo-1.0.0.jar" || true
sleep 2

echo "=== Starting application ==="
nohup java -jar "$JAR_FILE" \
  --spring.datasource.url="jdbc:mysql://${DB_HOST}:3306/${DB_NAME}?useSSL=false&allowPublicKeyRetrieval=true" \
  --spring.datasource.username="${DB_USER}" \
  --spring.datasource.password="${DB_PASS}" \
  > "$LOG_FILE" 2>&1 &

echo "=== Waiting for startup ==="
sleep 5

echo "=== Health check ==="
curl -s http://localhost:8080/health && echo ""

echo "=== Deploy complete ==="
echo "Logs: tail -f $LOG_FILE"
