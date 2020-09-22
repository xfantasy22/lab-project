package ru.itmo.service.command;

import lombok.AllArgsConstructor;
import ru.itmo.context.Context;
import ru.itmo.model.constant.Commands;

import java.util.Scanner;

import static ru.itmo.util.validator.EntityValidator.checkNull;

@AllArgsConstructor
public class InsertCommand extends AbstractCommand {
    private final Commands constant;
    private final Scanner scanner;
    private final String value;

    @Override
    public void execute() {
        switch (constant) {
            case ADD_NEW_ITEM -> getRouteHolder().addElement(createRoute(scanner));
            case ADD_NEW_ITEM_IF_MAX -> getRouteHolder().addElementIfMax(createRoute(scanner));
            case UPDATE_ITEM_BY_ID -> updateRoute();
            case INSERT_ITEM_AT_INDEX -> insertAtIndex();
        }
    }

    private void updateRoute() {
        checkNull(value, "id");
        Long id = Long.parseLong(value);
        getRouteHolder().updateElement(id, Context.getContext().getRouteParser().updateRoute(scanner));
    }

    private void insertAtIndex() {
        checkNull(value, "id");
        int id = Integer.parseInt(value);
        getRouteHolder().insertElementAtIndex(id, createRoute(scanner));
    }
}
