package org.example;

import java.util.Scanner;
import org.example.config.database.DatabaseSetup;
import org.example.repositories.AccountRepository;
import org.example.repositories.AccountRepositoryH2Impl;
import org.example.repositories.CategoryRepository;
import org.example.repositories.CategoryRepositoryH2Impl;
import org.example.repositories.CreditCardRepository;
import org.example.repositories.CreditCardRepositoryH2Impl;
import org.example.repositories.TransactionRepository;
import org.example.repositories.TransactionRepositoryH2Impl;
import org.example.repositories.TransferRepository;
import org.example.repositories.TransferRepositoryH2Impl;
import org.example.services.AccountService;
import org.example.services.CategoryService;
import org.example.services.CreditCardService;
import org.example.services.TransactionService;
import org.example.services.TransferService;
import org.example.services.impl.AccountServiceImpl;
import org.example.services.impl.CategoryServiceImpl;
import org.example.services.impl.CreditCardServiceImpl;
import org.example.services.impl.TransactionServiceImpl;
import org.example.services.impl.TransferServiceImpl;
import org.example.ui.MainMenu;

public class Main {

  public static void main(String[] args) throws Exception {
    // 1. Prepara o Banco de Dados
    System.out.println("Iniciando o Cash Flow CLI...");
    DatabaseSetup.initialize();

    // 2. Instancia a camada de Repositórios
    AccountRepository accountRepo = new AccountRepositoryH2Impl();
    CategoryRepository categoryRepo = new CategoryRepositoryH2Impl();
    CreditCardRepository cardRepo = new CreditCardRepositoryH2Impl(accountRepo);
    TransactionRepository transactionRepo = new TransactionRepositoryH2Impl(
      accountRepo,
      cardRepo,
      categoryRepo
    );
    TransferRepository transferRepo = new TransferRepositoryH2Impl(
      transactionRepo
    );

    // 3. Instancia a camada de Serviços
    AccountService accountServ = new AccountServiceImpl(accountRepo);
    CategoryService categoryServ = new CategoryServiceImpl(categoryRepo);
    CreditCardService creditCardServ = new CreditCardServiceImpl(
      cardRepo,
      accountRepo
    );
    TransactionService transactionServ = new TransactionServiceImpl(
      transactionRepo,
      accountRepo,
      cardRepo,
      categoryRepo,
      accountServ
    );
    TransferService transferServ = new TransferServiceImpl(
      transferRepo,
      transactionServ
    );

    // 4. Inicia o Scanner global para o sistema
    Scanner scanner = new Scanner(System.in);

    // 5. Inicia a Interface de Linha de Comando (CLI)
    MainMenu mainMenu = new MainMenu(
      scanner,
      accountServ,
      categoryServ,
      creditCardServ,
      transactionServ,
      transferServ
    );
    mainMenu.start();

    // 6. Encerra o sistema
    System.out.println("Encerrando o Cash Flow CLI. Até logo!");
    scanner.close();
  }
}
