package ru.itmo.service.parser;

import ru.itmo.context.Global;
import ru.itmo.exception.ValidateException;
import ru.itmo.model.Coordinates;
import ru.itmo.model.Location;
import ru.itmo.model.Route;
import ru.itmo.util.validator.EntityValidator;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Supplier;

import static ru.itmo.util.ParseUtils.parseStringToTwoValues;
import static ru.itmo.util.ParseUtils.parseStringToValues;
import static ru.itmo.util.validator.EntityValidator.*;

public class RouteParserImpl implements RouteParser {
    private static final String LOCATION_FROM = "from";
    private static final String LOCATION_TO = "to";

    private boolean isUpdate = false;
    private boolean isRead = false;

    @Override
    public Route updateRoute() {
        isUpdate = true;
        return getRoute(getScanner(), null);
    }

    @Override
    public Route addRoute(Long nextId) {
        isUpdate = false;
        return getRoute(getScanner(), nextId);
    }

    private Route getRoute(Scanner scanner, Long nextId) {
        Route.RouteBuilder routeBuilder = Route.builder();

        System.out.println("Enter a name:");
        validateUpdate((s) -> name(routeBuilder, s), scanner.nextLine());

        System.out.println("Enter a coordinates, X and Y:");
        validateUpdate(s -> coordinates(routeBuilder, s), scanner.nextLine());

        System.out.printf("Enter a location (%s). X, Y and Z:%n", LOCATION_FROM);
        validateUpdate(s -> location(routeBuilder, s, LOCATION_FROM), scanner.nextLine());

        System.out.printf("Enter a location (%s). X, Y and Z:%n", LOCATION_TO);
        validateUpdate(s -> location(routeBuilder, s, LOCATION_TO), scanner.nextLine());

        System.out.println("Enter a distance:");
        validateUpdate(s -> distance(routeBuilder, s), scanner.nextLine());

        if (isUpdate) {
            return routeBuilder.build();
        }

        return routeBuilder.id(nextId).creationTime(ZonedDateTime.now()).build();
    }

    private void validateUpdate(Function<String, Route.RouteBuilder> builderFunction, String value) {
        if (!isUpdate && value.isEmpty()) {
            throw new ValidateException("Value cannot be empty");
        }
        if (!value.isEmpty()) {
            builderFunction.apply(value);
        }
    }

    private Route.RouteBuilder name(Route.RouteBuilder builder, String value) {
        logValue(value);
        return builder.name(EntityValidator.checkEmpty(value));
    }

    private Route.RouteBuilder coordinates(Route.RouteBuilder builder, String value) {
        logValue(value);
        return builder.coordinates(checkCoordinates(getCoordinates(parseStringToTwoValues(value))));
    }

    private Route.RouteBuilder location(Route.RouteBuilder builder, String value, String location) {
        logValue(value);
        if (LOCATION_FROM.equals(location)) {
            return builder.from(checkLocation(getLocation(parseStringToValues(value, 3))));
        }
        Location locationTo = orNull(() -> getLocation(parseStringToValues(value, 3)));
        return builder.to(locationTo == null ? null : checkLocation(locationTo));
    }

    private Route.RouteBuilder distance(Route.RouteBuilder builder, String value) {
        logValue(value);
        return builder.distance(checkDistance(Long.parseLong(parseStringToValues(value, 1).get(0))));
    }

    private Location getLocation(List<String> coordinatesList) {
        if (coordinatesList.size() == 3) {
            return Location.builder()
                    .x(Double.parseDouble(coordinatesList.get(0)))
                    .y(Double.parseDouble(coordinatesList.get(1)))
                    .z(Integer.parseInt(coordinatesList.get(2)))
                    .build();
        }
        throw new ValidateException("Wrong coordinates count, expected size: 3 (X, Y and Z)");
    }

    private Coordinates getCoordinates(List<String> coordinatesList) {
        if (coordinatesList.size() == 2) {
            return Coordinates.builder()
                    .x(Long.parseLong(coordinatesList.get(0)))
                    .y(Float.parseFloat(coordinatesList.get(1)))
                    .build();
        } else {
            throw new ValidateException("Wrong coordinates count, expected size: 2 (X and Y)");
        }
    }

    private Location orNull(Supplier<Location> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }

    private Scanner getScanner() {
        Scanner scanner = Global.getGlobal().getScanner();
        if (scanner != null) {
            isRead = true;
            return scanner;
        }
        isRead = false;
        return new Scanner(System.in);
    }

    private void logValue(String value) {
        if (isRead) {
            System.out.println(value);
        }
    }
}
