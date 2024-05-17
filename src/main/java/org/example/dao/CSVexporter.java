package org.example.dao;
import org.example.entity.EmployeeEntity;
import org.example.entity.GroupEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class CSVexporter {

    private final SessionFactory sessionFactory;

    public CSVexporter(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void exportEmployeesToCSV(String filePath) {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery("SELECT e.name, e.salary, g.name FROM EmployeeEntity e JOIN GroupEntity g ON e.groupId = g.id", Object[].class);
            List<Object[]> result = query.getResultList();
            writeEmployeesToCSV(result, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeEmployeesToCSV(List<Object[]> employees, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("Imię,Nazwisko,Pensja,Grupa\n");

            for (Object[] employee : employees) {
                writer.append(String.valueOf(employee[0])).append(",");
                writer.append(String.valueOf(employee[1])).append(",");
                writer.append(String.valueOf(employee[2])).append("\n");
            }
        }
    }
}
