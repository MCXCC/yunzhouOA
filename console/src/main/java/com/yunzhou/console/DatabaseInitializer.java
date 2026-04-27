package com.yunzhou.console;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {

    private static final String URL = "jdbc:postgresql://pgm-bp13ydmhvh1a3bz78o.pg.rds.aliyuncs.com:5432/yunzhou_dev";
    private static final String USERNAME = "Chenlz";
    private static final String PASSWORD = "Clz11053";

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  云州OA - 数据库初始化工具");
        System.out.println("========================================");
        System.out.println();

        Path sqlFile = Paths.get("src/main/resources/db/init.sql");

        if (!Files.exists(sqlFile)) {
            System.err.println("错误: 找不到SQL文件 " + sqlFile);
            return;
        }

        try {
            String sql = Files.readString(sqlFile);
            System.out.println("正在连接数据库...");
            System.out.println("地址: " + URL);
            System.out.println();

            Class.forName("org.postgresql.Driver");

            try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                 Statement stmt = conn.createStatement()) {

                System.out.println("数据库连接成功!");
                System.out.println();

                // 按分号分割SQL语句
                String[] statements = sql.split(";");

                int successCount = 0;
                int skipCount = 0;

                for (String sqlStmt : statements) {
                    sqlStmt = sqlStmt.trim();
                    if (sqlStmt.isEmpty() || sqlStmt.startsWith("--")) {
                        continue;
                    }

                    try {
                        stmt.execute(sqlStmt);
                        successCount++;
                        String preview = sqlStmt.length() > 60 ? sqlStmt.substring(0, 60) + "..." : sqlStmt;
                        System.out.println("[OK] " + preview);
                    } catch (Exception e) {
                        if (e.getMessage().contains("already exists")) {
                            skipCount++;
                            String preview = sqlStmt.length() > 60 ? sqlStmt.substring(0, 60) + "..." : sqlStmt;
                            System.out.println("[SKIP] " + preview + " (已存在)");
                        } else {
                            System.err.println("[ERROR] " + e.getMessage());
                        }
                    }
                }

                System.out.println();
                System.out.println("========================================");
                System.out.println("  执行完成!");
                System.out.println("  成功: " + successCount + " 跳过: " + skipCount);
                System.out.println("========================================");

            }

        } catch (IOException e) {
            System.err.println("读取SQL文件失败: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
