package org.example.config.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactory {
    private static final String URL = "jdbc:h2:./banco/cashflow";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL,USER,PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao conectar no banco de dados H2!",e);
        }
    }

}
