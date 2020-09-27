package ru.itmo.service;

import java.util.Scanner;

public interface CommandInvoker {
    boolean invoke(Scanner scanner);
}