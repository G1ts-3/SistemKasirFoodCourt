package tubes.kelompok4.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Kelas konfigurasi untuk mengatur koneksi database.
 */
public class DatabaseConfig {
    // Base URL tanpa nama database untuk membuat database awal
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "db_restoran";
    private static final String URL = BASE_URL + DB_NAME;
    private static final String USER = "root"; 
    private static final String PASSWORD = ""; 

    /**
     * Method untuk membuat database secara otomatis jika belum ada di MySQL.
     */
    public static void buatDatabaseJikaBelumAda() {
        try (Connection conn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Eksekusi pembuatan database
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("[Sistem] Database '" + DB_NAME + "' siap digunakan.");
            
        } catch (SQLException e) {
            System.out.println("Gagal membuat database otomatis: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method untuk mendapatkan koneksi ke database spesifik (db_restoran)
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
