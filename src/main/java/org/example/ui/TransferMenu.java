package org.example.ui;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.Transfer;
import org.example.services.AccountService;
import org.example.services.TransferService;

public class TransferMenu {

  private final Scanner scanner;
  private final TransferService transferService;
  private final AccountService accountService;

  private final SimpleDateFormat dateFormat = new SimpleDateFormat(
    "dd/MM/yyyy"
  );

  public TransferMenu(
    Scanner scanner,
    TransferService transferService,
    AccountService accountService
  ) {
    this.scanner = scanner;
    this.transferService = transferService;
    this.accountService = accountService;
  }

  public void start() {
    boolean back = false;

    while (!back) {
      UIUtils.cleanIU();
      UIUtils.heading("Gestão de Transferências");
      System.out.println("1. Listar Histórico de Transferências");
      System.out.println("2. Realizar Nova Transferência");
      System.out.println("3. Desfazer/Remover Transferência");
      System.out.println("0. Voltar ao Menu Principal");
      System.out.print("Escolha uma opção: ");

      String option = scanner.nextLine();

      switch (option) {
        case "1":
          listTransfers();
          break;
        case "2":
          createTransfer();
          break;
        case "3":
          excluedeTransfer();
          break;
        case "0":
          back = true;
          break;
        default:
          System.out.println("Opção inválida!");
      }
    }
  }

  private void listTransfersWindows(List<Transfer> transfers) {
    if (transfers.isEmpty()) {
      System.out.println("Nenhuma transferência encontrada no histórico.");
    } else {
      System.out.printf(
        "%-5s | %-12s | %-15s | %-20s | %-20s%n",
        "ID",
        "Data",
        "Valor",
        "Conta Origem",
        "Conta Destino"
      );
      System.out.println(
        "----------------------------------------------------------------------------------"
      );

      for (Transfer t : transfers) {
        System.out.printf(
          "%-5d | %-12s | %-15s | %-20s | %-20s%n",
          t.getId(),
          UIUtils.formatDate(t.getOutputTransaction().getDate()),
          UIUtils.formatCurrency(
            t.getOutputTransaction().getTransactionValue()
          ),
          t.getOutputTransaction().getAccount().getAccountName(),
          t.getInputTransaction().getAccount().getAccountName()
        );
      }
    }
  }

  private void listTransfers() {
    UIUtils.cleanIU();
    UIUtils.heading("Histórico de Transferências");
    listTransfersWindows(transferService.listAll());
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

  private AbstractAccount searchAccontForId(
    List<AbstractAccount> accounts,
    int id
  ) {
    return accounts
      .stream()
      .filter(a -> a.getId() == id)
      .findFirst()
      .orElseThrow(() ->
        new RuntimeException("Conta com ID " + id + " não encontrada.")
      );
  }

  private void createTransfer() {
    UIUtils.cleanIU();
    UIUtils.heading("Nova Transferência");

    List<AbstractAccount> accounts = accountService.searchAccounts(null);
    if (accounts.size() < 2) {
      UIUtils.heading(
        "Você precisa ter pelo menos 2 contas cadastradas para fazer uma transferência."
      );
      return;
    }

    try {
      System.out.println("-- Selecione as Contas --");
      for (AbstractAccount a : accounts) {
        System.out.printf(
          "[%d] %s (Saldo: %s)%n",
          a.getId(),
          a.getAccountName(),
          UIUtils.formatCurrency(a.getBalance())
        );
      }

      System.out.print("\nID da Conta de ORIGEM (De onde o dinheiro sai): ");
      int idOrigin = Integer.parseInt(scanner.nextLine());
      AbstractAccount accountOrigin = searchAccontForId(accounts, idOrigin);

      System.out.print("ID da Conta de DESTINO (Para onde o dinheiro vai): ");
      int idDestination = Integer.parseInt(scanner.nextLine());
      AbstractAccount accountDestination = searchAccontForId(
        accounts,
        idDestination
      );

      if (idOrigin == idOrigin) {
        System.out.println(
          "A conta de origem e destino não podem ser a mesma!"
        );
        UIUtils.pause(scanner);
        return;
      }

      System.out.print("\nValor da Transferência: ");
      BigDecimal value = new BigDecimal(scanner.nextLine());

      Date date = validateDate();

      transferService.create(value, date, accountOrigin, accountDestination);
      System.out.println("Transferência realizada com sucesso!");
    } catch (Exception e) {
      System.out.println("\nErro ao fazer tranferencia: " + e.getMessage());
    }
    UIUtils.wait(1);
  }

  private void excluedeTransfer() {
    UIUtils.cleanIU();
    UIUtils.heading("Desfazer Transferência");

    listTransfersWindows(transferService.listAll());
    System.out.println();

    try {
      System.out.print("Digite o ID da transferência a ser desfeita: ");
      int id = Integer.parseInt(scanner.nextLine());

      System.out.print(
        "Tem certeza que deseja desfazer esta transferência? O saldo voltará para a conta de origem. (S/N): "
      );
      if (scanner.nextLine().equalsIgnoreCase("S")) {
        transferService.remove(id);
        System.out.println("Transferência desfeita e saldos restaurados!");
      } else {
        System.out.println("\nOperação cancelada.");
      }
    } catch (Exception e) {
      System.out.println("\nErro ao criar transferencia: " + e.getMessage());
    }
    UIUtils.wait(1);
  }
}
