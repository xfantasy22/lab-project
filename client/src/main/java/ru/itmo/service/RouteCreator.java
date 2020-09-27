package ru.itmo.service;

import ru.itmo.model.Route;

import java.util.Scanner;

public interface RouteCreator {

    Route createRoute(Scanner scanner, boolean isUpdate);

}
