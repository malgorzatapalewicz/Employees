package org.example.dao;

import org.example.entity.EmployeeEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class EmployeeDAO {
    private final SessionFactory sessionFactory;

    public EmployeeDAO(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    public void addEmployee(EmployeeEntity employee) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(employee);
            session.getTransaction().commit();
        }
    }

    public EmployeeEntity getEmployeeById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(EmployeeEntity.class, id);
        }
    }

    public List<EmployeeEntity> getAllEmployees() {
        try (Session session = sessionFactory.openSession()) {
            //HQL - Hibernate Query Language
            Query<EmployeeEntity> query = session.createQuery("from EmployeeEntity", EmployeeEntity.class);
            return query.list();
        }
    }

    public void deleteEmployee(EmployeeEntity employee) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(employee);
            session.getTransaction().commit();
        }
    }

    public List<EmployeeEntity> getEmployeesByGroupId(int groupId) {
        try (Session session = sessionFactory.openSession()) {
            Query<EmployeeEntity> query = session.createQuery("select e from EmployeeEntity e where e.groupId = :groupId", EmployeeEntity.class);
            query.setParameter("groupId", groupId);
            return query.list();
        }
    }


}
