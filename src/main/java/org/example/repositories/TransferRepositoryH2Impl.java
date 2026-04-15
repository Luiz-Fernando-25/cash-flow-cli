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
import org.example.domain.models.AbstractTransaction;
import org.example.domain.models.TransactionInput;
import org.example.domain.models.TransactionOutput;
import org.example.domain.models.Transfer;

public class TransferRepositoryH2Impl implements TransferRepository {

  private final TransactionRepository repoTransaction;

  public TransferRepositoryH2Impl(TransactionRepository repoTransaction) {
    this.repoTransaction = repoTransaction;
  }

  @Override
  public void save(Transfer transfer) {
    String sql =
      "INSERT INTO movimentacao (saida_id, entrada_id) VALUES (?, ?)";

    try (
      Connection conn = ConnectionFactory.getConnection();
      PreparedStatement stmt = conn.prepareStatement(
        sql,
        java.sql.Statement.RETURN_GENERATED_KEYS
      )
    ) {
      stmt.setInt(1, transfer.getOutputTransaction().getId());
      stmt.setInt(2, transfer.getInputTransaction().getId());

      stmt.executeUpdate();

      try (ResultSet generatedKey = stmt.getGeneratedKeys()) {
        if (generatedKey.next()) {
          transfer.setId(generatedKey.getInt(1));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(
        "Erro crítico ao criar conexão com banco de dados.",
        e
      );
    }
  }

  @Override
  public Optional<Transfer> findById(Integer id) {
    String sql = "SELECT * FROM movimentacao WHERE id = ?";

    try (
      Connection conn = ConnectionFactory.getConnection();
      PreparedStatement stmt = conn.prepareStatement(sql)
    ) {
      stmt.setInt(1, id);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          TransactionInput transactionInput;
          TransactionOutput transactionOutput;
          AbstractTransaction transaction;

          transaction = this.repoTransaction.findById(
            rs.getInt("saida_id")
          ).orElseThrow(() ->
            new RuntimeException("Transação de saida não encontrada!")
          );
          if (transaction instanceof TransactionOutput) {
            transactionOutput = (TransactionOutput) transaction;
          } else {
            throw new RuntimeException(
              "O ID informado não pertence a uma transação de saida!"
            );
          }

          transaction = this.repoTransaction.findById(
            rs.getInt("entrada_id")
          ).orElseThrow(() ->
            new RuntimeException("Transação de entrada não encontrada!")
          );
          if (transaction instanceof TransactionInput) {
            transactionInput = (TransactionInput) transaction;
          } else {
            throw new RuntimeException(
              "O ID informado não pertence a uma transação de entrada!"
            );
          }

          Transfer transfer = new Transfer(
            id,
            transactionOutput,
            transactionInput
          );

          return Optional.of(transfer);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(
        "Erro crítico ao criar conexão com banco de dados.",
        e
      );
    }
    return Optional.empty();
  }

  @Override
  public List<Transfer> findAll() {
    String sql = "SELECT * FROM movimentacao";
    try (
      Connection conn = ConnectionFactory.getConnection();
      PreparedStatement stmt = conn.prepareStatement(sql);
      ResultSet rs = stmt.executeQuery()
    ) {
      List<Transfer> transfer = new ArrayList<>();
      Map<Integer, AbstractTransaction> transactionInMemory = new HashMap<>();

      while (rs.next()) {
        TransactionInput transactionInput;
        TransactionOutput transactionOutput;

        int transactionInputId = rs.getInt("entrada_id");
        AbstractTransaction transaction;
        if (transactionInMemory.containsKey(transactionInputId)) {
          transaction = transactionInMemory.get(transactionInputId);
          if (transaction instanceof TransactionInput) {
            transactionInput = (TransactionInput) transaction;
          } else {
            throw new RuntimeException(
              "O ID informado não pertence a uma transação de entrada!"
            );
          }
        } else {
          transaction = this.repoTransaction.findById(
            transactionInputId
          ).orElseThrow(() -> new RuntimeException("Transação não encontada"));
          if (transaction instanceof TransactionInput) {
            transactionInput = (TransactionInput) transaction;
            transactionInMemory.put(transactionInputId, transaction);
          } else {
            throw new RuntimeException(
              "O ID informado não pertence a uma transação de entrada!"
            );
          }
        }

        int transactionOutputId = rs.getInt("saida_id");
        if (transactionInMemory.containsKey(transactionOutputId)) {
          transaction = transactionInMemory.get(transactionOutputId);
          if (transaction instanceof TransactionOutput) {
            transactionOutput = (TransactionOutput) transaction;
          } else {
            throw new RuntimeException(
              "O ID informado não pertence a uma transação de saida!"
            );
          }
        } else {
          transaction = this.repoTransaction.findById(
            transactionOutputId
          ).orElseThrow(() -> new RuntimeException("Transação não encontada"));
          if (transaction instanceof TransactionOutput) {
            transactionOutput = (TransactionOutput) transaction;
            transactionInMemory.put(transactionOutputId, transaction);
          } else {
            throw new RuntimeException(
              "O ID informado não pertence a uma transação de saida!"
            );
          }
        }
        transfer.add(
          new Transfer(rs.getInt("id"), transactionOutput, transactionInput)
        );
      }
      return transfer;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(
        "Erro crítico ao criar conexão com banco de dados.",
        e
      );
    }
  }

  @Override
  public void update(Transfer transfer) {
    String sql =
      "UPDATE movimentacao SET saida_id = ?, entrada_id = ? WHERE id = ?";

    try (
      Connection conn = ConnectionFactory.getConnection();
      PreparedStatement stmt = conn.prepareStatement(sql)
    ) {
      stmt.setInt(1, transfer.getOutputTransaction().getId());
      stmt.setInt(2, transfer.getInputTransaction().getId());
      stmt.setInt(3, transfer.getId());

      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(
        "Erro crítico ao criar conexão com banco de dados.",
        e
      );
    }
  }

  @Override
  public void delete(Integer id) {
    String sql = "DELETE FROM movimentacao WHERE id = ?";

    try (
      Connection conn = ConnectionFactory.getConnection();
      PreparedStatement stmt = conn.prepareStatement(sql)
    ) {
      stmt.setInt(1, id);

      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(
        "Erro crítico ao criar conexão com banco de dados.",
        e
      );
    }
  }
}
