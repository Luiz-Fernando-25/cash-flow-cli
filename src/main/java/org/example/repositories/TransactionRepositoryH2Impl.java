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
import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.AbstractTransaction;
import org.example.domain.models.CreditCard;
import org.example.domain.models.TransactionCreditCard;
import org.example.domain.models.TransactionInput;
import org.example.domain.models.TransactionOutput;
import org.example.domain.models.Category;

public class TransactionRepositoryH2Impl implements TransactionRepository {

    private final AccountRepository repoAccount;
    private final CreditCardRepository repoCreditCard;
    private final CategoryRepository repoCategory;

    


    public TransactionRepositoryH2Impl(AccountRepository repeAccount, CreditCardRepository repoCreditCard,
            CategoryRepository repoCategory) {
        this.repoAccount = repeAccount;
        this.repoCreditCard = repoCreditCard;
        this.repoCategory = repoCategory;
    }

    @Override
    public void save(AbstractTransaction transaction) {
        String sql = "INSERT INTO transacao (valor, descricao, data, status, categoria_id, tipo_transacao, conta_id, cartao_id, data_vencimento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            stmt.setBigDecimal(1, transaction.getTransactionValue());
            stmt.setString(2, transaction.getDescription());
            stmt.setDate(3, new java.sql.Date(transaction.getDate().getTime()));
            stmt.setString(4, transaction.getStatus().name());
            stmt.setInt(5, transaction.getCategory().getId());
            stmt.setString(6, transaction.getType().name());
            stmt.setInt(7, transaction.getAccount().getId());
            if (transaction instanceof TransactionCreditCard) {
                TransactionCreditCard tcc = (TransactionCreditCard) transaction;
                stmt.setInt(8, tcc.getCreditCard().getId());
                stmt.setDate(9, new java.sql.Date(tcc.getDueDate().getTime()));
            } else {
                stmt.setNull(8, java.sql.Types.INTEGER);
                stmt.setNull(9, java.sql.Types.DATE);
            }

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()){
                if (generatedKeys.next()){
                    transaction.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }
    }

    @Override
    public Optional<AbstractTransaction> findById(Integer id) {
        String sql = "SELECT * FROM transacao WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    AbstractAccount account = this.repoAccount.findById(rs.getInt("conta_id")).orElseThrow(
                        () -> new RuntimeException("Conta não encontrada!")
                    );
                    Category category = this.repoCategory.findById(rs.getInt("categoria_id")).orElseThrow(
                        () -> new RuntimeException("Categoria não encontrada!")
                    );

                    AbstractTransaction transaction;
                    int creditCardId = rs.getInt("cartao_id");

                    if (!(rs.wasNull())){
                        CreditCard creditCard = this.repoCreditCard.findById(creditCardId).orElseThrow(
                            () -> new RuntimeException("Cartão de credito não encontrado!")
                        );
                        transaction = new TransactionCreditCard(id, rs.getBigDecimal("valor"), rs.getString("descricao"), rs.getDate("data"), TransactionStatus.valueOf(rs.getString("status")), category, TransactionType.valueOf(rs.getString("tipo_transacao")), account, creditCard, rs.getDate("data_vencimento"));
                    } else {
                        TransactionType type = TransactionType.valueOf(rs.getString("tipo_transacao"));

                        if (type == TransactionType.ENTRADA) {
                            transaction = new TransactionInput(id, rs.getBigDecimal("valor"), rs.getString("descricao"), rs.getDate("data"), TransactionStatus.valueOf(rs.getString("status")), category, type, account);
                        } else {
                            transaction = new TransactionOutput(id, rs.getBigDecimal("valor"), rs.getString("descricao"), rs.getDate("data"), TransactionStatus.valueOf(rs.getString("status")), category, type, account);
                        }
                    }

