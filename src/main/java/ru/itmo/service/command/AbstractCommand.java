package ru.itmo.service.command;

import ru.itmo.context.Context;
import ru.itmo.model.Route;
import ru.itmo.service.holder.RouteHolder;
import ru.itmo.service.parser.RouteParser;

import java.util.Scanner;

public abstract class AbstractCommand implements Command {

    protected Route createRoute(Scanner scanner) {
        Context context = Context.getContext();
        RouteParser parser = context.getRouteParser();
        RouteHolder holder = context.getRouteHolder();
        return parser.newRoute(scanner, holder.getNextId());
    }

    protected RouteHolder getRouteHolder() {
        return Context.getContext().getRouteHolder();
    }
}
