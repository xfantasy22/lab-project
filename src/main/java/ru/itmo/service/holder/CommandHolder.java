package ru.itmo.service.holder;

import ru.itmo.model.State;

import java.util.function.Function;

public interface CommandHolder {
    Function<String, State> command(String command);
}
