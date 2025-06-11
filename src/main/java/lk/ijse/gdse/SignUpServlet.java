package lk.ijse.gdse;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet("/signup")
public class SignUpServlet extends HttpServlet {
    //@Resource(name = "java:comp/env/jdbc/pool")

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String,String> user = mapper.readValue(req.getInputStream(), Map.class);

            ServletContext sc = req.getServletContext();
            BasicDataSource dataSource= (BasicDataSource) sc.getAttribute("ds");

            Connection connection=dataSource.getConnection();
            PreparedStatement pstm=
                    connection.prepareStatement("INSERT INTO users" +
                            "(id,name,password,email) Values (?,?,?,?)");
            pstm.setString(1, UUID.randomUUID().toString());
            pstm.setString(2, user.get("name"));
            pstm.setString(3,user.get("password"));
            pstm.setString(4,user.get("email"));

            int executed=pstm.executeUpdate();
            PrintWriter out=resp.getWriter();
            resp.setContentType("application/json");
            if(executed>0){
                resp.setStatus(HttpServletResponse.SC_ACCEPTED);
                mapper.writeValue(out,Map.of(
                        "code","201",
                        "status","success",
                        "message","User signed up successfully"
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
