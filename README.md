# Sample Spring Boot App (ALB + EC2/ASG + RDS MySQL)

ALB ヘルスチェック対応 & RDS MySQL 接続確認ができるシンプルな Spring Boot アプリです。

## エンドポイント

| パス | 説明 |
|------|------|
| `/` | Hello メッセージ + ホスト名 (ASGでどのインスタンスか確認可能) |
| `/health` | ALB ヘルスチェック用 (`{"status":"UP"}`) |
| `/db` | RDS MySQL 接続確認 (`SELECT 1`) |

## EC2 でのセットアップ手順

### 前提
- Amazon Linux 2023 の EC2 インスタンス
- RDS MySQL が作成済み (エンドポイントを控えておく)
- セキュリティグループで EC2 → RDS の 3306 ポートが許可されている

### 手順

```bash
# ソースコードを EC2 に配置 (scp or git clone)
cd /home/ec2-user/sample-app

# 1. ランタイムほかインストール
bash scripts/install.sh

# 2. ビルド
bash scripts/build.sh

# 3. デプロイ (※ 先にスクリプト内のRDS接続情報を編集してください)
vi scripts/deploy.sh   # DB_HOST, DB_NAME, DB_USER, DB_PASS を設定
bash scripts/deploy.sh
```

### ALB ヘルスチェック設定

- ターゲットグループのヘルスチェックパス: `/health`
- ポート: `8080`
- 正常コード: `200`

## ファイル構成

```
sample-app/
├── pom.xml
├── scripts/
│   ├── install.sh    # 1. ランタイムほかインストール
│   ├── build.sh      # 2. ビルド
│   └── deploy.sh     # 3. デプロイ
└── src/main/
    ├── java/com/example/demo/
    │   ├── DemoApplication.java
    │   └── HelloController.java
    └── resources/
        └── application.properties
```
