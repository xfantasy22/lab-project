package ru.itmo.service.holder;

import java.util.Scanner;

public interface CommandInvoker {
    boolean invoke(Scanner scanner);

    boolean init(String fileName, Scanner scanner);
}