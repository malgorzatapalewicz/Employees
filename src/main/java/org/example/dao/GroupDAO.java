package org.example.dao;

import org.example.entity.GroupEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class GroupDAO {
    private final SessionFactory sessionFactory;

    public GroupDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    public void addGroup(GroupEntity group) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(group);
            session.getTransaction().commit();
        }
    }

    public GroupEntity getGroupById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(GroupEntity.class, id);
        }
    }

    public List<GroupEntity> getAllGroups() {
        try (Session session = sessionFactory.openSession()) {
            Query<GroupEntity> query = session.createQuery("from GroupEntity", GroupEntity.class);
            return query.list();
        }
    }


    public GroupEntity getGroupByName(String groupName) {
        try (Session session = sessionFactory.openSession()) {
            Query<GroupEntity> query = session.createQuery("FROM GroupEntity WHERE name = :name", GroupEntity.class);
            query.setParameter("name", groupName);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getGroupNameById(int groupId) {
        try (Session session = sessionFactory.openSession()) {
            Query<String> query = session.createQuery("SELECT name FROM GroupEntity WHERE id = :groupId", String.class);
            query.setParameter("groupId", groupId);
            return query.uniqueResult();
        }
    }



}
