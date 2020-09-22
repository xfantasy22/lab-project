package ru.itmo;

import ru.itmo.context.Context;
import ru.itmo.service.holder.CommandInvoker;

import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CommandInvoker commandInvoker = Context.getContext().getCommandInvoker();
        if (commandInvoker.init(args[0], scanner)) {
            boolean running = true;
            while (running) {
                if (!commandInvoker.invoke(scanner)) {
                    running = false;
                }
            }
        }
        scanner.close();
    }
}