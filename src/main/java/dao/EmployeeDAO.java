package dao;


import model.Employee;
import util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class EmployeeDAO {
    // No need to create SessionFactory here anymore
    public void saveEmployee(Employee employee) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(employee);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error saving employee: " + e.getMessage());
        }
    }

    public List<Employee> getAllEmployees() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Employee", Employee.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching employees: " + e.getMessage());
        }
    }

    public Employee getEmployee(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Employee.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching employee: " + e.getMessage());
        }
    }

    public void updateEmployee(Employee employee) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(employee);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error updating employee: " + e.getMessage());
        }
    }

    public void deleteEmployee(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Employee employee = session.get(Employee.class, id);
            if (employee != null) {
                session.remove(employee);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting employee: " + e.getMessage());
        }
    }
}