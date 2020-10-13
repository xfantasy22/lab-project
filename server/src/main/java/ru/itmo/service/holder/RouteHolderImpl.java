package ru.itmo.service.holder;

import lombok.extern.slf4j.Slf4j;
import ru.itmo.context.ServerContext;
import ru.itmo.dao.RouteDao;
import ru.itmo.exception.ValidateException;
import ru.itmo.model.ClientRequest;
import ru.itmo.model.domain.User;
import ru.itmo.model.dto.RouteView;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
public class RouteHolderImpl implements RouteHolder {
    private static final List<RouteView> ROUTES = new CopyOnWriteArrayList<>();
    private static final ZonedDateTime CREATION_TIME = ZonedDateTime.now();
    private static ZonedDateTime updateTime = CREATION_TIME;

    public void addElement(ClientRequest request) {
        RouteView routeView = Objects.requireNonNull(request.getRoute());
        routeView.setCreationTime(ZonedDateTime.now());
        ServerContext.getInstance().getRouteDao().save(routeView, request.getUser());
        ROUTES.add(routeView);
        log.info("Route has been added: {}", routeView);
        updateTime = ZonedDateTime.now();
    }

    public void updateElement(ClientRequest request) {
        long id = Objects.requireNonNull(request.getArgument()).longValue();
        if (!checkExistsId(id)) {
            throw new ValidateException("Id not found, id: " + id);
        }
        ROUTES.stream()
                .filter(element -> element.getId().equals(id))
                .findFirst()
                .map(element -> updateElement(element, request.getRoute(), request.getUser()))
                .orElseThrow(() -> new RuntimeException(String.format("Element cannot be updated, id: %s", id)));
    }

    private RouteView updateElement(RouteView exist, RouteView updated, User user) {
        if (Objects.equals(exist, updated)) {
            throw new ValidateException(String.format("Elements are equal. \n Exist: %s \n Updated: %s", exist, updated));
        }
        RouteView updatedRoute = exist.toBuilder()
                .name(updated.getName() != null ? updated.getName() : exist.getName())
                .coordinates(updated.getCoordinates() != null ? updated.getCoordinates() : exist.getCoordinates())
                .from(updated.getFrom() != null ? updated.getFrom() : exist.getFrom())
                .to(updated.getTo() != null ? updated.getTo() : exist.getTo())
                .distance(updated.getDistance() != 0 ? updated.getDistance() : exist.getDistance())
                .build();

        log.debug("Updated route: {}", updatedRoute);

        ServerContext.getInstance().getRouteDao().update(updatedRoute, user);

        ROUTES.removeIf(value -> value.getId().equals(exist.getId()));
        ROUTES.add(updatedRoute);

        log.info("Route has been updated: {}", updatedRoute);

        updateTime = ZonedDateTime.now();
        return updatedRoute;
    }

    public void removeElementById(ClientRequest request) {
        RouteDao routeDao = ServerContext.getInstance().getRouteDao();
        long id = Objects.requireNonNull(request.getArgument()).longValue();
        ROUTES.removeIf(element -> {
            if (element.getId().equals(id)) {
                routeDao.deleteById(element.getId(), request.getUser());
                if (routeDao.exists(id)) {
                    log.error("Entity cannot be removed for current user: {}", request.getUser());
                    return false;
                }
                log.info("Element has been removed, element: {}", element);
                return true;
            }
            return false;
        });
        updateTime = ZonedDateTime.now();
    }

    public void clear(User user) {
        RouteDao routeDao = ServerContext.getInstance().getRouteDao();
        List<RouteView> routeViews = routeDao.findAllByUser(user);
        ROUTES.removeIf(routeViews::contains);
        routeDao.deleteAll(user);
        updateTime = ZonedDateTime.now();
        log.info("Collection has been updated, removed entities: {}", routeViews);
    }

    public void insertElementAtIndex(ClientRequest request) {
        int index = checkIndex(Objects.requireNonNull(request.getArgument()).intValue());
        RouteView routeView = Objects.requireNonNull(request.getRoute());
        routeView.setCreationTime(ZonedDateTime.now());
        ServerContext.getInstance().getRouteDao().save(routeView, request.getUser());
        ROUTES.add(index, routeView);
        log.info("Route has been added: {}, by index: {}", request.getRoute(), request.getArgument());
        updateTime = ZonedDateTime.now();
    }

    private int checkIndex(int index) {
        if (index < 0 || index > ROUTES.size()) {
            throw new ValidateException(String.format("Index is incorrect. Possible index range from 0 to %s", ROUTES.size()));
        }
        return index;
    }

    public void addElementIfMax(ClientRequest request) {
        RouteView routeView = request.getRoute();
        int compareResult = Objects.requireNonNull(routeView).compareTo(Collections.max(ROUTES));
        if (compareResult > 0) {
            ServerContext.getInstance().getRouteDao().save(routeView, request.getUser());
            ROUTES.add(routeView);
            return;
        }
        throw new RuntimeException("Element cannot be inserted");
    }

    public void removeElementsLessThanLower(ClientRequest request) {
        RouteDao routeDao = ServerContext.getInstance().getRouteDao();
        RouteView route = Objects.requireNonNull(request.getRoute());
        ROUTES.removeIf(value -> {
            if (route.compareTo(value) < 0) {
                routeDao.deleteById(value.getId(), request.getUser());
                if (routeDao.exists(value.getId())) {
                    log.error("Entity cannot be removed for current user: {}", request.getUser());
                    return false;
                }
                log.info("Element removed: {}", value);
                updateTime = ZonedDateTime.now();
                return true;
            }
            return false;
        });
    }

    public void removeElementsIfDistanceAreEqual(ClientRequest request) {
        RouteDao routeDao = ServerContext.getInstance().getRouteDao();
        long distance = Objects.requireNonNull(request.getArgument()).longValue();
        ROUTES.removeIf(value -> {
            if (value.getDistance() == distance) {
                routeDao.deleteByDistance(distance, request.getUser());
                if (routeDao.exists(value.getId())) {
                    log.error("Entity cannot be removed for current user: {}", request.getUser());
                    return false;
                }
                updateTime = ZonedDateTime.now();
                log.info("Element: {} has been deleted", value);
                return true;
            }
            log.warn("Elements with such distance {} is not present", distance);
            return false;
        });
    }

    public String getElementByMaxName() {
        return ROUTES.stream()
                .max(Comparator.comparing(RouteView::getName))
                .orElseThrow(() -> new RuntimeException("Max element is not present"))
                .toString();
    }

    public String groupCountingById() {
        return ROUTES.stream().collect(Collectors.groupingBy(RouteView::getId)).toString();
    }

    public String showAllElements() {
        System.out.println(ServerContext.getInstance().getRouteDao().findAll());
        StringBuilder stringBuilder = new StringBuilder();
        ROUTES.stream()
                .sorted(Comparator.comparing(RouteView::getName))
                .forEach(value -> stringBuilder.append(value).append("\n"));
        return stringBuilder.toString();
    }

    public boolean checkExistsId(Long id) {
        return ROUTES.stream().anyMatch(value -> value.getId().equals(id));
    }

    public void initCollection() {
        ROUTES.addAll(ServerContext.getInstance().getRouteDao().findAll());
        log.info("Collection has been initialized");
        updateTime = ZonedDateTime.now();
    }

    public String getInfo() {
        return "Collection type: " + ROUTES.getClass().toString() + ".\n" +
                "Creation time: " + CREATION_TIME.toString() + ".\n" +
                "Data count: " + ROUTES.size() + ".\n" +
                "Last update time: " + updateTime.toString();
    }
}