package ru.itmo.service;

import ru.itmo.model.dto.RouteView;

import java.util.Scanner;

public interface RouteCreator {

    RouteView createRoute(Scanner scanner, boolean isUpdate);

}
