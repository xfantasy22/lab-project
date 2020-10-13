package ru.itmo.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.itmo.HibernateUtils;
import ru.itmo.mapper.Mapper;
import ru.itmo.model.domain.Coordinates;
import ru.itmo.model.domain.Location;
import ru.itmo.model.domain.Route;
import ru.itmo.model.domain.User;
import ru.itmo.model.dto.RouteView;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class RouteDaoImpl implements RouteDao {

    @Override
    public void save(RouteView route, User user) {
        Route route1 = Mapper.toRoute(route, user);
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(route1);
        route.setId(route1.getId());

        route1.getCoordinates().setRoute(route1);
        session.save(route1.getCoordinates());

        route1.getLocations().forEach(value -> {
            if (value != null) {
                value.setRoute(route1);
                session.save(value);
            }
        });

        transaction.commit();
        session.close();
    }

    public boolean exists(Long id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.get(Route.class, id) != null;
        }
    }

    private Route checkForUser(Long routeId, User user) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();

            log.debug("Route id: {}, user: {}", routeId, user);
            CriteriaQuery<Route> cq = cb.createQuery(Route.class);
            Root<Route> rootEntry = cq.from(Route.class);
            cq.select(rootEntry);
            Predicate userId = cb.equal(rootEntry.get("user"), user);
            Predicate routeIdPredicate = cb.equal(rootEntry.get("id"), routeId);
            cq.where(cb.and(userId, routeIdPredicate));

            TypedQuery<Route> allQuery = session.createQuery(cq);
            return allQuery.getSingleResult();
        }
    }

    @Override
    @Transactional
    public void update(RouteView route, User user) {
        Route route1 = Mapper.toRoute(route, user);
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Route routeFromDb = checkForUser(route1.getId(), user);
            log.debug("Entity from DB: {}", routeFromDb);
            fillRoute(routeFromDb, route1);

            session.update(routeFromDb);
            session.update(routeFromDb.getCoordinates());
            routeFromDb.getLocations().forEach(value -> {
                if (value != null) {
                    session.update(value);
                }
            });

            transaction.commit();
        }
    }

    @Override
    public void deleteById(Long id, User user) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaDelete<Route> cq = cb.createCriteriaDelete(Route.class);
            Root<Route> rootEntry = cq.from(Route.class);
            Predicate userId = cb.equal(rootEntry.get("user"), user);
            Predicate routeId = cb.equal(rootEntry.get("id"), id);
            cq.where(cb.and(userId, routeId));

            Transaction transaction = session.beginTransaction();
            session.createQuery(cq).executeUpdate();
            transaction.commit();
        }
    }

    @Override
    public void deleteByDistance(long distance, User user) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();

            CriteriaDelete<Route> cq = cb.createCriteriaDelete(Route.class);
            Root<Route> rootEntry = cq.from(Route.class);
            Predicate userId = cb.equal(rootEntry.get("user"), user);
            Predicate distancePredicate = cb.equal(rootEntry.get("distance"), distance);
            cq.where(cb.and(userId, distancePredicate));

            Transaction transaction = session.beginTransaction();
            session.createQuery(cq).executeUpdate();
            transaction.commit();
        }
    }

    @Override
    public List<RouteView> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();

            CriteriaQuery<Route> cq = cb.createQuery(Route.class);
            Root<Route> rootEntry = cq.from(Route.class);
            CriteriaQuery<Route> all = cq.select(rootEntry);

            TypedQuery<Route> allQuery = session.createQuery(all);
            return allQuery.getResultStream().map(Mapper::toRouteView).collect(Collectors.toList());
        }
    }

    public List<RouteView> findAllByUser(User user) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();

            CriteriaQuery<Route> cq = cb.createQuery(Route.class);
            Root<Route> rootEntry = cq.from(Route.class);
            cq.select(rootEntry);
            cq.where(cb.equal(rootEntry.get("user"), user));

            TypedQuery<Route> allQuery = session.createQuery(cq);

            return allQuery.getResultStream().map(Mapper::toRouteView).collect(Collectors.toList());
        }
    }

    @Override
    public void deleteAll(User user) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaDelete<Route> cq = cb.createCriteriaDelete(Route.class);
            Root<Route> rootEntry = cq.from(Route.class);
            cq.where(cb.equal(rootEntry.get("user"), user));
        }
    }

    private void fillRoute(Route exists, Route updated) {
        exists.setName(updated.getName());
        exists.setDistance(updated.getDistance());
        fillCoordinates(exists.getCoordinates(), updated.getCoordinates());
        fillLocation(exists.getLocations().get(0), updated.getLocations().get(0));
        if (updated.getLocations().size() == 2 && exists.getLocations().size() == 2) {
            fillLocation(exists.getLocations().get(1), updated.getLocations().get(1));
        }
    }

    private void fillCoordinates(Coordinates exists, Coordinates updated) {
        exists.setX(updated.getX());
        exists.setY(updated.getY());
    }

    private void fillLocation(Location exists, Location updated) {
        exists.setX(updated.getX());
        exists.setY(updated.getY());
        exists.setZ(updated.getZ());
    }
}
