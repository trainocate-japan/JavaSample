#!/bin/bash
# ============================================================
# 1. ランタイムほかインストール
#    EC2 (Amazon Linux 2023) に Java 17 と Maven をインストール
# ============================================================
set -e

echo "=== Installing Java 17 (Amazon Corretto) ==="
sudo dnf install -y java-17-amazon-corretto-devel

echo "=== Installing Maven ==="
sudo dnf install -y maven

echo "=== Installing Git ==="
sudo dnf install -y git

echo "=== Verifying installations ==="
java -version
mvn -version
git --version

echo "=== Installation complete ==="
