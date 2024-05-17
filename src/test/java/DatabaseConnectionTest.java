import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/employee";
        String username = "root";

        try (Connection connection = DriverManager.getConnection(url, username, null)) {
            System.out.println("Connection to the database established!");
        } catch (SQLException e) {
            System.out.println("Error while connecting to the database!");
            e.printStackTrace();
        }
    }
}
