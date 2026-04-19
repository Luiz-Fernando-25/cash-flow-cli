package org.example.ui;

import java.util.Scanner;
import org.example.services.AccountService;
import org.example.services.CategoryService;
import org.example.services.CreditCardService;
import org.example.services.TransactionService;
import org.example.services.TransferService;

public class MainMenu {

  private final Scanner scanner;
  private final AccountService accountService;
  private final CategoryService categoryService;
  private final CreditCardService creditCardService;
  private final TransactionService transactionService;
  private final TransferService transferService;
  private AccountMenu accountMenu;
  private CategoryMenu categoryMenu;
  private CreditCardMenu creditCardMenu;
  private TransactionMenu transactionMenu;
  private TransferMenu transferMenu;

  public MainMenu(
    Scanner scanner,
    AccountService accountService,
    CategoryService categoryService,
    CreditCardService creditCardService,
    TransactionService transactionService,
    TransferService transferService
  ) {
    this.scanner = scanner;
    this.accountService = accountService;
    this.categoryService = categoryService;
    this.creditCardService = creditCardService;
    this.transactionService = transactionService;
    this.transferService = transferService;
  }

  public void start() {
    boolean running = true;
    accountMenu = new AccountMenu(scanner, accountService);
    categoryMenu = new CategoryMenu(scanner, categoryService);
    creditCardMenu = new CreditCardMenu(
      scanner,
      creditCardService,
      accountService
    );
    transactionMenu = new TransactionMenu(
      scanner,
      transactionService,
      accountService,
      creditCardService,
      categoryService
    );
    transferMenu = new TransferMenu(scanner, transferService, accountService);

    while (running) {
      UIUtils.cleanIU();
      System.out.println("\n=================================");
      System.out.println("# CASH FLOW CLI - Mobills Clone #");
      System.out.println("=================================");
      System.out.println("1. Gerenciar Contas e Carteiras");
      System.out.println("2. Gerenciar Categorias");
      System.out.println("3. Gerenciar Cartões de Crédito");
      System.out.println("4. Gerenciar Transações (Receitas/Despesas)");
      System.out.println("5. Gerenciar Transferências");
      System.out.println("0. Sair do Sistema");
      System.out.print("Escolha uma opção: ");

      String option = scanner.nextLine();

      switch (option) {
        case "1":
          UIUtils.cleanIU();
          UIUtils.heading("Gerenciar Contas");
          UIUtils.wait(1);
          accountMenu.start();
          break;
        case "2":
          UIUtils.cleanIU();
          UIUtils.heading("Gerenciar categoria");
          UIUtils.wait(1);
          categoryMenu.start();
          break;
        case "3":
          UIUtils.cleanIU();
          UIUtils.heading("Gerenciar Cartões de Crédito");
          UIUtils.wait(1);
          creditCardMenu.start();
          break;
        case "4":
          UIUtils.cleanIU();
          UIUtils.heading("Gerenciar Transações");
          UIUtils.wait(1);
          transactionMenu.start();
          break;
        case "5":
          UIUtils.cleanIU();
          UIUtils.heading("Gerenciar Transferências");
          UIUtils.wait(1);
          transferMenu.start();
          break;
        case "0":
          running = false;
          break;
        default:
          System.out.println("\nOpção inválida! Tente novamente.");
          UIUtils.pause(scanner);
      }
    }
  }
}
