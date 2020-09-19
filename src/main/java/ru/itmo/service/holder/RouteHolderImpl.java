package ru.itmo.service.holder;

import ru.itmo.context.Context;
import ru.itmo.context.Global;
import ru.itmo.exception.ValidateException;
import ru.itmo.model.Route;
import ru.itmo.model.State;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class RouteHolderImpl implements RouteHolder {
    private static final List<Route> ROUTES = new ArrayList<>();
    private static final ZonedDateTime CREATION_TIME = ZonedDateTime.now();
    private static ZonedDateTime updateTime = CREATION_TIME;
    private static Long startId = 1L;

    public Route addElement(Route route) {
        ROUTES.add(route);
        updateTime = now();
        return route;
    }

    public void updateElement(Long id, Route route) {
        ROUTES.stream()
                .filter(element -> element.getId().equals(id))
                .findFirst()
                .map(element -> updateElement(element, route))
                .orElseThrow(() -> new RuntimeException(String.format("Element cannot be updated, id: %s", id)));
    }

    private Route updateElement(Route exist, Route updated) {
        if (Objects.equals(exist, updated)) {
            throw new ValidateException(String.format("Elements are equal. \n Exist: %s \n Updated: %s", exist, updated));
        }
        Route updatedRoute = exist.toBuilder()
                .name(updated.getName() != null ? updated.getName() : exist.getName())
                .coordinates(updated.getCoordinates() != null ? updated.getCoordinates() : exist.getCoordinates())
                .from(updated.getFrom() != null ? updated.getFrom() : exist.getFrom())
                .to(updated.getTo() != null ? updated.getTo() : exist.getTo())
                .distance(updated.getDistance() != 0 ? updated.getDistance() : exist.getDistance())
                .build();

        removeElementById(exist.getId());

        return addElement(updatedRoute);
    }

    public void removeElementById(Long id) {
        ROUTES.removeIf(element -> element.getId().equals(id));
        updateTime = now();
    }

    public void clear() {
        ROUTES.clear();
        updateTime = now();
    }

    public void insertElementAtIndex(int index, Route route) {
        ROUTES.add(checkIndex(index), route);
        updateTime = now();
    }

    public void getElementAtIndex(int index) {
        checkIndex(index);
    }

    private int checkIndex(int index) {
        if (index < 0 || index > ROUTES.size()) {
            throw new ValidateException(String.format("Index is incorrect. Possible index range from 0 to %s", ROUTES.size()));
        }
        return index;
    }

    public void addElementIfMax(Route route) {
        int compareResult = route.compareTo(Collections.max(ROUTES));

        if (compareResult > 0) {
            addElement(route);
            return;
        }

        throw new RuntimeException("Element cannot be inserted");
    }

    public void removeElementsLessThanLower(Route route) {
        ROUTES.removeIf(value -> route.compareTo(value) < 0);
        updateTime = now();
    }

    public void removeElementsIfDistanceAreEqual(long distance) {
        if (ROUTES.removeIf(value -> value.getDistance() == distance)) {
            System.out.printf("All elements with distance %s has been deleted%n", distance);
        } else {
            System.out.printf("Elements with such distance %s is not present%n", distance);
        }

        updateTime = now();
    }

    public Route getElementByMaxName() {
        return ROUTES.stream()
                .max(Comparator.comparing(Route::getName))
                .orElseThrow(() -> new RuntimeException("Max element is not present"));
    }

    public Map<Long, List<Route>> groupCountingById() {
        return ROUTES.stream().collect(Collectors.groupingBy(Route::getId));
    }

    public void showAllElements() {
        if (ROUTES.isEmpty()) {
            System.out.println("Collection is empty");
        }
        ROUTES.forEach(System.out::println);
    }

    public boolean checkExistsId(Long id) {
        return ROUTES.stream().anyMatch(value -> value.getId().equals(id));
    }

    public void writeToFile() {
        Context context = Context.getContext();
        context.getXmlParser().writeDataToFile(ROUTES, Global.getGlobal().getFileName());
    }

    public State readFromFile(String fileName) {
        List<Route> routeList;
        try {
            routeList = Context.getContext().getXmlParser().readDataFromFile(fileName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return State.SWITCH_STATE;
        }

        startId = routeList.stream()
                .map(Route::getId)
                .max(Comparator.comparing(Long::longValue))
                .orElse(1L);
        ROUTES.addAll(routeList);
        updateTime = now();
        return State.RUN;
    }

    public Long getNextId() {
        return startId++;
    }

    public String getInfo() {
        return "Collection type: " + ROUTES.getClass().toString() + ".\n " +
                "Creation time: " + CREATION_TIME.toString() + ".\n " +
                "Data count: " + ROUTES.size() + ".\n " +
                "Last update time: " + updateTime.toString();
    }

    private ZonedDateTime now() {
        return ZonedDateTime.now();
    }
}