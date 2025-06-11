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
        BasicDataSource bs = new BasicDataSource();
        bs.setDriverClassName("com.mysql.cj.jdbc.Driver");
        bs.setUrl("jdbc:mysql://localhost:3306/emsdb");
        bs.setUsername("root");
        bs.setPassword("Ijse@1234");
        bs.setInitialSize(50);
        bs.setMaxTotal(100);

        ServletContext sc = sce.getServletContext();
        sc.setAttribute("ds", bs);
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
