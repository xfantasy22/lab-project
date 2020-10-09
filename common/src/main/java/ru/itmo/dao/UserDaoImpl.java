package ru.itmo.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.itmo.HibernateUtils;
import ru.itmo.model.domain.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class UserDaoImpl implements UserDao {
    @Override
    public void save(User user) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(user);
        transaction.commit();
        session.close();
    }

    @Override
    public User findOne(String username) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder cb = session.getCriteriaBuilder();

        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);
        cr.select(root).where(cb.equal(root.get("username"), username));
        Query<User> query = session.createQuery(cr);
        query.setMaxResults(1);
        List<User> result = query.getResultList();

        transaction.commit();
        session.close();

        return result.stream().findFirst().orElse(null);
    }
}
