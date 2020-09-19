package ru.itmo.service.executor.script;

import lombok.SneakyThrows;
import ru.itmo.context.Context;
import ru.itmo.context.Global;
import ru.itmo.model.State;
import ru.itmo.service.executor.CommandExecutor;

import java.io.File;
import java.util.Scanner;

import static ru.itmo.util.ExecuteUtils.handleCommand;

public class ScriptExecutorImpl implements ScriptExecutor {

    @Override
    @SneakyThrows
    public State executeScript(File file) {
        CommandExecutor executor = Context.getContext().getCommandExecutor();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Execute the script? Y/N");
        if ("y".equalsIgnoreCase(scanner.nextLine())) {
            System.out.printf("Prepare...\nExecute file: %s%n", file.getName());
            scanner = new Scanner(file);
            State state = State.RUN;
            while (scanner.hasNext() && State.STOP != state) {
                String command = scanner.nextLine();
                System.out.println(command);
                Global.getGlobal().setScanner(scanner);
                state = handleCommand(executor.execute(command));
            }
            Global.getGlobal().setScanner(null);
            scanner.close();
            System.out.printf("Script file %s executed%n", file.getName());
            return state;
        }
        System.out.println("Command aborted");
        return State.FAILED;
    }
}
