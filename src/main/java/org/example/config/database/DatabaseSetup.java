package org.example.config.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public  static void initialize() {

    String sql = """
            """;
    try (Connection conn = ConnectionFactory.getConnection();
    Statement stmt = conn.createStatement()) {
        stmt.execute(sql);
        System.out.println("Estabelecida conexão com banco de dados");
    } catch (SQLException e) {
        throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
    }
    }
}

