package Bank;

import java.sql.DriverManager;
import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/bank34";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement statement = conn.createStatement()) {

                statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "name VARCHAR(50)," +
                        "last_name VARCHAR(50)" +
                        "middle_name VARCHAR(50)," +
                        "birthday DATE," +
                        "login VARCHAR(50) UNIQUE," +
                        "password VARCHAR(255))");

                statement.executeUpdate("CREATE TABLE IF NOT EXISTS accounts (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "user_id INT," +
                        "balance DOUBLE," +
                        "FOREIGN KEY (user_id) REFERENCES users(id))");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
