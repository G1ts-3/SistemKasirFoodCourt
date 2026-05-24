package WG58.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    // Method utk initialize database ketika program di run
    public static void initialize() {
        System.out.println("[DB] Initializing database...");
        
        // Create database
        createDatabase();
        System.out.println("[DB] Database created.");
        // Create 7 tables
        createAllTables();
        System.out.println("[DB] All tables created.");
        // Insert sample data
        insertSampleData();
        System.out.println("[DB] Sample data inserted.");
    }

    // Method untuk create db
    public static void createDatabase() {
        try {
            // Connection langsung ke MySQL (tanpa DB)
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "");
            
            // Statement -> object utk execute SQL Query
            Statement stmt = conn.createStatement();

            // SQL Query untuk create db
            String sql = "CREATE DATABASE IF NOT EXISTS sistem_kasir_food_court " + 
                         "CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci"; 

            // executeUpdate() -> jalankan CREATE/INSERT/DELETE/UPDATE query
            stmt.executeUpdate(sql);

            // Close resources (statement & connection)
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("[DB] Error create database: " + e.getMessage());
        }
    }

    // Method utk create 7 tabel
    public static void createAllTables() {
        try {
            // Get connection ke db
            Connection conn = KonektorMySQL.getConnection();

            if (conn == null) {
                System.out.println("[DB] Connection error");
                return;
            }

            // Create 7 tables
            createTenantTable(conn);
            createMenuTable(conn);
            createStokMenuTable(conn);
            createItemPesananTable(conn);
            createPembayaranTable(conn);
            createRatingTable(conn);
            
            // Close connection
            conn.close();
        } catch (Exception e) {
            System.out.println("[DB] Error create tables: " + e.getMessage());
        }
    }

    // Method utk create Tenant table
    public static void createTenantTable(Connection conn) {
        try {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS tenant (" +
                        "  id_tenant VARCHAR(10) PRIMARY KEY," +
                        "  nama_tenant VARCHAR(100) NOT NULL," +
                        "  username VARCHAR(50) UNIQUE NOT NULL," +
                        "  password VARCHAR(100) NOT NULL," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")";

            stmt.executeQuery(sql);
            stmt.close();
        } catch (Exception e) {
            System.out.println("[DB] Error create tenant table: " + e.getMessage());
        }
    }

    // Method utk create Menu table
    public static void createMenuTable(Connection conn) {
        try {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS menu (" +
                        "  id_menu VARCHAR(10) PRIMARY KEY," +
                        "  nama_menu VARCHAR(100) NOT NULL," +
                        "  jenis ENUM('Makanan', 'Minuman') NOT NULL," +
                        "  harga_kupon INT NOT NULL," +
                        "  id_tenant VARCHAR(10) NOT NULL," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  FOREIGN KEY (id_tenant) REFERENCES tenant(id_tenant) ON DELETE CASCADE" +
                        ")";

            stmt.executeQuery(sql);
            stmt.close();
        } catch (Exception e) {
            System.out.println("[DB] Error create menu table: " + e.getMessage());
        }
    }

    // Method utk create StokMenu table
    public static void createStokMenuTable(Connection conn) {
        try {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS stok_menu (" +
                        "  id_stok INT PRIMARY KEY AUTO_INCREMENT," +
                        "  id_menu VARCHAR(10) NOT NULL," +
                        "  id_tenant VARCHAR(10) NOT NULL," +
                        "  jumlah_stok INT NOT NULL DEFAULT 0," +
                        "  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                        "  FOREIGN KEY (id_menu) REFERENCES menu(id_menu) ON DELETE CASCADE," +
                        "  FOREIGN KEY (id_tenant) REFERENCES tenant(id_tenant) ON DELETE CASCADE" +
                        ")";

            stmt.executeQuery(sql);
            stmt.close();
        } catch (Exception e) {
            System.out.println("[DB] Error create stok_menu table: " + e.getMessage());
        }
    }

    // Method utk create Pesanan Table
    public static void createPesananTable(Connection conn) {
        try {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS pesanan (" +
                        "  id_pesanan INT PRIMARY KEY AUTO_INCREMENT," +
                        "  no_meja VARCHAR(10) NOT NULL," +
                        "  id_tenant VARCHAR(10) NOT NULL," +
                        "  status_pesanan VARCHAR(50) NOT NULL DEFAULT 'Dibuat'," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                        "  FOREIGN KEY (id_tenant) REFERENCES tenant(id_tenant) ON DELETE CASCADE" +
                        ")";

            stmt.executeQuery(sql);
            stmt.close();
        } catch (Exception e) {
            System.out.println("[DB] Error create item_pesanan table: " + e.getMessage());
        }
    }

    // Method utk create ItemPesanan Table
    public static void createItemPesananTable(Connection conn) {
        try {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS item_pesanan (" +
                        "  id_item INT PRIMARY KEY AUTO_INCREMENT," +
                        "  id_pesanan INT NOT NULL," +
                        "  id_menu VARCHAR(10) NOT NULL," +
                        "  jumlah INT NOT NULL," +
                        "  catatan TEXT," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  FOREIGN KEY (id_pesanan) REFERENCES pesanan(id_pesanan) ON DELETE CASCADE," +
                        "  FOREIGN KEY (id_menu) REFERENCES menu(id_menu) ON DELETE CASCADE" +
                        ")";

            stmt.executeQuery(sql);
            stmt.close();
        } catch (Exception e) {
            System.out.println("[DB] Error create item_pesanan table: " + e.getMessage());
        }
    }

    // Method utk create Pembayaran table
    public static void createPembayaranTable(Connection conn) {
        try {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS pembayaran (" +
                        "  id_pembayaran INT PRIMARY KEY AUTO_INCREMENT," +
                        "  id_pesanan INT NOT NULL UNIQUE," +
                        "  total_bayar INT NOT NULL," +
                        "  status_bayar BOOLEAN DEFAULT FALSE," +
                        "  kode_qris VARCHAR(100)," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  FOREIGN KEY (id_pesanan) REFERENCES pesanan(id_pesanan) ON DELETE CASCADE" +
                        ")";

            stmt.executeQuery(sql);
            stmt.close();
        } catch (Exception e) {
            System.out.println("[DB] Error create pembayaran table: " + e.getMessage());
        }
    }

    // Method utk create Rating table
    public static void createRatingTable(Connection conn) {
        try {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS rating (" +
                        "  id_rating INT PRIMARY KEY AUTO_INCREMENT," +
                        "  id_pesanan INT NOT NULL UNIQUE," +
                        "  nilai INT NOT NULL," +
                        "  ulasan TEXT," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  FOREIGN KEY (id_pesanan) REFERENCES pesanan(id_pesanan) ON DELETE CASCADE" +
                        ")";

            stmt.executeQuery(sql);
            stmt.close();
        } catch (Exception e) {
            System.out.println("[DB] Error create rating table: " + e.getMessage());
        }
    }

    // Method utk insert sample data
    public static void insertSampleData() {
        try {
            Connection conn = KonektorMySQL.getConnection();

            if (conn == null) {
                System.out.println("[DB] Connection error");
                return;
            }

            // Check apakah tenant table sudah ada data
            Statement stmt = conn.createStatement();
            // ResultSet -> object yang hold hasil dari SELECT quary
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM tenant");

            rs.next(); // Move kehasil pertama
            int count = rs.getInt("count");

            rs.close();
            stmt.close();

            if (count > 0) {
                // Data sudah ada, skip insert sample data
                return;
            }

            // Insert sample data
            insertTenants(conn);
            insertMenus(conn);
            insertStoks(conn);

            conn.close();
        } catch (Exception e) {
            System.out.println("[DB] Error insert sample data: " + e.getMessage());
        }
    }

    // Method utk insert sample tenants
    public static void insertTenants(Connection conn) {
        try {
            // PreparedStatement -> prepared SQL query dgn ? parameter
            // ? ->  akan di fill dgn setString/setInt later.
            String sql = "INSERT INTO tenant (id_tenant, nama_tenant, username, password) VALUES (?, ?, ?, ?)";
        
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Insert tenant 1
            pstmt.setString(1, "T001");     // ? pertama    = "T001"
            pstmt.setString(2, "Warung Bude");     // ? kedua      = "Warung Bude"
            pstmt.setString(3, "warung_bude");     // ? ketiga     = "warung_bude"
            pstmt.setString(4, "12345");     // ? keempat    = "12345"
            pstmt.addBatch();     // Tambah ke batch (belum di execute)

            // Insert tenant 2
            pstmt.setString(1, "T002");
            pstmt.setString(2, "Warung Soto");
            pstmt.setString(3, "warung_soto");
            pstmt.setString(4, "12345");
            pstmt.addBatch();

            // Insert tenant 3
            pstmt.setString(1, "T003");
            pstmt.setString(2, "Toko Minuman");
            pstmt.setString(3, "toko_minuman");
            pstmt.setString(4, "12345");
            pstmt.addBatch();

            // executeBatch() -> jalankan semua addBatch sekaligus
            pstmt.executeBatch();
            pstmt.close();

        } catch (SQLException e) {
            System.out.println("[DB] Error insert tenants: " + e.getMessage());
        }
    }

    // Method untuk insert sample menus
    public static void insertMenus(Connection conn) {
        try {
        String sql = "INSERT INTO menu (id_menu, nama_menu, jenis, harga_kupon, id_tenant) VALUES (?, ?, ?, ?, ?)";
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        // Warung Bude menus
        pstmt.setString(1, "M001");
        pstmt.setString(2, "Nasi Kuning");
        pstmt.setString(3, "Makanan");
        pstmt.setInt(4, 15000);
        pstmt.setString(5, "T001");
        pstmt.addBatch();
        
        pstmt.setString(1, "M002");
        pstmt.setString(2, "Ayam Goreng");
        pstmt.setString(3, "Makanan");
        pstmt.setInt(4, 20000);
        pstmt.setString(5, "T001");
        pstmt.addBatch();
        
        pstmt.setString(1, "M003");
        pstmt.setString(2, "Teh Hangat");
        pstmt.setString(3, "Minuman");
        pstmt.setInt(4, 5000);
        pstmt.setString(5, "T001");
        pstmt.addBatch();
        
        // Warung Soto menus
        pstmt.setString(1, "M004");
        pstmt.setString(2, "Soto Ayam");
        pstmt.setString(3, "Makanan");
        pstmt.setInt(4, 18000);
        pstmt.setString(5, "T002");
        pstmt.addBatch();
        
        pstmt.setString(1, "M005");
        pstmt.setString(2, "Perkedel");
        pstmt.setString(3, "Makanan");
        pstmt.setInt(4, 8000);
        pstmt.setString(5, "T002");
        pstmt.addBatch();
        
        pstmt.setString(1, "M006");
        pstmt.setString(2, "Jus Jeruk");
        pstmt.setString(3, "Minuman");
        pstmt.setInt(4, 8000);
        pstmt.setString(5, "T002");
        pstmt.addBatch();
        
        // Toko Minuman menus
        pstmt.setString(1, "M007");
        pstmt.setString(2, "Kopi Hitam");
        pstmt.setString(3, "Minuman");
        pstmt.setInt(4, 6000);
        pstmt.setString(5, "T003");
        pstmt.addBatch();
        
        pstmt.setString(1, "M008");
        pstmt.setString(2, "Cappuccino");
        pstmt.setString(3, "Minuman");
        pstmt.setInt(4, 12000);
        pstmt.setString(5, "T003");
        pstmt.addBatch();
        
        pstmt.setString(1, "M009");
        pstmt.setString(2, "Smoothie Mangga");
        pstmt.setString(3, "Minuman");
        pstmt.setInt(4, 10000);
        pstmt.setString(5, "T003");
        pstmt.addBatch();
        
        pstmt.executeBatch();
        pstmt.close();
        
        } catch (Exception e) {
        System.out.println("Error insert menus: " + e.getMessage());
        }
    }
    
    // Method untuk insert sample stoks
    public static void insertStoks(Connection conn) {
        try {
        String sql = "INSERT INTO stok_menu (id_menu, id_tenant, jumlah_stok) VALUES (?, ?, ?)";
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        // Warung Bude stok
        pstmt.setString(1, "M001");
        pstmt.setString(2, "T001");
        pstmt.setInt(3, 50);
        pstmt.addBatch();
        
        pstmt.setString(1, "M002");
        pstmt.setString(2, "T001");
        pstmt.setInt(3, 30);
        pstmt.addBatch();
        
        pstmt.setString(1, "M003");
        pstmt.setString(2, "T001");
        pstmt.setInt(3, 100);
        pstmt.addBatch();
        
        // Warung Soto stok
        pstmt.setString(1, "M004");
        pstmt.setString(2, "T002");
        pstmt.setInt(3, 25);
        pstmt.addBatch();
        
        pstmt.setString(1, "M005");
        pstmt.setString(2, "T002");
        pstmt.setInt(3, 40);
        pstmt.addBatch();
        
        pstmt.setString(1, "M006");
        pstmt.setString(2, "T002");
        pstmt.setInt(3, 80);
        pstmt.addBatch();
        
        // Toko Minuman stok
        pstmt.setString(1, "M007");
        pstmt.setString(2, "T003");
        pstmt.setInt(3, 60);
        pstmt.addBatch();
        
        pstmt.setString(1, "M008");
        pstmt.setString(2, "T003");
        pstmt.setInt(3, 35);
        pstmt.addBatch();
        
        pstmt.setString(1, "M009");
        pstmt.setString(2, "T003");
        pstmt.setInt(3, 45);
        pstmt.addBatch();
        
        pstmt.executeBatch();
        pstmt.close();
        
        } catch (Exception e) {
        System.out.println("Error insert stoks: " + e.getMessage());
        }
    }
}

// ============================================================================
// Penjelasan JDBC Terms:
// 
// Statement               = untuk execute SQL query
// PreparedStatement       = statement dengan ? parameter (prevent SQL injection)
// ResultSet               = hasil dari SELECT query
// executeUpdate()         = jalankan CREATE/INSERT/UPDATE/DELETE
// executeQuery()          = jalankan SELECT
// setString(index, value) = fill ? parameter dengan string
// setInt(index, value)    = fill ? parameter dengan integer
// addBatch()              = tambah statement ke batch (belum execute)
// executeBatch()          = jalankan semua batch sekaligus
// close()                 = tutup resource (penting!)
// ============================================================================