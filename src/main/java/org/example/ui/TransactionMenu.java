package org.example.ui;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import org.example.domain.enums.CategoryType;
import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.AbstractTransaction;
import org.example.domain.models.Category;
import org.example.domain.models.CreditCard;
import org.example.services.AccountService;
import org.example.services.CategoryService;
import org.example.services.CreditCardService;
import org.example.services.TransactionService;

public class TransactionMenu {

  private final Scanner scanner;
  private final TransactionService transactionService;
  private final AccountService accountService;
  private final CreditCardService creditCardService;
  private final CategoryService categoryService;

  private final SimpleDateFormat dateFormat = new SimpleDateFormat(
    "dd/MM/yyyy"
  );

  public TransactionMenu(
    Scanner scanner,
    TransactionService transactionService,
    AccountService accountService,
    CreditCardService creditCardService,
    CategoryService categoryService
  ) {
    this.scanner = scanner;
    this.transactionService = transactionService;
    this.accountService = accountService;
    this.creditCardService = creditCardService;
    this.categoryService = categoryService;
  }

  public void start() {
    boolean back = false;

    while (!back) {
      UIUtils.cleanIU();
      UIUtils.heading("Gestão de Transações");
      System.out.println("1. Listar Transações (Com Filtros)");
      System.out.println("2. Registrar Nova Transação (Conta/Carteira)");
      System.out.println("3. Registrar Despesa no Cartão de Crédito");
      System.out.println("4. Editar Transação");
      System.out.println("5. Alterar Status (Efetivar/Pendente)");
      System.out.println("6. Remover Transação");
      System.out.println("0. Voltar ao Menu Principal");
      System.out.print("Escolha uma opção: ");

      String option = scanner.nextLine();

      switch (option) {
        case "1":
          listTransaction();
          break;
        case "2":
          createTransaction();
          break;
        case "3":
          createCreditCardTransaction();
          break;
        case "4":
          editTransaction();
          break;
        case "5":
          changeStatus();
          break;
        case "6":
          excludeTransaction();
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

  private void listTransactionWindows(List<AbstractTransaction> transactions) {
    if (transactions.isEmpty()) {
      System.out.println("Nenhuma transação encontrada.");
    } else {
      System.out.printf(
        "%-5s | %-12s | %-20s | %-12s | %-10s | %-10s | %-15s%n",
        "ID",
        "Data",
        "Descrição",
        "Valor",
        "Tipo",
        "Status",
        "Conta"
      );
      System.out.println(
        "---------------------------------------------------------------------------------------------------"
      );
      for (AbstractTransaction t : transactions) {
        System.out.printf(
          "%-5d | %-12s | %-20s | %-12s | %-10s | %-10s | %-15s%n",
          t.getId(),
          UIUtils.formatDate(t.getDate()),
          t.getDescription(),
          UIUtils.formatCurrency(t.getTransactionValue()),
          t.getType(),
          t.getStatus(),
          t.getAccount().getAccountName()
        );
      }
    }
  }

  private void listTransaction() {
    UIUtils.cleanIU();
    UIUtils.heading("Filtro de Transações");
    System.out.println(
      "Pressione ENTER para ignorar um filtro ou digite o valor desejado."
    );

    System.out.print("Filtrar por Tipo (1-ENTRADA, 2-SAIDA): ");
    String typeInput = scanner.nextLine();
    TransactionType type = typeInput.equals("1")
      ? TransactionType.ENTRADA
      : (typeInput.equals("2") ? TransactionType.SAIDA : null);

    System.out.print("Filtrar por Status (1-PENDENTE, 2-EFETIVADA): ");
    String statusInput = scanner.nextLine();
    TransactionStatus status = statusInput.equals("1")
      ? TransactionStatus.PENDENTE
      : (statusInput.equals("2") ? TransactionStatus.EFETIVADA : null);

    UIUtils.cleanIU();
    UIUtils.heading("Resultado da Busca");
    listTransactionWindows(
      transactionService.searchTransactions(status, null, type, null, null)
    );

    UIUtils.pause(scanner);
  }

  private Date validateDate() {
    System.out.print("Data (dd/MM/yyyy) ou deixe em branco para HOJE: ");
    String dateInput = scanner.nextLine();
    if (dateInput.trim().isEmpty()) return new Date();
    try {
      return dateFormat.parse(dateInput);
    } catch (ParseException e) {
      System.out.println("Data inválida. Usando a data de hoje.");
      return new Date();
    }
  }

  private void createTransaction() {
    UIUtils.cleanIU();
    UIUtils.heading("Nova Transação");

    try {
      System.out.print("Valor (Ex: 150.50): ");
      BigDecimal value = new BigDecimal(scanner.nextLine());

      System.out.print("Descrição: ");
      String description = scanner.nextLine();

      Date date = validateDate();

      System.out.print("Tipo (1-ENTRADA, 2-SAIDA): ");
      TransactionType type = scanner.nextLine().equals("1")
        ? TransactionType.ENTRADA
        : TransactionType.SAIDA;

      CategoryType categoryType = (TransactionType.ENTRADA == type)
        ? CategoryType.RECEITA
        : CategoryType.DESPESA;

      System.out.print("Status (1-PENDENTE, 2-EFETIVADA): ");
      TransactionStatus status = scanner.nextLine().equals("1")
        ? TransactionStatus.PENDENTE
        : TransactionStatus.EFETIVADA;

      System.out.println("\n-- Selecione a Categoria --");
      for (Category c : categoryService.ListForType(categoryType)) {
        System.out.printf(
          "[%d] %s (%s)%n",
          c.getId(),
          c.getName(),
          c.getType()
        );
      }

      System.out.print("ID da Categoria: ");
      int catId = Integer.parseInt(scanner.nextLine());

      System.out.println("\n-- Selecione a Conta --");
      for (AbstractAccount a : accountService.searchAccounts(null)) {
        System.out.printf(
          "[%d] %s (Saldo: %s)%n",
          a.getId(),
          a.getAccountName(),
          UIUtils.formatCurrency(a.getBalance())
        );
      }

      System.out.print("ID da Conta: ");
      int accId = Integer.parseInt(scanner.nextLine());

      transactionService.create(
        value,
        description,
        date,
        status,
        catId,
        type,
        accId
      );

      System.out.println("\nTransação criada com sucesso!");
    } catch (Exception e) {
      System.out.println("\nErro ao criar conta: " + e.getMessage());
    }
    UIUtils.wait(1);
  }

  private void createCreditCardTransaction() {
    UIUtils.cleanIU();
    UIUtils.heading("Nova Despesa no Cartão");

    try {
      System.out.print("Valor (Ex: 150.50): ");
      BigDecimal value = new BigDecimal(scanner.nextLine());

      System.out.print("Descrição: ");
      String description = scanner.nextLine();

      Date date = validateDate();

      System.out.println("\n-- Selecione a Categoria --");
      for (Category c : categoryService.ListForType(CategoryType.DESPESA)) {
        System.out.printf("[%d] %s%n", c.getId(), c.getName());
      }

      System.out.print("ID da Categoria: ");
      int catId = Integer.parseInt(scanner.nextLine());

      System.out.println("\n-- Selecione o Cartão --");
      for (CreditCard c : creditCardService.listAll()) {
        System.out.printf(
          "[%d] %s (Banco: %s)%n",
          c.getId(),
          c.getName(),
          c.getBank().getAccountName()
        );
      }

      System.out.print("ID do Cartão: ");
      int cardId = Integer.parseInt(scanner.nextLine());

      CreditCard cardSelecionado = creditCardService
        .listAll()
        .stream()
        .filter(c -> c.getId().equals(cardId))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

      transactionService.createCreditCardTransaction(
        value,
        description,
        date,
        catId,
        cardSelecionado.getBank().getId(),
        cardId
      );
      System.out.println("\nDespesa no cartão registrada!");
    } catch (Exception e) {
      System.out.println("\nErro ao criar despesa: " + e.getMessage());
    }
    UIUtils.wait(1);
  }

  private void editTransaction() {
    UIUtils.cleanIU();
    UIUtils.heading("Editar Transação");
    System.out.print("Digite o ID da transação: ");
    try {
      int id = Integer.parseInt(scanner.nextLine());

      System.out.println("O que deseja alterar?");
      System.out.println("1. Valor");
      System.out.println("2. Descrição");
      System.out.println("3. Data");
      System.out.print("Opção: ");
      String option = scanner.nextLine();

      switch (option) {
        case "1" -> {
          System.out.print("Novo valor: ");
          transactionService.changeValue(
            id,
            new BigDecimal(scanner.nextLine())
          );
        }
        case "2" -> {
          System.out.print("Nova descrição: ");
          transactionService.changeDescription(id, scanner.nextLine());
        }
        case "3" -> transactionService.changeDate(id, validateDate());
      }
      System.out.println("\nTransação editada com sucesso!");
    } catch (Exception e) {
      System.out.println("\nErro ao editar Transação: " + e.getMessage());
    }
    UIUtils.wait(1);
  }

  private void changeStatus() {
    UIUtils.cleanIU();
    UIUtils.heading("Efetivar/Pendente Transação");
    System.out.print("Digite o ID da transação: ");
    try {
      int id = Integer.parseInt(scanner.nextLine());
      System.out.print("Novo status (1-PENDENTE, 2-EFETIVADA): ");
      TransactionStatus status = scanner.nextLine().equals("1")
        ? TransactionStatus.PENDENTE
        : TransactionStatus.EFETIVADA;

      transactionService.changeStatus(id, status);
      System.out.println(
        "Status atualizado! Saldos recalculados (se aplicável)."
      );
    } catch (Exception e) {
      System.out.println("\nErro ao tentar alterar status: " + e.getMessage());
    }
    UIUtils.wait(1);
  }

  private void excludeTransaction() {
    UIUtils.cleanIU();
    UIUtils.heading("Remover Transação");
    System.out.print("Digite o ID da transação a remover: ");
    try {
      int id = Integer.parseInt(scanner.nextLine());
      transactionService.remove(id);
      System.out.println("Transação removida e saldos estornados!");
    } catch (Exception e) {
      System.out.println(
        "\nErro ao tentar remover transação: " + e.getMessage()
      );
    }
    UIUtils.wait(1);
  }
}
