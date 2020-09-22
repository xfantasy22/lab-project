package ru.itmo.service.command;

import lombok.AllArgsConstructor;
import ru.itmo.context.Context;

@AllArgsConstructor
public class SaveCommand extends AbstractCommand {

    private final String fileName;

    @Override
    public void execute() {
        Context.getContext().getRouteHolder().writeToFile(fileName);
    }
}
