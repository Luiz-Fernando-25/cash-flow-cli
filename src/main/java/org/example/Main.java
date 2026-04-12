package org.example;

import org.example.config.database.DatabaseSetup;
import org.h2.tools.Server;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        DatabaseSetup.initialize();

        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
    }

}