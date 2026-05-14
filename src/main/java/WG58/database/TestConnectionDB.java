package WG58.database;

import java.sql.*;

public class TestConnectionDB {
    private final static String url = "jdbc:mysql://localhost:3306/db_name";
    private final static String username = "root";
    private final static String password = "";
    private static Connection connect;

    public static Connection createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection(url, username, password);
            return connect;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
