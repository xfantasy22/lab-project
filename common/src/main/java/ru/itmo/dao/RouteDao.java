package ru.itmo.dao;

import ru.itmo.model.domain.User;
import ru.itmo.model.dto.RouteView;

import java.util.List;

public interface RouteDao {
    void save(RouteView route, User user);

    void update(RouteView route, User user);

    void deleteById(Long id, User user);

    void deleteByDistance(long distance, User user);

    List<RouteView> findAll();

    void deleteAll(User user);

    boolean exists(Long id);

    List<RouteView> findAllByUser(User user);
}
