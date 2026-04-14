package org.example.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.config.database.ConnectionFactory;
import org.example.domain.enums.CategoryType;
import org.example.domain.models.Category;

public class CategoryRepositoryH2Impl implements CategoryRepository {

    @Override
    public void save(Category category) {
        String sql = "INSERT INTO categoria (nome, tipo) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getType().name());

            stmt.executeLargeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()){
                if (generatedKeys.next()){
                    category.setId(generatedKeys.getInt(1));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();    
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);        }
    }

    @Override
    public Optional<Category> findById(Integer id) {
        String sql = "SELECT * FROM categoria WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    Category category = new Category(id, rs.getString("nome"), CategoryType.valueOf(rs.getString("tipo")));

                    return Optional.of(category);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }
        return Optional.empty();
    }


    @Override
    public List<Category> findAll() {
        String sql = "SELECT * FROM categoria";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
            List<Category> category = new ArrayList<>();
            while (rs.next()) {
                category.add(new Category(rs.getInt("id"), rs.getString("nome"), CategoryType.valueOf(rs.getString("tipo"))));
            }
            return category;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }

    }

    @Override
    public void update(Category category) {
        String sql = "UPDATE categoria SET nome = ?, tipo = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getType().name());
            stmt.setInt(3, category.getId());

            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro crítico ao criar conexão com banco de dados.", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM categoria WHERE id = ?";

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
