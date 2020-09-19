package ru.itmo.service.executor.script;

import ru.itmo.model.State;

import java.io.File;

public interface ScriptExecutor {

    State executeScript(File file);
}
