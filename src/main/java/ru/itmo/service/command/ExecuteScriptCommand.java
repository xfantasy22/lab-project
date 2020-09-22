package ru.itmo.service.command;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import ru.itmo.context.Context;
import ru.itmo.util.LogUtils;
import ru.itmo.util.validator.FileValidator;

import java.io.File;
import java.util.Scanner;

@AllArgsConstructor
public class ExecuteScriptCommand extends AbstractCommand {
    private final String fileName;

    @Override
    public void execute() {
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
                running = Context.getContext().getCommandInvoker().invoke(fileScanner);
                System.out.println(running);
            }
            LogUtils.setFullLogs(false);
            System.out.printf("Script file %s executed%n", file.getName());
            return;
        }
        System.out.println("Command aborted");
    }
}
