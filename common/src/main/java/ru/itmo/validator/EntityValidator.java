package ru.itmo.validator;

import ru.itmo.exception.ValidateException;
import ru.itmo.model.dto.CoordinatesView;
import ru.itmo.model.dto.LocationView;
import ru.itmo.model.dto.RouteView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EntityValidator {
    private static final String POSITION_X = "Position X";
    private static final String POSITION_Y = "Position Y";
    private static final String LOCATION = "Location";

    public static <T> void checkNull(T value, String name) {
        if (value == null) {
            throw new ValidateException(name + " must not be null");
        }
    }

    public static CoordinatesView checkCoordinates(CoordinatesView coordinates) {
        checkNull(coordinates, "Coordinates");
        checkNull(coordinates.getX(), POSITION_X);

        if (coordinates.getX() < -896L) {
            throw new ValidateException(POSITION_X + " must not be less than -896");
        }

        if (coordinates.getY() < -804) {
            throw new ValidateException(POSITION_Y + " must not be less than -804");
        }

        return coordinates;
    }

    public static LocationView checkLocation(LocationView location) {
        checkNull(location, LOCATION);
        checkNull(location.getX(), POSITION_X);
        checkNull(location.getY(), POSITION_Y);

        return location;
    }

    public static String checkEmpty(String value) {
        checkNull(value, "Name");

        if (value.isEmpty()) {
            throw new ValidateException("Name must not be empty");
        }
        return value;
    }

    public static long checkDistance(long value) {
        if (value < 1) {
            throw new ValidateException("Distance must be more than 1");
        }
        return value;
    }

    public static List<RouteView> checkRoutes(List<RouteView> routeList) {
        List<RouteView> routes = new ArrayList<>();
        if (routeList == null || routeList.isEmpty()) {
            return Collections.emptyList();
        }

        final int[] counter = {0};

        removeDuplicatesById(routeList).forEach(value -> {
            try {
                checkEmpty(value.getName());
                checkDistance(value.getDistance());
                checkCoordinates(value.getCoordinates());
                checkLocation(value.getFrom());
                if (value.getTo() != null) {
                    checkLocation(value.getTo());
                }
                routes.add(value);
            } catch (Exception exception) {
                counter[0]++;
            }
        });
        counter[0] = counter[0] + routeList.size() - routes.size();
        if (counter[0] > 0) {
            System.out.printf("Count skipped values: %d%n", counter[0]);
        }

        return routes;
    }

    private static List<RouteView> removeDuplicatesById(List<RouteView> routes) {
        return routes.stream()
                .filter(value -> Objects.nonNull(value.getId()))
                .filter(distinctById(RouteView::getId))
                .filter(value -> value.getId() > 0L)
                .collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctById(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