                    return Optional.of(transaction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }
        return Optional.empty();
    }
    
    @Override
    public List<AbstractTransaction> findAll() {
        String sql = "SELECT * FROM transacao";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

            List<AbstractTransaction> transaction = new ArrayList<>();
            Map<Integer, CreditCard> cardInMemory = new HashMap<>();
            Map<Integer, AbstractAccount> accountInMemory = new HashMap<>();
            Map<Integer, Category> categoryInMamory = new HashMap<>();

            while (rs.next()) {
                CreditCard creditCard;
                AbstractAccount account;
                Category category;
                int accountId = rs.getInt("conta_id");
                if (accountInMemory.containsKey(accountId)){
                    account = accountInMemory.get(accountId);
                } else {
                    account = this.repoAccount.findById(accountId).orElseThrow(
                        () -> new RuntimeException("Conta não encontrada!")
                    );
                    accountInMemory.put(accountId, account);
                }
                int categoryId = rs.getInt("categoria_id");
                if (categoryInMamory.containsKey(categoryId)){
                    category = categoryInMamory.get(categoryId);
                } else {
                    category = this.repoCategory.findById(categoryId).orElseThrow(
                        () -> new RuntimeException("Categoria não encontrada!")
                    );
                    categoryInMamory.put(categoryId, category);
                }
                int creditCardId = rs.getInt("cartao_id");
                if (!rs.wasNull()) {
                    if (cardInMemory.containsKey(creditCardId)){
                        creditCard = cardInMemory.get(creditCardId);
                    } else {
                        creditCard = this.repoCreditCard.findById(creditCardId).orElseThrow(
                            () -> new RuntimeException("Cartão de credito não encontrado")
                        );
                        cardInMemory.put(creditCardId, creditCard);
                    }
                    transaction.add(new TransactionCreditCard(rs.getInt("id"), rs.getBigDecimal("valor"), rs.getString("descricao"), rs.getDate("data"), TransactionStatus.valueOf(rs.getString("status")), category, TransactionType.valueOf(rs.getString("tipo_transacao")), account, creditCard, rs.getDate("data_vencimento")));
                } else {
                    TransactionType type = TransactionType.valueOf(rs.getString("tipo_transacao"));

                        if (type == TransactionType.ENTRADA) {
                            transaction.add( new TransactionInput(rs.getInt("id"), rs.getBigDecimal("valor"), rs.getString("descricao"), rs.getDate("data"), TransactionStatus.valueOf(rs.getString("status")), category, type, account));
                        } else {
                            transaction.add( new TransactionOutput(rs.getInt("id"), rs.getBigDecimal("valor"), rs.getString("descricao"), rs.getDate("data"), TransactionStatus.valueOf(rs.getString("status")), category, type, account));
                        }

                }



            }

            return transaction;
             
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }
    }

    @Override
    public void update(AbstractTransaction transaction) {
        String sql = "UPDATE transacao SET valor = ?, descricao = ?, data = ?, status = ?, categoria_id = ?, tipo_transacao = ?, conta_id = ?, cartao_id = ?, data_vencimento = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, transaction.getTransactionValue());
            stmt.setString(2, transaction.getDescription());
            stmt.setDate(3, new java.sql.Date(transaction.getDate().getTime()));
            stmt.setString(4, transaction.getStatus().name());
            stmt.setInt(5, transaction.getCategory().getId());
            stmt.setString(6, transaction.getType().name());
            stmt.setInt(7, transaction.getAccount().getId());
            if (transaction instanceof TransactionCreditCard) {
                TransactionCreditCard tcc = (TransactionCreditCard) transaction;
                stmt.setInt(8, tcc.getCreditCard().getId());
                stmt.setDate(9, new java.sql.Date(tcc.getDueDate().getTime()));
            } else {
                stmt.setNull(8, java.sql.Types.INTEGER);
                stmt.setNull(9, java.sql.Types.DATE);
            }
            stmt.setInt(10, transaction.getId());

            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM transacao WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }
    }    
}
