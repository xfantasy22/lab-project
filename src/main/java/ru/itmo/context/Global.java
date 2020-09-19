package ru.itmo.context;

import ru.itmo.model.State;

import java.util.Scanner;

public class Global {

    private static final Global GLOBAL = new Global();

    public static Global getGlobal() {
        return GLOBAL;
    }

    private String fileName;
    private Scanner scanner;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }
}
