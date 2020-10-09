package ru.itmo.service;

import ru.itmo.exception.ValidateException;
import ru.itmo.model.dto.CoordinatesView;
import ru.itmo.model.dto.LocationView;
import ru.itmo.model.dto.RouteView;
import ru.itmo.util.LogUtils;
import ru.itmo.validator.EntityValidator;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Supplier;

import static ru.itmo.util.ParseUtils.parseStringToTwoValues;
import static ru.itmo.util.ParseUtils.parseStringToValues;

public class RouteCreatorImpl implements RouteCreator {
    private static final String LOCATION_FROM = "from";
    private static final String LOCATION_TO = "to";

    private boolean isUpdate;

    @Override
    public RouteView createRoute(Scanner scanner, boolean isUpdate) {
        this.isUpdate = isUpdate;
        return parseRouteFromConsole(scanner);
    }

    private RouteView parseRouteFromConsole(Scanner scanner) {
        RouteView.RouteViewBuilder routeBuilder = RouteView.builder();

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

        return routeBuilder.build();
    }

    private void validateUpdate(Function<String, RouteView.RouteViewBuilder> builderFunction, String value) {
        if (!isUpdate && value.isEmpty()) {
            throw new ValidateException("Value cannot be empty");
        }
        if (!value.isEmpty()) {
            builderFunction.apply(value);
        }
    }

    private RouteView.RouteViewBuilder name(RouteView.RouteViewBuilder builder, String value) {
        LogUtils.log(value);
        return builder.name(EntityValidator.checkEmpty(value));
    }

    private RouteView.RouteViewBuilder coordinates(RouteView.RouteViewBuilder builder, String value) {
        LogUtils.log(value);
        return builder.coordinates(EntityValidator.checkCoordinates(getCoordinates(parseStringToTwoValues(value))));
    }

    private RouteView.RouteViewBuilder location(RouteView.RouteViewBuilder builder, String value, String location) {
        LogUtils.log(value);
        if (LOCATION_FROM.equals(location)) {
            return builder.from(EntityValidator.checkLocation(getLocation(parseStringToValues(value, 3))));
        }
        LocationView locationTo = orNull(() -> getLocation(parseStringToValues(value, 3)));
        return builder.to(locationTo == null ? null : EntityValidator.checkLocation(locationTo));
    }

    private RouteView.RouteViewBuilder distance(RouteView.RouteViewBuilder builder, String value) {
        LogUtils.log(value);
        return builder.distance(EntityValidator.checkDistance(Long.parseLong(parseStringToValues(value, 1).get(0))));
    }

    private LocationView getLocation(List<String> coordinatesList) {
        if (coordinatesList.size() == 3) {
            return LocationView.builder()
                    .x(Double.parseDouble(coordinatesList.get(0)))
                    .y(Double.parseDouble(coordinatesList.get(1)))
                    .z(Integer.parseInt(coordinatesList.get(2)))
                    .build();
        }
        throw new ValidateException("Wrong coordinates count, expected size: 3 (X, Y and Z)");
    }

    private CoordinatesView getCoordinates(List<String> coordinatesList) {
        if (coordinatesList.size() == 2) {
            return CoordinatesView.builder()
                    .x(Long.parseLong(coordinatesList.get(0)))
                    .y(Float.parseFloat(coordinatesList.get(1)))
                    .build();
        } else {
            throw new ValidateException("Wrong coordinates count, expected size: 2 (X and Y)");
        }
    }

    private LocationView orNull(Supplier<LocationView> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }
}
