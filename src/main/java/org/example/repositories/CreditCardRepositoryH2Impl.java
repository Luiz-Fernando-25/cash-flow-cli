package org.example.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.config.database.ConnectionFactory;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.AccountBank;
import org.example.domain.models.CreditCard;


public class CreditCardRepositoryH2Impl implements CreditCardRepository{

    private final AccountRepository repoAccount;
    
    
    public CreditCardRepositoryH2Impl(AccountRepository repoAccount) {
        this.repoAccount = repoAccount;
    }

    @Override
    public void save(CreditCard creditCard) {
        String sql = "INSERT INTO cartaocredito (nome, limite, dia_fechamento, dia_vencimento, conta_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, creditCard.getName());
            stmt.setBigDecimal(2, creditCard.getLimit());
            stmt.setInt(3, creditCard.getClosingDay());
            stmt.setInt(4, creditCard.getDueDate());
            stmt.setInt(5, creditCard.getBank().getId());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()){
                if (generatedKeys.next()){
                    creditCard.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }
    }

    @Override
    public Optional<CreditCard> findById(Integer id) {
        String sql = "SELECT * FROM cartaocredito WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AccountBank bank;
                    AbstractAccount account = this.repoAccount.findById(rs.getInt("conta_id")).orElseThrow(
                        () -> new RuntimeException("Conta não encontrada!")
                    );
                    if (account instanceof AccountBank) {
                        bank = (AccountBank) account;
                    } else {
                        throw new RuntimeException("O ID informado não pertence a um Banco!");
                    }

                    CreditCard creditCard;
                    creditCard = new CreditCard(id, rs.getString("nome"), rs.getBigDecimal("limite"), rs.getInt("dia_fechamento"), rs.getInt("dia_vencimento"), bank);

                    return Optional.of(creditCard);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<CreditCard> findAll() {
        String sql = "SELECT * FROM cartaocredito";
        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
            List<CreditCard> creditCard = new ArrayList<>();
            Map<Integer, AccountBank> bankInMemory = new HashMap<>();
        
            while (rs.next()) {
                AccountBank bankCard;
                int accountId = rs.getInt("conta_id");
                if (bankInMemory.containsKey(accountId)) {
                    bankCard = bankInMemory.get(accountId);
                } else {
                    AbstractAccount account = this.repoAccount.findById(accountId).orElseThrow(
                        () -> new RuntimeException("Conta não encontrada!")
                    );
                    if (account instanceof AccountBank){
                        bankCard = (AccountBank) account;
                        bankInMemory.put(accountId, bankCard);
                    } else {
                        throw new RuntimeException("O ID informado não pertence a um Banco!");
                    }
                }
                creditCard.add(new CreditCard(rs.getInt("id"), rs.getString("nome"), rs.getBigDecimal("limite"), rs.getInt("dia_fechamento"), rs.getInt("dia_vencimento"), bankCard));
            }

            return creditCard;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }
    }

    @Override
    public void update(CreditCard creditCard) {
        String sql = "UPDATE cartaocredito SET nome = ?, limite = ?, dia_fechamento = ?, dia_vencimento = ?, conta_id = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, creditCard.getName());
            stmt.setBigDecimal(2, creditCard.getLimit());
            stmt.setInt(3, creditCard.getClosingDay());
            stmt.setInt(4, creditCard.getDueDate());
            stmt.setInt(5, creditCard.getBank().getId());
            stmt.setInt(6, creditCard.getId());

            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM cartaocredito WHERE id = ?";
        
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
