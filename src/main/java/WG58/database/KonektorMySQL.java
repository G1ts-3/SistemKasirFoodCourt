package WG58.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class KonektorMySQL {
    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/wg58_foodcourt";
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

    // Static method utk get connection tanpa menentukan database (dipakai saat inisialisasi database)
    public static Connection getConnectionWithoutDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("[DB] MySQL Driver tidak ditemukan: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.out.println("[DB] Database connection gagal (tanpa DB): " + e.getMessage());
            return null;
        }
    }
}
