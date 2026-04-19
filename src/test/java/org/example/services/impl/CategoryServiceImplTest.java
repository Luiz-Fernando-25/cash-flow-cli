package org.example.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.example.config.database.DatabaseSetup;
import org.example.domain.enums.CategoryType;
import org.example.domain.models.Category;
import org.example.repositories.CategoryRepository;
import org.example.repositories.CategoryRepositoryH2Impl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CategoryServiceImplTest {

  private CategoryServiceImpl categoryService;
  private CategoryRepository categoryRepository;

  @BeforeEach
  void setUp() {
    DatabaseSetup.dropDB();
    DatabaseSetup.initialize();
    // Lembre-se: O clearDB sempre insere a categoria "Transferência" (MOVIMENTACAO) com ID 1!
    DatabaseSetup.clearDB();

    categoryRepository = new CategoryRepositoryH2Impl();
    categoryService = new CategoryServiceImpl(categoryRepository);
  }

  // ==========================================
  // TESTES DO MÉTODO: create()
  // ==========================================

  @Test
  void shouldCreateCategorySuccessfully() {
    categoryService.create("Alimentação", CategoryType.DESPESA);

    List<Category> categorias = categoryRepository.findAll();
    // Esperamos 2, porque a "Transferência" (ID 1) já nasce com o banco!
    assertEquals(
      2,
      categorias.size(),
      "Deveriam existir 2 categorias no banco."
    );
    assertEquals("Alimentação", categorias.get(1).getName());
    assertEquals(CategoryType.DESPESA, categorias.get(1).getType());
  }

  @Test
  void shouldThrowExceptionWhenCreatingDuplicateCategoryName() {
    categoryService.create("Lazer", CategoryType.DESPESA);

    // Tenta criar outra com o mesmo nome "Lazer"
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      categoryService.create("Lazer", CategoryType.RECEITA);
    });

    assertEquals(
      "Já existe esse nome na lista de catogorias.",
      exception.getMessage()
    );
  }

  @Test
  void shouldThrowExceptionWhenCreatingCategoryWithEmptyName() {
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      categoryService.create("   ", CategoryType.RECEITA);
    });
    assertEquals("O nome não pode ser vazio!", exception.getMessage());
  }

  // ==========================================
  // TESTES DO MÉTODO: changeName()
  // ==========================================

  @Test
  void shouldChangeCategoryNameSuccessfully() {
    // Vamos alterar a categoria padrão (ID 1 - Transferência)
    categoryService.changeName(1, "Movimentação Interna");

    Category categoriaAtualizada = categoryRepository.findById(1).get();
    assertEquals("Movimentação Interna", categoriaAtualizada.getName());
  }

  @Test
  void shouldThrowExceptionWhenChangingNameToEmpty() {
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      categoryService.changeName(1, "");
    });
    assertEquals("O nome não pode ser vazio!", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenChangingNameOfInvalidId() {
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      categoryService.changeName(999, "Fantasma");
    });
    assertEquals("Id da categoria não encontrada", exception.getMessage());
  }

  // ==========================================
  // TESTES DOS MÉTODOS: listAll() e ListForType()
  // ==========================================

  @Test
  void shouldListAllCategories() {
    categoryService.create("Salário", CategoryType.RECEITA);
    categoryService.create("Mercado", CategoryType.DESPESA);

    List<Category> categorias = categoryService.listAll();
    // 1 (Transferência) + 2 criadas = 3
    assertEquals(3, categorias.size());
  }

  @Test
  void shouldListCategoriesSpecificTypeOnly() {
    // Preparação: Criar uma mistura de tipos
    categoryService.create("Salário", CategoryType.RECEITA);
    categoryService.create("Rendimento", CategoryType.RECEITA);
    categoryService.create("Mercado", CategoryType.DESPESA);

    // Ação: Filtrar apenas receitas
    List<Category> receitas = categoryService.ListForType(CategoryType.RECEITA);

    // Verificação: Devem vir apenas as 2 receitas (Salário e Rendimento)
    assertEquals(2, receitas.size(), "Deveria listar apenas 2 receitas.");
    assertEquals(CategoryType.RECEITA, receitas.get(0).getType());
    assertEquals(CategoryType.RECEITA, receitas.get(1).getType());

    // Ação e Verificação: O tipo MOVIMENTACAO deve trazer a categoria padrão
    List<Category> movimentacoes = categoryService.ListForType(
      CategoryType.MOVIMENTACAO
    );
    assertEquals(
      1,
      movimentacoes.size(),
      "Deveria listar apenas 1 movimentação (Transferência)."
    );
  }

  // ==========================================
  // TESTES DO MÉTODO: remove()
  // ==========================================

  @Test
  void shouldRemoveCategorySuccessfully() {
    // ID 1 já existe (Transferência). Vamos deletá-lo.
    assertEquals(1, categoryService.listAll().size());

    categoryService.remove(1);

    assertEquals(
      0,
      categoryService.listAll().size(),
      "A lista deve ficar vazia após a exclusão."
    );
  }

  @Test
  void shouldThrowExceptionWhenRemovingInvalidId() {
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      categoryService.remove(-5);
    });
    assertEquals(
      "Categoria não encontrada para o ID informado.",
      exception.getMessage()
    );
  }
}
