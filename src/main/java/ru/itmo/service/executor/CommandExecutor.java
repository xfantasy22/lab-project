package ru.itmo.service.executor;

import ru.itmo.model.State;

import java.util.function.Supplier;

public interface CommandExecutor {
    Supplier<State> execute(String command);
}
