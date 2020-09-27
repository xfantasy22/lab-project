package ru.itmo.service.holder;

import lombok.extern.slf4j.Slf4j;
import ru.itmo.context.ServerContext;
import ru.itmo.exception.ValidateException;
import ru.itmo.model.Route;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class RouteHolderImpl implements RouteHolder {
    private static final List<Route> ROUTES = new ArrayList<>();
    private static final ZonedDateTime CREATION_TIME = ZonedDateTime.now();
    private static ZonedDateTime updateTime = CREATION_TIME;
    private static Long startId = 1L;

    private void addElement(Route route, Integer value) {
        route = route.toBuilder().id(getNextId()).creationTime(ZonedDateTime.now()).build();
        if (value == null) {
            ROUTES.add(route);
            log.info("Route has been added: {}", route);
        } else {
            ROUTES.add(value, route);
            log.info("Route has been added: {}, by index: {}", route, value);
        }
        updateTime = ZonedDateTime.now();
    }

    public void addElement(Route route) {
        addElement(route, null);
    }

    public void updateElement(long id, Route route) {
        if (!checkExistsId(id)) {
            throw new ValidateException("Id not found, id: " + id);
        }
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
        ROUTES.add(updatedRoute);
        log.info("Route has been updated: {}", updatedRoute);
        updateTime = ZonedDateTime.now();
        return updated;
    }

    public void removeElementById(Long id) {
        ROUTES.removeIf(element -> {
            if (element.getId().equals(id)) {
                log.info("Element has been removed by id: {}, element: {}", id, element);
                return true;
            }
            return false;
        });
        updateTime = ZonedDateTime.now();
    }

    public void clear() {
        ROUTES.clear();
        startId = 1L;
        updateTime = ZonedDateTime.now();
        log.info("Collection has been cleaned");
    }

    public void insertElementAtIndex(int index, Route route) {
        addElement(route, checkIndex(index));
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
        ROUTES.removeIf(value -> {
            if (route.compareTo(value) < 0) {
                log.info("Element removed: {}", value);
                updateTime = ZonedDateTime.now();
                return true;
            }
            return false;
        });
    }

    public void removeElementsIfDistanceAreEqual(long distance) {
        ROUTES.removeIf(value -> {
            if (value.getDistance() == distance) {
                updateTime = ZonedDateTime.now();
                log.info("Element: {} with distance {} has been deleted", value, distance);
                return true;
            }
            log.warn("Elements with such distance {} is not present", distance);
            return false;
        });
    }

    public String getElementByMaxName() {
        return ROUTES.stream()
                .max(Comparator.comparing(Route::getName))
                .orElseThrow(() -> new RuntimeException("Max element is not present"))
                .toString();
    }

    public String groupCountingById() {
        return ROUTES.stream().collect(Collectors.groupingBy(Route::getId)).toString();
    }

    public String showAllElements() {
        if (ROUTES.isEmpty()) {
            return "Collection is empty";
        }
        StringBuilder stringBuilder = new StringBuilder();
        ROUTES.stream()
                .sorted(Comparator.comparing(Route::getName))
                .forEach(value -> stringBuilder.append(value).append("\n"));
        return stringBuilder.toString();
    }

    public boolean checkExistsId(Long id) {
        return ROUTES.stream().anyMatch(value -> value.getId().equals(id));
    }

    public void writeToFile(String fileName) {
        ServerContext context = ServerContext.getInstance();
        context.getXmlParser().writeDataToFile(ROUTES, fileName);
    }

    public void initCollection(List<Route> routeList) {
        startId = routeList.stream()
                .map(Route::getId)
                .max(Comparator.comparing(Long::longValue))
                .orElse(1L);
        ROUTES.addAll(routeList);
        log.info("Collection has been initialized");
        updateTime = ZonedDateTime.now();
    }

    private Long getNextId() {
        return startId++;
    }

    public String getInfo() {
        return "Collection type: " + ROUTES.getClass().toString() + ".\n" +
                "Creation time: " + CREATION_TIME.toString() + ".\n" +
                "Data count: " + ROUTES.size() + ".\n" +
                "Last update time: " + updateTime.toString();
    }
}