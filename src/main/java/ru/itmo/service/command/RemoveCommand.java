package ru.itmo.service.command;

import ru.itmo.exception.ValidateException;
import ru.itmo.model.constant.Commands;
import ru.itmo.service.holder.RouteHolder;

import java.util.Scanner;

import static ru.itmo.util.validator.EntityValidator.checkDistance;
import static ru.itmo.util.validator.EntityValidator.checkNull;

public class RemoveCommand extends AbstractCommand {
    private final Commands constant;
    private final Scanner scanner;
    private final String value;

    public RemoveCommand(Commands constant, Scanner scanner, String value) {
        this.constant = constant;
        this.scanner = scanner;
        this.value = value;
    }

    public RemoveCommand(Commands commands) {
        this.constant = commands;
        this.scanner = null;
        this.value = null;
    }

    @Override
    public void execute() {
        switch (constant) {
            case REMOVE_ITEM_BY_ID -> removeById(getRouteHolder());
            case REMOVE_ALL_ITEMS_BY_DISTANCE -> removeByDistance(getRouteHolder());
            case REMOVE_LOWER -> removeLower(getRouteHolder());
            case CLEAR_COLLECTION -> getRouteHolder().clear();
        }
    }

    private void removeById(RouteHolder routeHolder) {
        checkNull(value, "id");
        Long id = Long.parseLong(value);
        if (routeHolder.checkExistsId(id)) {
            routeHolder.removeElementById(id);
        }
        System.out.printf("Id does not exists, id: %s%n", id);
    }

    private void removeByDistance(RouteHolder routeHolder) {
        if (value == null || value.isEmpty()) {
            throw new ValidateException("distance cannot be empty");
        }
        long distance = Long.parseLong(value);
        checkDistance(distance);
        routeHolder.removeElementsIfDistanceAreEqual(distance);
    }

    private void removeLower(RouteHolder routeHolder) {
        routeHolder.removeElementsLessThanLower(createRoute(scanner));
    }
}
