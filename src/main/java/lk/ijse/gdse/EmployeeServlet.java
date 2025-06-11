package lk.ijse.gdse;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@WebServlet("/employee")
@MultipartConfig
public class EmployeeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        resp.setContentType("application/json");

        ServletContext sc = req.getServletContext();
        BasicDataSource dataSource = (BasicDataSource) sc.getAttribute("ds");

        try{
            Connection connection = dataSource.getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM employees");

            ResultSet rst = pstm.executeQuery();
            List<Map<String, String>> employees = new ArrayList<>();
            while (rst.next()) {
                Map<String, String> employee = new HashMap<>();
                employee.put("empId", rst.getString("empId"));
                employee.put("empPicture", rst.getString("empPicture"));
                employee.put("empName", rst.getString("empName"));
                employee.put("empAddress", rst.getString("empAddress"));
                employee.put("empEmail", rst.getString("empEmail"));
                employees.add(employee);
            }
            PrintWriter out = resp.getWriter();
            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(out, Map.of(
                    "code", "200",
                    "status", "success",
                    "data", employees
            ));
            connection.close();
        }catch (Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), Map.of(
                    "code", "500",
                    "status", "error",
                    "message", "Internal Server Error"
            ));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        PrintWriter out = resp.getWriter();

        String name = req.getParameter("empName");
        String address = req.getParameter("empAddress");
        String email = req.getParameter("empEmail");
        Part filrPart = req.getPart("empPicture");

        String originalFileName = filrPart.getSubmittedFileName();
        String fileName = UUID.randomUUID() + "_" + originalFileName;

        String uploadPath =  "C:\\Users\\User\\OneDrive\\Documents\\AAD\\JAVAEE\\WORK\\EMS-FN\\assets";
        java.io.File upload = new java.io.File(uploadPath);
        if (!upload.exists()) {
            upload.mkdirs();
        }

        String filePath = uploadPath + java.io.File.separator + fileName;
        filrPart.write(filePath);

        ServletContext sc = req.getServletContext();
        BasicDataSource dataSource = (BasicDataSource) sc.getAttribute("ds");

        try{
            Connection connection = dataSource.getConnection();
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO employees" +
                    "(empId,empName,empAddress,empEmail,empPicture) Values (?,?,?,?,?)");
            pstm.setString(1, UUID.randomUUID().toString());
            pstm.setString(2, name);
            pstm.setString(3, address);
            pstm.setString(4, email);
            pstm.setString(5, fileName);

            int executed = pstm.executeUpdate();
            if (executed > 0) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                mapper.writeValue(out, Map.of(
                        "code", "200",
                        "status", "success",
                        "message", "Employee saved successfully"
                ));
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(out, Map.of(
                        "code", "400",
                        "status", "error",
                        "message", "Bad Request"
                ));
            }
            connection.close();
        }catch (Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(out, Map.of(
                    "code", "500",
                    "status", "error",
                    "message", "Internal Server Error"
            ));
            e.printStackTrace();
        }
    }
}
