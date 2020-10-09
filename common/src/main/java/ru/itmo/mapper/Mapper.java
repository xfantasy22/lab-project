package ru.itmo.mapper;

import lombok.Value;
import ru.itmo.model.domain.Coordinates;
import ru.itmo.model.domain.Location;
import ru.itmo.model.domain.Route;
import ru.itmo.model.domain.User;
import ru.itmo.model.dto.CoordinatesView;
import ru.itmo.model.dto.LocationView;
import ru.itmo.model.dto.RouteView;

import java.util.Arrays;

@Value
public class Mapper {
    public static Route toRoute(RouteView view, User user) {
        return Route.builder()
                .creationTime(view.getCreationTime())
                .name(view.getName())
                .distance(view.getDistance())
                .locations(Arrays.asList(toLocation(view.getFrom(), true), toLocation(view.getTo(), false)))
                .coordinates(toCoordinates(view.getCoordinates()))
                .user(user)
                .build();
    }

    private static Location toLocation(LocationView view, boolean isFrom) {
        return Location.builder()
                .x(view.getX())
                .y(view.getY())
                .z(view.getZ())
                .isFrom(isFrom)
                .build();
    }

    public static Coordinates toCoordinates(CoordinatesView view) {
        return Coordinates.builder()
                .x(view.getX())
                .y(view.getY())
                .build();
    }

    public static CoordinatesView toCoordinatesView(Coordinates coordinates) {
        return CoordinatesView.builder()
                .x(coordinates.getX())
                .y(coordinates.getY())
                .build();
    }

    public static LocationView toLocationView(Location location) {
        return LocationView.builder()
                .z(location.getZ())
                .y(location.getY())
                .x(location.getX())
                .build();
    }

    public static RouteView toRouteView(Route route) {
        RouteView.RouteViewBuilder builder = RouteView.builder()
                .id(route.getId())
                .name(route.getName())
                .distance(route.getDistance())
                .creationTime(route.getCreationTime())
                .coordinates(toCoordinatesView(route.getCoordinates()));

        route.getLocations().forEach(value -> {
            if (value.isFrom()) {
                builder.from(toLocationView(value));
            } else {
                builder.to(toLocationView(value));
            }
        });

        return builder.build();
    }
}
