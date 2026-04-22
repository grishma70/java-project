import java.sql.Connection;

public class TestDB {
    public static void main(String[] args) {
        Connection con = DBConnection.getConnection();
    }
}