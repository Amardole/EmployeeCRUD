package controller;

import dao.EmployeeDAO;
import model.Employee;
import util.HibernateUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/employees/*"})
public class EmployeeServlet extends HttpServlet {
    private EmployeeDAO employeeDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        employeeDAO = new EmployeeDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String path = req.getPathInfo() == null ? "/" : req.getPathInfo();
        System.out.println("GET request received for path: " + path);

        try {
            if (path.equals("/")) {
                System.out.println("Fetching all employees");
                resp.getWriter().write(gson.toJson(employeeDAO.getAllEmployees()));
            } else if (path.startsWith("/edit/")) {
                int id = Integer.parseInt(path.substring(6));
                System.out.println("Fetching employee with ID: " + id);
                Employee employee = employeeDAO.getEmployee(id);
                if (employee != null) {
                    resp.getWriter().write(gson.toJson(employee));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Employee not found\"}");
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Invalid endpoint\"}");
            }
        } catch (Exception e) {
            System.out.println("Error in doGet: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String action = req.getParameter("action");
        System.out.println("POST request received with action: " + action);
        Map<String, String> response = new HashMap<>();

        try {
            if ("register".equals(action)) {
                Employee emp = new Employee(
                    req.getParameter("firstName"),
                    req.getParameter("lastName"),
                    req.getParameter("email"),
                    req.getParameter("mobileNumber"),
                    Double.parseDouble(req.getParameter("salary")),
                    req.getParameter("department")
                );
                employeeDAO.saveEmployee(emp);
                response.put("message", "Employee registered successfully");
            } else if ("update".equals(action)) {
                Employee emp = employeeDAO.getEmployee(Integer.parseInt(req.getParameter("empId")));
                if (emp != null) {
                    emp.setFirstName(req.getParameter("firstName"));
                    emp.setLastName(req.getParameter("lastName"));
                    emp.setEmail(req.getParameter("email"));
                    emp.setMobileNumber(req.getParameter("mobileNumber"));
                    emp.setSalary(Double.parseDouble(req.getParameter("salary")));
                    emp.setDepartment(req.getParameter("department"));
                    employeeDAO.updateEmployee(emp);
                    response.put("message", "Employee updated successfully");
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.put("error", "Employee not found");
                }
            } else if ("delete".equals(action)) {
                int empId = Integer.parseInt(req.getParameter("empId"));
                employeeDAO.deleteEmployee(empId);
                response.put("message", "Employee deleted successfully");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.put("error", "Invalid action");
            }
        } catch (Exception e) {
            System.out.println("Error in doPost: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.put("error", e.getMessage());
        }
        resp.getWriter().write(gson.toJson(response));
    }

    @Override
    public void destroy() {
        HibernateUtil.shutdown(); // Cleanly close the SessionFactory
        System.out.println("Servlet destroyed, SessionFactory closed");
    }
}