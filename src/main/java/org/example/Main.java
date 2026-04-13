package org.example;

import org.example.config.database.DatabaseSetup;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.AccountBank;
import org.example.repositories.AccountRepositoryH2Impl;
import org.h2.tools.Server;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        DatabaseSetup.initialize();

        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
 
        BigDecimal deposito = new BigDecimal("80.00");

        AccountBank nuBank = new AccountBank("nubank");
        //System.out.println(nuBank);
        nuBank.deposit(deposito);
        //System.out.println(nuBank);
        nuBank.setId(1);
 
        AccountRepositoryH2Impl teste = new AccountRepositoryH2Impl();
        teste.save(nuBank);
        //System.out.println(teste.findById(1));
        List<AbstractAccount> teste2; 
        teste2 = new ArrayList<>(teste.findAll());
        for (AbstractAccount obj : teste2) {
            System.out.println(obj);
        }
        System.out.println("----------");
        nuBank.deposit(deposito);
        teste.update(nuBank);
        teste2 = new ArrayList<>(teste.findAll());
        for (AbstractAccount obj : teste2) {
            System.out.println(obj);
        }
        System.out.println("----------");
        teste.delete(1);
        teste2 = new ArrayList<>(teste.findAll());
        for (AbstractAccount obj : teste2) {
            System.out.println(obj);
        }




    }

}