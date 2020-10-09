package ru.itmo.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.itmo.HibernateUtils;
import ru.itmo.mapper.Mapper;
import ru.itmo.model.domain.Location;
import ru.itmo.model.domain.Route;
import ru.itmo.model.domain.User;
import ru.itmo.model.dto.RouteView;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RouteDaoImpl implements RouteDao {

    @Override
    public void save(RouteView route, User user) {
        Route route1 = Mapper.toRoute(route.toBuilder().creationTime(ZonedDateTime.now()).build(), user);
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(route1);
        route.setId(route1.getId());

        route1.getCoordinates().setRoute(route1);
        session.save(route1.getCoordinates());

        route1.getLocations().forEach(value -> {
            value.setRoute(route1);
            session.save(value);
        });

        transaction.commit();
        session.close();
    }

    @Override
    public void update(RouteView route, User user) {
        Route route1 = Mapper.toRoute(route, user);
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Route loadRoute = session.load(Route.class, route1.getId());
        session.update(route1);

        route1.getCoordinates().setId(loadRoute.getCoordinates().getId());
        session.update(route1.getCoordinates());

        loadRoute.getLocations().forEach(value -> {
            Location updatedLoc;
            if (value.isFrom()) {
                updatedLoc = route1.getLocations().stream()
                        .filter(Location::isFrom)
                        .peek(location -> location.setId(value.getId()))
                        .findFirst()
                        .orElse(null);
            } else {
                updatedLoc = route1.getLocations().stream()
                        .filter(location -> !location.isFrom())
                        .peek(location -> location.setId(value.getId()))
                        .findFirst()
                        .orElse(null);
            }
            session.update(updatedLoc);
        });

        transaction.commit();
        session.close();
    }

    @Override
    public void deleteById(Long id, User user) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Route loadRoute = session.load(Route.class, id);
        session.delete(loadRoute);
        session.flush();
        transaction.commit();
    }

    @Override
    public void deleteByDistance(long distance, User user) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaDelete<Route> cq = cb.createCriteriaDelete(Route.class);
        Root<Route> rootEntry = cq.from(Route.class);
        cq.where(cb.equal(rootEntry.get("user_id"), user.getId()))
                .where(cb.equal(rootEntry.get("distance"), distance));
        session.close();
    }

    @Override
    public List<RouteView> findAll() {
        Session session = HibernateUtils.getSessionFactory().openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Route> cq = cb.createQuery(Route.class);
        Root<Route> rootEntry = cq.from(Route.class);
        CriteriaQuery<Route> all = cq.select(rootEntry);

        TypedQuery<Route> allQuery = session.createQuery(all);
        session.flush();
        session.close();
        return allQuery.getResultList().stream().map(Mapper::toRouteView).collect(Collectors.toList());
    }

    @Override
    public void deleteAll(User user) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaDelete<Route> cq = cb.createCriteriaDelete(Route.class);
        Root<Route> rootEntry = cq.from(Route.class);
        cq.where(cb.equal(rootEntry.get("user_id"), user.getId()));
        session.close();
    }
}
