package lk.ijse.gdse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.commons.dbcp2.BasicDataSource;

@WebListener
public class DataSource implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/emsdb");
        ds.setUsername("root");
        ds.setPassword("Ijse@1234");
        ds.setInitialSize(50);
        ds.setMaxTotal(100);

        ServletContext sc = sce.getServletContext();
        sc.setAttribute("ds", ds);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try{
            ServletContext sc = sce.getServletContext();
            BasicDataSource bs = (BasicDataSource) sc.getAttribute("DataSource");
            bs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
