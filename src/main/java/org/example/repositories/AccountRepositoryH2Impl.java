package org.example.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.config.database.ConnectionFactory;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.AccountBank;
import org.example.domain.models.AccountWallet;

public class AccountRepositoryH2Impl implements AccountRepository {
    
    
    @Override
    public void save(AbstractAccount account) {        
        String sql = "INSERT INTO conta (nome, saldo_atual, tipo_conta) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, account.getAccountName());
            stmt.setBigDecimal(2, account.getBalance());
            stmt.setString(3, account.getType().name());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()){
                if (generatedKeys.next()){
                    account.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();    
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }
        
    }

    @Override
    public Optional<AbstractAccount> findById(Integer id) {
        String sql = "SELECT * FROM conta WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AbstractAccount account;
                    if ("BANCO".equals(rs.getString("tipo_conta"))) {
                        account = new AccountBank(id, rs.getString("nome"), rs.getBigDecimal("saldo_atual"));
                    } else {
                        account = new AccountWallet(id, rs.getString("nome"), rs.getBigDecimal("saldo_atual"));
                    };
                    
                    return Optional.of(account);
                }
            }            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }
        return Optional.empty();
    }
    
    @Override
    public List<AbstractAccount> findAll() {
        String sql = "SELECT * FROM conta";
        
        try(Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
            List<AbstractAccount> conta = new ArrayList<>();
            while (rs.next()) {
                if ("BANCO".equals(rs.getString("tipo_conta"))) {
                    conta.add(new AccountBank(rs.getInt("id"), rs.getString("nome"), rs.getBigDecimal("saldo_atual")));
                } else {
                    conta.add(new AccountWallet(rs.getInt("id"), rs.getString("nome"), rs.getBigDecimal("saldo_atual")));
                }
            }

            return conta;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }
    }

    @Override
    public void update(AbstractAccount account) {
        String sql = "UPDATE conta SET nome = ?, saldo_atual = ?, tipo_conta = ? WHERE id = ? ";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getAccountName());
            stmt.setBigDecimal(2, account.getBalance());
            stmt.setString(3, account.getType().name());
            stmt.setInt(4, account.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }
        
    }
    
    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM conta WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }

    }
    
}
