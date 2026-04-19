package org.example.ui;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class UIUtils {

  private static final NumberFormat CURRENC_FORMAT =
    NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
    "dd/MM/yyyy"
  );

  public static void cleanIU() {
    System.out.println("\n\033[H\033[2J");
    System.out.flush();
  }

  public static void heading(String titulo) {
    System.out.println("\n--- " + titulo.toUpperCase() + " ---");
  }

  public static void pause(Scanner scanner) {
    System.out.println("\nPressione ENTER para continuar...");
    scanner.nextLine();
  }

  public static String formatCurrency(BigDecimal value) {
    if (value == null) return CURRENC_FORMAT.format(BigDecimal.ZERO);
    return CURRENC_FORMAT.format(value);
  }

  public static void wait(int seconds) {
    try {
      Thread.sleep(seconds * 1000L);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static String formatDate(Date date) {
    if (date == null) return "N/A";
    return DATE_FORMAT.format(date);
  }
}
