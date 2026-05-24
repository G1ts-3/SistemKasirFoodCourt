package WG58.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class KonektorMySQL {
    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sistem_kasir_food_court";
    private static final String DB_USER = "root";   
    private static final String DB_PASSWORD = "";

    // Static method utk get connection
    public static Connection getConnection() {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Return new Connection object ke database
            // Connection -> object utk komunikasi dgn db
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("[DB] MySQL Driver tidak ditemukan: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.out.println("[DB] Database connection gagal: " + e.getMessage());
            return null;
        }
    }
}
