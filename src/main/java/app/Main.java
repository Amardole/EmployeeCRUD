package app;

import java.io.File;
import java.io.IOException;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import util.HibernateUtil;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Initializing Hibernate...");
        HibernateUtil.getSessionFactory();
        System.out.println("Hibernate initialized");
        
        // Use a different port if 8080 is busy
        int port = findAvailablePort(8080, 8090);
        
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);

        // Configure webapp
        String webappDir = new File("src/main/webapp").getAbsolutePath();
        Context context = tomcat.addWebapp("", webappDir);
        
        // Disable default servlet auto-registration
        System.setProperty("tomcat.util.scan.StandardJarScanFilter.jarsToSkip", "*");
        
        // Add your servlet
        tomcat.addServlet("", "EmployeeServlet", "controller.EmployeeServlet");
        context.addServletMappingDecoded("/employees/*", "EmployeeServlet");

        // Start server
        tomcat.getConnector();
        System.out.println("Starting Tomcat on port " + port + "...");
        tomcat.start();
        System.out.println("Tomcat started on http://localhost:" + port);
        System.out.println("Access displayEmp.html at http://localhost:" + port + "/displayEmp.html");
        tomcat.getServer().await();
    }

    private static int findAvailablePort(int min, int max) {
        for (int port = min; port <= max; port++) {
            try {
                new java.net.ServerSocket(port).close();
                return port;
            } catch (IOException e) {
                // Port in use, try next one
            }
        }
        throw new RuntimeException("No available port found between " + min + " and " + max);
    }
}