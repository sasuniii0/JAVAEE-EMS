package lk.ijse.gdse;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@WebServlet("/employee")
public class EmployeeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            ObjectMapper mapper = new ObjectMapper();
            ServletContext sc = req.getServletContext();

            List<Map<String, String>> employees = new ArrayList<>();

            BasicDataSource dataSource = (BasicDataSource) sc.getAttribute("ds");
            Connection connection = dataSource.getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM employees");

            ResultSet rst = pstm.executeQuery();
            while (rst.next()) {
                Map<String, String> employee = new HashMap<>();
                employee.put("empId", rst.getString("empId"));
                employee.put("empName", rst.getString("empName"));
                employee.put("empAddress", rst.getString("empAddress"));
                employee.put("empEmail", rst.getString("empEmail"));
                employees.add(employee);
            }
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(resp.getWriter(), employees);
            connection.close();

        }catch (Exception e){
            ObjectMapper mapper = new ObjectMapper();
            PrintWriter out=resp.getWriter();
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(out,Map.of(
                    "code","500",
                    "status","error",
                    "message","Internal Server Error"
            ));
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String,String> employee = mapper.readValue(req.getInputStream(), Map.class);

            ServletContext sc = req.getServletContext();
            BasicDataSource dataSource= (BasicDataSource) sc.getAttribute("ds");

            Connection connection=dataSource.getConnection();
            PreparedStatement pstm=
                    connection.prepareStatement("INSERT INTO employees" +
                            "(empId,empName,empAddress,empEmail) Values (?,?,?,?)");
            pstm.setString(1, UUID.randomUUID().toString());
            pstm.setString(2, employee.get("empName"));
            pstm.setString(3,employee.get("empAddress"));
            pstm.setString(4,employee.get("empEmail"));

            int executed=pstm.executeUpdate();
            PrintWriter out=resp.getWriter();
            resp.setContentType("application/json");
            if(executed>0){
                resp.setStatus(HttpServletResponse.SC_ACCEPTED);
                mapper.writeValue(out,Map.of(
                        "code","201",
                        "status","success",
                        "message","Employee saved successfully"
                ));
            }else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(out,Map.of(
                        "code","400",
                        "status","error",
                        "message","Bad Request"
                ));
            }
            connection.close();
        } catch (Exception e) {
            ObjectMapper mapper = new ObjectMapper();
            PrintWriter out=resp.getWriter();
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(out,Map.of(
                    "code","500",
                    "status","error",
                    "message","Internal Server Error"
            ));
            throw new RuntimeException(e);
        }
    }
}
