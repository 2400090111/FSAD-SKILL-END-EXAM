package com.klef.fsad.exam;

import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

public class ClientDemo {

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");

        try (SessionFactory sessionFactory = configuration.buildSessionFactory()) {
            int serviceId = insertService(sessionFactory);
            updateServiceById(sessionFactory, serviceId, "Cloud Support Service", "Completed");
            displayServices(sessionFactory);
        }
    }

    private static int insertService(SessionFactory sessionFactory) {
        Transaction transaction = null;
        int serviceId;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Service service = new Service(
                    "System Maintenance",
                    new Date(),
                    "Pending",
                    "IT Support",
                    2500.00
            );

            serviceId = (int) session.save(service);
            transaction.commit();
            System.out.println("Service record inserted with ID: " + serviceId);
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }

        return serviceId;
    }

    private static void updateServiceById(SessionFactory sessionFactory, int id, String name, String status) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String hql = "update Service set name = :serviceName, status = :serviceStatus where id = :serviceId";
            Query<?> query = session.createQuery(hql);
            query.setParameter("serviceName", name);
            query.setParameter("serviceStatus", status);
            query.setParameter("serviceId", id);

            int rowsUpdated = query.executeUpdate();
            transaction.commit();
            System.out.println("Number of records updated: " + rowsUpdated);
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    private static void displayServices(SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from Service";
            Query<Service> query = session.createQuery(hql, Service.class);
            List<Service> services = query.list();

            System.out.println("Service Records:");
            for (Service service : services) {
                System.out.println(service);
            }
        }
    }
}
