package ru.itmo;

import ru.itmo.context.Context;
import ru.itmo.context.Global;
import ru.itmo.model.State;
import ru.itmo.service.executor.CommandExecutor;

import java.util.Scanner;

import static ru.itmo.util.ExecuteUtils.handleCommand;

public class Application {

    public static void main(String[] args) {
        //student
        CommandExecutor executor = Context.getContext().getCommandExecutor();
        Scanner scanner = new Scanner(System.in);
        State state = State.INITIAL;

        while (State.STOP != state) {
            if (State.INITIAL == state) {
                state = initialState(args[0]);
            }
            if (State.SWITCH_STATE == state) {
                System.out.println("Want to continue without a file? Y/N");
                state = switchState(scanner.nextLine());
            }
            if (State.RUN == state || State.FAILED == state) {
                System.out.println("Enter a command: ");
                state = handleCommand(executor.execute(scanner.nextLine()));
            }
            if (State.RUN == state) {
                System.out.println("Command completed successfully");
            }
        }
        scanner.close();
    }

    private static State switchState(String answer) {
        if ("y".equalsIgnoreCase(answer)) {
            return State.RUN;
        } else return State.STOP;
    }

    private static State initialState(String fileName) {
        Global.getGlobal().setFileName(fileName);
        return Context.getContext().getRouteHolder().readFromFile(fileName);
    }
}