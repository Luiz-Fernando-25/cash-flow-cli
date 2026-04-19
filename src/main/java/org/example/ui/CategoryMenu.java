package org.example.ui;

import java.util.List;
import java.util.Scanner;
import org.example.domain.enums.CategoryType;
import org.example.domain.models.Category;
import org.example.services.CategoryService;

public class CategoryMenu {

  private final Scanner scanner;
  private final CategoryService categoryService;

  public CategoryMenu(Scanner scanner, CategoryService categoryService) {
    this.scanner = scanner;
    this.categoryService = categoryService;
  }

  public void start() {
    boolean back = false;

    while (!back) {
      UIUtils.cleanIU();
      UIUtils.heading("Gestão de Categorias");
      System.out.println("1. Listar Todas as Categorias");
      System.out.println("2. Listar Categorias por Tipo");
      System.out.println("3. Criar Nova Categoria");
      System.out.println("4. Editar Nome da Categoria");
      System.out.println("5. Remover Categoria");
      System.out.println("0. Voltar ao Menu Principal");
      System.out.print("Escolha uma opção: ");

      String option = scanner.nextLine();

      switch (option) {
        case "1":
          listCategories();
          break;
        case "2":
          listCategoriesForType();
          break;
        case "3":
          createCategory();
          break;
        case "4":
          editCategoryName();
          break;
        case "5":
          excludeCategory();
          break;
        case "0":
          back = true;
          break;
        default:
          System.out.println("\nOpção inválida!");
          UIUtils.pause(scanner);
      }
    }
  }

  private void listCategoriesForTypeWindows(List<Category> categories) {
    if (categories.isEmpty()) {
      System.out.println("Nenhuma categoria encontrada.");
    } else {
      System.out.printf("%-5s | %-25s | %-15s%n", "ID", "Nome", "Tipo");
      System.out.println("--------------------------------------------------");
      for (Category category : categories) {
        System.out.printf(
          "%-5s | %-25s | %-15s%n",
          category.getId(),
          category.getName(),
          category.getType()
        );
      }
    }
  }

  private void listCategories() {
    UIUtils.cleanIU();

    UIUtils.heading("Todas as Categorias");

    listCategoriesForTypeWindows(categoryService.listAll());

    UIUtils.pause(scanner);
  }

  private void listCategoriesForType() {
    UIUtils.cleanIU();
    UIUtils.heading("Filtrar Categorias");

    System.out.println("Selecione o Tipo que deseja ver:");
    System.out.println("1. RECEITA");
    System.out.println("2. DESPESA");
    System.out.println("Digite qual outra tecla para cancelar");
    System.out.print("Opção: ");
    String typeOption = scanner.nextLine();

    CategoryType type;
    switch (typeOption) {
      case "1":
        type = CategoryType.RECEITA;
        break;
      case "2":
        type = CategoryType.DESPESA;
        break;
      default:
        System.out.println("Operação cacelada");
        return;
    }

    UIUtils.cleanIU();
    UIUtils.heading("Categorias do tipo: " + type.name());

    listCategoriesForTypeWindows(categoryService.ListForType(type));
    UIUtils.pause(scanner);
  }

  private void createCategory() {
    UIUtils.cleanIU();
    UIUtils.heading("Nova Categoria");

    System.out.print("Digite o nome da categoria: ");
    String name = scanner.nextLine();

    System.out.println("Selecione o Tipo:");
    System.out.println("1. RECEITA");
    System.out.println("2. DESPESA");
    System.out.println("Digite qual outra tecla para cancelar");
    System.out.print("Opção: ");
    String typeOption = scanner.nextLine();

    CategoryType type;
    switch (typeOption) {
      case "1":
        type = CategoryType.RECEITA;
        break;
      case "2":
        type = CategoryType.DESPESA;
        break;
      default:
        System.out.println("Operação cacelada");
        return;
    }

    try {
      categoryService.create(name, type);
      System.out.println("\nCategoria criada com sucesso!");
    } catch (Exception e) {
      System.out.println("\nErro: " + e.getMessage());
    }
  }

  private void editCategoryName() {
    UIUtils.cleanIU();
    UIUtils.heading("Todas as Categorias");
    listCategoriesForTypeWindows(categoryService.listAll());
    UIUtils.heading("Editar Nome da Categoria");

    System.out.println();

    try {
      System.out.print("Digite o ID da categoria que deseja editar: ");
      int id = Integer.parseInt(scanner.nextLine());

      System.out.print("Digite o novo nome para a categoria: ");
      String newName = scanner.nextLine();

      categoryService.changeName(id, newName);
      UIUtils.heading("Nome atualizado com sucesso!");
    } catch (NumberFormatException e) {
      System.out.println("\nID deve ser um número inteiro válido.");
    } catch (Exception e) {
      System.out.println("\nErro: " + e.getMessage());
    }
  }

  private void excludeCategory() {
    UIUtils.cleanIU();
    UIUtils.heading("Todas as Categorias");
    listCategoriesForTypeWindows(categoryService.listAll());
    UIUtils.heading("Remover Categoria");

    System.out.println();

    try {
      System.out.print("Digite o ID da categoria a ser removida: ");
      int id = Integer.parseInt(scanner.nextLine());

      // Uma proteção extra de interface para não deixar o usuário deletar a categoria do sistema sem querer
      if (id == 1) {
        System.out.println(
          "\nErro: A Categoria 'Transferência' (ID 1) é do sistema e não deve ser removida."
        );
        UIUtils.wait(1);
        return;
      }

      System.out.print(
        "Tem certeza que deseja remover esta categoria? (S/N): "
      );
      String confirm = scanner.nextLine();

      if (confirm.equalsIgnoreCase("S")) {
        categoryService.remove(id);
        System.out.println("\nCategoria removida com sucesso!");
      } else {
        System.out.println("\nOperação cancelada.");
      }
      UIUtils.wait(1);
    } catch (NumberFormatException e) {
      System.out.println("\\nErro: ID deve ser um número inteiro válido.");
      UIUtils.wait(1);
    } catch (Exception e) {
      System.out.println("\nErro: " + e.getMessage());
      UIUtils.wait(1);
    }
  }
}
