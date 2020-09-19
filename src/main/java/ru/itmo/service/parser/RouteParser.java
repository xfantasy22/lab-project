package ru.itmo.service.parser;

import ru.itmo.model.Route;

import java.util.Scanner;

public interface RouteParser {
    Route updateRoute();

    Route addRoute(Long nextId);
}
