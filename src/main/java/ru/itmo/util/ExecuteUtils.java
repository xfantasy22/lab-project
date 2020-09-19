package ru.itmo.util;

import ru.itmo.model.State;

import java.util.function.Supplier;

public class ExecuteUtils {

    public static State handleCommand(Supplier<State> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            System.out.printf("%s \n" + "Command FAILED %n", e.getMessage());
            return State.FAILED;
        }
    }
}
