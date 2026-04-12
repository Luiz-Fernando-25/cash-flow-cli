package org.example;

import org.example.config.database.DatabaseSetup;
import org.example.domain.models.Bank;
import org.h2.tools.Server;

import java.math.BigDecimal;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        DatabaseSetup.initialize();

        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();

        BigDecimal deposito = new BigDecimal("50.00");

        Bank nuBank = new Bank("nubank");
        System.out.println(nuBank);
        nuBank.deposit(deposito);
        System.out.println(nuBank);

    }

}