package ru.itmo.service;

import lombok.SneakyThrows;
import ru.itmo.context.ClientContext;
import ru.itmo.util.LogUtils;
import ru.itmo.validator.FileValidator;

import java.io.File;
import java.util.Scanner;

public class ScriptExecutorImpl implements ScriptExecutor {
    @Override
    public void execute(String fileName) {
        try {
            File scriptFile = new File(fileName);
            FileValidator.checkReadProperty(scriptFile);
            runScript(scriptFile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @SneakyThrows
    public void runScript(File file) {
        System.out.println("Execute the script? Y/N");
        Scanner scanner = new Scanner(System.in);
        if ("y".equalsIgnoreCase(scanner.nextLine())) {
            System.out.printf("Prepare...\nExecute file: %s%n", file.getName());
            Scanner fileScanner = new Scanner(file);
            boolean running = true;
            LogUtils.setFullLogs(true);
            while (fileScanner.hasNext() && running) {
                running = ClientContext.getInstance().getCommandInvoker().invoke(fileScanner);
            }
            LogUtils.setFullLogs(false);
            System.out.printf("Script file %s executed%n", file.getName());
            fileScanner.close();
            return;
        }
        System.out.println("Command aborted");
    }
}
