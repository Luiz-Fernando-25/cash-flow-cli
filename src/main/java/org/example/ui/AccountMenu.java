package org.example.ui;

import java.util.List;
import java.util.Scanner;
import org.example.domain.enums.AccountType;
import org.example.domain.models.AbstractAccount;
import org.example.services.AccountService;

public class AccountMenu {

  private final Scanner scanner;
  private final AccountService accountService;

  public AccountMenu(Scanner scanner, AccountService accountService) {
    this.scanner = scanner;
    this.accountService = accountService;
  }

  public void start() {
    boolean back = false;

    while (!back) {
      UIUtils.cleanIU();
      UIUtils.heading("Gestão de Contas e Carteiras");

      System.out.println("1. Listar Todas as Contas");
      System.out.println("2. Criar Nova Conta/Carteira");
      System.out.println("3. Editar Nome da Conta");
      System.out.println("4. Remover Conta");
      System.out.println("0. Voltar ao Menu Principal");
      System.out.print("Escolha uma opção: ");

      String option = scanner.nextLine();

      switch (option) {
        case "1":
          listAcconts();
          break;
        case "2":
          createAccount();
          break;
        case "3":
          editAccount();
          break;
        case "4":
          excludeAccount();
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

  private void listAccontsWindows() {
    UIUtils.heading("Suas Contas");

    List<AbstractAccount> accounts = accountService.searchAccounts(null);

    if (accounts.isEmpty()) {
      System.out.println("Nenhuma conta encontrada.");
    } else {
      System.out.printf(
        "%-5s | %-20s | %-15s | %-15s%n",
        "ID",
        "Nome",
        "Tipo",
        "Saldo"
      );
      System.out.println(
        "------------------------------------------------------------"
      );
      for (AbstractAccount account : accounts) {
        System.out.printf(
          "%-5s | %-20s | %-15s | %-15s%n",
          account.getId(),
          account.getAccountName(),
          account.getType(),
          UIUtils.formatCurrency(account.getBalance())
        );
      }
    }
  }

  private void listAcconts() {
    UIUtils.cleanIU();

    listAccontsWindows();

    UIUtils.pause(scanner);
  }

  private void createAccount() {
    UIUtils.cleanIU();
    listAccontsWindows();
    UIUtils.heading("Nova Conta");

    System.out.println("Digite 0 para cancelar");
    System.out.print("Digite o nome da conta: ");
    String name = scanner.nextLine();

    if (name.trim().equals("0")) return;

    System.out.println("Selecione o Tipo:");
    System.out.println("1. BANCO");
    System.out.println("2. CARTEIRA");
    System.out.println("Digite qual outra tecla para cancelar");
    System.out.print("Opção: ");
    String typeOption = scanner.nextLine();

    if (
      !(typeOption.trim().equals("1")) && !(typeOption.trim().equals("2"))
    ) return;
    AccountType type = typeOption.equals("1")
      ? AccountType.BANCO
      : AccountType.CARTEIRA;

    try {
      accountService.create(name, type);
      System.out.println("\nConta criada com sucesso!");
    } catch (Exception e) {
      System.out.println("\nErro ao criar conta: " + e.getMessage());
    }
    UIUtils.wait(1);
  }

  private void editAccount() {
    UIUtils.cleanIU();
    listAccontsWindows();
    UIUtils.heading("Editar Nome da Conta");

    System.out.print("Digite o ID da conta que deseja editar: ");
    int id = Integer.parseInt(scanner.nextLine());

    System.out.print("Digite o novo nome para a conta: ");
    String newName = scanner.nextLine();

    try {
      accountService.changeName(id, newName);
      System.out.println("\nNome atualizado com sucesso!");
    } catch (Exception e) {
      System.out.println("\nErro: " + e.getMessage());
    }

    UIUtils.wait(1);
  }

  private void excludeAccount() {
    UIUtils.cleanIU();
    listAccontsWindows();
    UIUtils.heading("Remover Conta");

    System.out.print("Digite o ID da conta a ser removida: ");
    int id = Integer.parseInt(scanner.nextLine());

    System.out.print("Tem certeza que deseja remover esta conta? (S/N): ");
    String confirm = scanner.nextLine();

    if (confirm.equalsIgnoreCase("S")) {
      try {
        accountService.remove(id);
        System.out.println("\nConta removida com sucesso!");
      } catch (Exception e) {
        System.out.println("\nErro ao remover: " + e.getMessage());
      }
    } else {
      System.out.println("\nOperação cancelada.");
    }

    UIUtils.wait(1);
  }
}
