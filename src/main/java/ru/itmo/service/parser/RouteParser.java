package ru.itmo.service.parser;

import ru.itmo.model.Route;

import java.util.Scanner;

public interface RouteParser {
    Route updateRoute(Scanner scanner);

    Route newRoute(Scanner scanner, Long nextId);
}
