package ru.itmo.dao;

import ru.itmo.model.domain.User;

public interface UserDao {
    void save(User user);
    User findOne(String username);
}
