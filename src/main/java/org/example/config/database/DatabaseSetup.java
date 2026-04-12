package org.example.config.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public  static void initialize() {

    String sql = """
            CREATE TABLE IF NOT EXISTS conta (
            id INT AUTO_INCREMENT PRIMARY KEY,
            nome VARCHAR(100) NOT NULL,
            saldo_Atual DECIMAL(10,2) DEFAULT 0.00,
            tipo_conta VARCHAR(20) NOT NULL
            );

            CREATE TABLE IF NOT EXISTS cartaocredito (
            id INT AUTO_INCREMENT PRIMARY KEY,
            nome VARCHAR(100) NOT NULL,
            limite DECIMAL(10,2) DEFAULT 0.00,
            dia_fechamento INT NOT NULL,
            dia_vencimento INT NOT NULL,
            conta_id INT NOT NULL,
            FOREIGN KEY (conta_id) REFERENCES conta(id)
            );

            CREATE TABLE IF NOT EXISTS categoria (
            id INT AUTO_INCREMENT PRIMARY KEY,
            nome VARCHAR(100) NOT NULL,
            tipo VARCHAR(50) NOT NULL
            );

            CREATE TABLE IF NOT EXISTS transacao (
            id INT AUTO_INCREMENT PRIMARY KEY,
            valor DECIMAL(10,2) NOT NULL,
            descricao VARCHAR(100) NOT NULL,
            data DATE NOT NULL,
            status VARCHAR(20) NOT NULL,
            categoria_id INT NOT NULL,
            tipo_transacao VARCHAR(50) NOT NULL,
            conta_id INT NOT NULL,
            cartao_id INT,
            data_compra DATE,
            FOREIGN KEY (conta_id) REFERENCES conta(id),
            FOREIGN KEY (categoria_id) REFERENCES categoria(id),
            FOREIGN KEY (cartao_id) REFERENCES cartaocredito(id)
            );

            CREATE TABLE IF NOT EXISTS movimentacao (
            id INT AUTO_INCREMENT PRIMARY KEY,
            saida_id INT NOT NULL,
            entrada_id INT NOT NULL,
            FOREIGN KEY (entrada_id) REFERENCES transacao(id),
            FOREIGN KEY (saida_id) REFERENCES transacao(id)
            );
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

