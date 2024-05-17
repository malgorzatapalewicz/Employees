package org.example.dao;
import org.example.entity.RateEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class RateDAO {
    private final SessionFactory sessionFactory;

    public RateDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addRate(RateEntity rate) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(rate);
            session.getTransaction().commit();
        }
    }

    public RateEntity getRateById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(RateEntity.class, id);
        }
    }

    public List<RateEntity> getAllRates() {
        try (Session session = sessionFactory.openSession()) {
            Query<RateEntity> query = session.createQuery("from RateEntity", RateEntity.class);
            return query.list();
        }
    }
}
