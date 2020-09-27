package ru.itmo;

import ru.itmo.context.ClientContext;
import ru.itmo.service.CommandInvoker;

import java.util.Scanner;

public class ClientApplication {

    public static void main(String[] args) {
        CommandInvoker commandInvoker = ClientContext.getInstance().getCommandInvoker();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            if (!commandInvoker.invoke(scanner)) {
                running = false;
            }
        }
        scanner.close();
    }

}
