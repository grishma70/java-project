import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // ✅ correct driver

            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/eventdb",
                "root",
                ""
            );

            System.out.println("Connected to database!");

        } catch (Exception e) {
            System.out.println("MySQL Driver not found!");
            e.printStackTrace();
        }
        return con;
    }
}