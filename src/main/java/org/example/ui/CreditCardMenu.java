package org.example.ui;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import org.example.domain.enums.AccountType;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.CreditCard;
import org.example.services.AccountService;
import org.example.services.CreditCardService;

public class CreditCardMenu {

  private final Scanner scanner;
  private final CreditCardService creditCardService;
  private final AccountService accountService;

  public CreditCardMenu(
    Scanner scanner,
    CreditCardService creditCardService,
    AccountService accountService
  ) {
    this.scanner = scanner;
    this.creditCardService = creditCardService;
    this.accountService = accountService;
  }

  public void start() {
    boolean back = false;

    while (!back) {
      UIUtils.cleanIU();
      UIUtils.heading("Gestão de Cartões de Crédito");
      System.out.println("1. Listar Todos os Cartões");
      System.out.println("2. Criar Novo Cartão");
      System.out.println("3. Editar Detalhes do Cartão");
      System.out.println("4. Remover Cartão");
      System.out.println("0. Voltar ao Menu Principal");
      System.out.print("Escolha uma opção: ");

      String option = scanner.nextLine();

      switch (option) {
        case "1":
          listCards();
          break;
        case "2":
          createCard();
          break;
        case "3":
          editCard();
          break;
        case "4":
          excludeCard();
          break;
        case "0":
          back = true;
          break;
        default:
          System.out.println("\nOpção inválida! Tente novamente.");
          UIUtils.pause(scanner);
      }
    }
  }

  private void listCardsWindows(List<CreditCard> creditCards) {
    UIUtils.heading("Suas Cartões de Crédito");

    if (creditCards.isEmpty()) {
      System.out.println("Nenhum Cartão de crédito encontrado.");
    } else {
      System.out.printf(
        "%-5s | %-20s | %-12s | %-12s | %-8s | %-8s | %-15s%n",
        "ID",
        "Nome",
        "Valor em uso.",
        "Limite",
        "Fecho",
        "Venc.",
        "Banco"
      );
      System.out.println(
        "---------------------------------------------------------------------------------------------"
      );
      for (CreditCard creditCard : creditCards) {
        System.out.printf(
          "%-5d | %-20s | %-12s | %-12s | %-8d | %-8d | %-15s%n",
          creditCard.getId(),
          creditCard.getName(),
          UIUtils.formatCurrency(creditCard.getBalance()),
          UIUtils.formatCurrency(creditCard.getLimit()),
          creditCard.getClosingDay(),
          creditCard.getDueDate(),
          creditCard.getBank().getAccountName()
        );
      }
    }
  }

  private void listCards() {
    UIUtils.cleanIU();

    listCardsWindows(creditCardService.listAll());

    UIUtils.pause(scanner);
  }

  private void listAccountsWindows(List<AbstractAccount> accounts) {
    System.out.println("Selecione o ID do Banco vinculado:");
    for (AbstractAccount account : accounts) {
      System.out.printf(
        "[%d] %s (%s)%n",
        account.getId(),
        account.getAccountName(),
        account.getType()
      );
    }
  }

  private void createCard() {
    UIUtils.cleanIU();
    UIUtils.heading("Novo Cartão de Crédito");

    listAccountsWindows(accountService.searchAccounts(AccountType.BANCO));

    try {
      System.out.println("ID do Banco: ");
      int bankId = Integer.parseInt(scanner.nextLine());

      System.out.println("Nome do Cartão: ");
      String name = scanner.nextLine();

      System.out.print("Limite Total (Ex: 5000.00): ");
      BigDecimal limit = new BigDecimal(scanner.nextLine());

      System.out.print("Dia de Fechamento (1-28): ");
      int closing = Integer.parseInt(scanner.nextLine());

      System.out.print("Dia de Vencimento (1-28): ");
      int pay = Integer.parseInt(scanner.nextLine());

      creditCardService.create(
        name,
        limit,
        BigDecimal.ZERO,
        closing,
        pay,
        bankId
      );
      System.out.println("Cartão criado com sucesso!");
    } catch (Exception e) {
      System.out.println(
        "\nErro ao criar cartão de crédito: " + e.getMessage()
      );
    }
    UIUtils.wait(1);
  }

  private void editCard() {
    UIUtils.cleanIU();
    listCardsWindows(creditCardService.listAll());
    UIUtils.heading("Editar Cartão");

    try {
      System.out.print("\nDigite o ID do cartão para editar: ");
      int id = Integer.parseInt(scanner.nextLine());

      System.out.println("O que deseja alterar?");
      System.out.println("1. Nome");
      System.out.println("2. Limite");
      System.out.println("3. Dias de Fechamento/Vencimento");
      System.out.println("4. Banco Vinculado");
      System.out.print("Opção: ");
      String subOption = scanner.nextLine();

      switch (subOption) {
        case "1" -> {
          System.out.print("Novo Nome: ");
          creditCardService.changeName(id, scanner.nextLine());
        }
        case "2" -> {
          System.out.print("Novo Limite: ");
          creditCardService.changeLimit(id, new BigDecimal(scanner.nextLine()));
        }
        case "3" -> {
          System.out.print("Novo Dia Fechamento: ");
          int c = Integer.parseInt(scanner.nextLine());
          System.out.print("Novo Dia Vencimento: ");
          int p = Integer.parseInt(scanner.nextLine());
          creditCardService.changeClosingDay(id, c);
          creditCardService.changeDueDate(id, p);
        }
        case "4" -> {
          listAccountsWindows(accountService.searchAccounts(AccountType.BANCO));
          System.out.print("Novo ID do Banco: ");
          creditCardService.changeBank(
            id,
            Integer.parseInt(scanner.nextLine())
          );
        }
      }
      System.out.println("Cartão atualizado!");
    } catch (Exception e) {
      System.out.println("\nErro ao editar cartão: " + e.getMessage());
    }
    UIUtils.wait(1);
  }

  private void excludeCard() {
    UIUtils.cleanIU();
    listCardsWindows(creditCardService.listAll());
    UIUtils.heading("Remover Cartão");

    try {
      System.out.print("\nID do cartão a remover: ");
      int id = Integer.parseInt(scanner.nextLine());

      System.out.print("Confirmar remoção? (S/N): ");
      if (scanner.nextLine().equalsIgnoreCase("S")) {
        creditCardService.remove(id);
        System.out.println("Cartão removido!");
      }
    } catch (Exception e) {
      System.out.println("\nErro ao excluir cartão: " + e.getMessage());
    }
    UIUtils.wait(1);
  }
}
