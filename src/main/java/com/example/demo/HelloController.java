package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@RestController
public class HelloController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * ALB ヘルスチェック用 (シンプルに200を返す)
     */
    @GetMapping("/health")
    public String health() {
        return "{\"status\":\"UP\"}";
    }

    /**
     * トップページ - DB接続確認 + インスタンス情報表示
     */
    @GetMapping(value = "/", produces = "text/html; charset=UTF-8")
    public String index() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<title>Sample App</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }");
        html.append("h1 { color: #232f3e; }");
        html.append("h2 { color: #527fff; margin-top: 30px; }");
        html.append(".success { color: #1a8754; font-size: 24px; font-weight: bold; }");
        html.append("table { border-collapse: collapse; margin-top: 10px; }");
        html.append("th, td { border: 1px solid #ccc; padding: 8px 16px; text-align: left; }");
        html.append("th { background: #232f3e; color: white; }");
        html.append(".info-box { background: white; padding: 20px; border-radius: 8px; margin-top: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        html.append("</style></head><body>");
        html.append("<h1>AWS Sample Application</h1>");

        // --- EC2 インスタンス情報 (IMDSv2) ---
        String instanceId = "取得失敗";
        String availabilityZone = "取得失敗";
        try {
            // IMDSv2: まずトークンを取得
            String token = getImdsToken();
            instanceId = getImdsMetadata(token, "instance-id");
            availabilityZone = getImdsMetadata(token, "placement/availability-zone");
        } catch (Exception e) {
            // メタデータ取得失敗時はデフォルト値のまま
        }

        html.append("<div class='info-box'>");
        html.append("<h2>EC2 インスタンス情報</h2>");
        html.append("<table>");
        html.append("<tr><th>項目</th><th>値</th></tr>");
        html.append("<tr><td>インスタンスID</td><td>").append(instanceId).append("</td></tr>");
        html.append("<tr><td>アベイラビリティゾーン</td><td>").append(availabilityZone).append("</td></tr>");
        html.append("</table>");
        html.append("</div>");

        // --- DB接続確認: SHOW DATABASES ---
        html.append("<div class='info-box'>");
        html.append("<h2>RDS MySQL 接続確認</h2>");
        try {
            List<Map<String, Object>> databases = jdbcTemplate.queryForList("SHOW DATABASES");
            html.append("<p class='success'>DB接続成功！</p>");
            html.append("<table>");
            html.append("<tr><th>Database</th></tr>");
            for (Map<String, Object> row : databases) {
                html.append("<tr><td>").append(row.values().iterator().next()).append("</td></tr>");
            }
            html.append("</table>");
        } catch (Exception e) {
            html.append("<p style='color:red;font-weight:bold;'>DB接続失敗: ").append(e.getMessage()).append("</p>");
        }
        html.append("</div>");

        html.append("</body></html>");
        return html.toString();
    }

    /**
     * IMDSv2 トークン取得
     */
    private String getImdsToken() throws Exception {
        URL url = new URL("http://169.254.169.254/latest/api/token");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("X-aws-ec2-metadata-token-ttl-seconds", "21600");
        conn.setConnectTimeout(2000);
        conn.setReadTimeout(2000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String token = reader.readLine();
        reader.close();
        return token;
    }

    /**
     * IMDSv2 メタデータ取得
     */
    private String getImdsMetadata(String token, String path) throws Exception {
        URL url = new URL("http://169.254.169.254/latest/meta-data/" + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-aws-ec2-metadata-token", token);
        conn.setConnectTimeout(2000);
        conn.setReadTimeout(2000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String value = reader.readLine();
        reader.close();
        return value;
    }
}
