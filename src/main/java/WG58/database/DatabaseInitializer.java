package WG58.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * DatabaseInitializer — inisialisasi schema dan data awal.
 *
 * Penyederhanaan dari versi sebelumnya (13 method -> 3 method):
 *  - 7 method createXxxTable(Connection) digabung jadi 1 loop di createAllTables()
 *  - 3 method insertXxx(Connection) digabung jadi 1 blok di insertSampleData()
 *
 * Method tersisa: initialize() · createAllTables() · insertSampleData()
 */
public class DatabaseInitializer {

    public static void initialize() {
        createDatabase();
        createAllTables();
        insertSampleData();
    }

    public static void createDatabase() {
        String sql = "CREATE DATABASE IF NOT EXISTS wg58_foodcourt " +
                     "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
        try {
            Connection conn = KonektorMySQL.getConnectionWithoutDB();
            if (conn == null) return;
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close(); conn.close();
            System.out.println("[DB] Database siap.");
        } catch (Exception e) {
            System.out.println("[DB] Error buat database: " + e.getMessage());
        }
    }

    /**
     * Buat semua 7 tabel dengan loop atas array DDL.
     * Sebelumnya 7 method terpisah — sekarang cukup 1.
     * Exception handling: try-catch.
     */
    public static void createAllTables() {
        String[] ddl = {
            "CREATE TABLE IF NOT EXISTS tenant (" +
            "  id_tenant   VARCHAR(10)  NOT NULL PRIMARY KEY," +
            "  nama_tenant VARCHAR(100) NOT NULL," +
            "  username    VARCHAR(50)  NOT NULL UNIQUE," +
            "  password    VARCHAR(100) NOT NULL" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS menu (" +
            "  id_menu     VARCHAR(10)  NOT NULL PRIMARY KEY," +
            "  nama_menu   VARCHAR(100) NOT NULL," +
            "  jenis       VARCHAR(20)  NOT NULL," +
            "  harga_kupon INT          NOT NULL," +
            "  id_tenant   VARCHAR(10)  NOT NULL," +
            "  FOREIGN KEY (id_tenant) REFERENCES tenant(id_tenant) ON DELETE CASCADE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS stok_menu (" +
            "  id_stok     INT         NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "  id_menu     VARCHAR(10) NOT NULL UNIQUE," +
            "  id_tenant   VARCHAR(10) NOT NULL," +
            "  jumlah_stok INT         NOT NULL DEFAULT 0," +
            "  FOREIGN KEY (id_menu)   REFERENCES menu(id_menu)     ON DELETE CASCADE," +
            "  FOREIGN KEY (id_tenant) REFERENCES tenant(id_tenant) ON DELETE CASCADE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS pesanan (" +
            "  id_pesanan     INT         NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "  no_meja        VARCHAR(10) NOT NULL," +
            "  id_tenant      VARCHAR(10) NOT NULL," +
            "  status_pesanan VARCHAR(20) NOT NULL DEFAULT 'Menunggu'," +
            "  FOREIGN KEY (id_tenant) REFERENCES tenant(id_tenant) ON DELETE CASCADE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS item_pesanan (" +
            "  id_item    INT         NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "  id_pesanan INT         NOT NULL," +
            "  id_menu    VARCHAR(10) NOT NULL," +
            "  jumlah     INT         NOT NULL DEFAULT 1," +
            "  catatan    VARCHAR(200)," +
            "  FOREIGN KEY (id_pesanan) REFERENCES pesanan(id_pesanan) ON DELETE CASCADE," +
            "  FOREIGN KEY (id_menu)    REFERENCES menu(id_menu)       ON DELETE CASCADE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS pembayaran (" +
            "  id_pembayaran INT         NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "  id_pesanan    INT         NOT NULL UNIQUE," +
            "  total_bayar   INT         NOT NULL," +
            "  status_bayar  BOOLEAN     NOT NULL DEFAULT FALSE," +
            "  kode_qris     VARCHAR(50)," +
            "  FOREIGN KEY (id_pesanan) REFERENCES pesanan(id_pesanan) ON DELETE CASCADE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS rating (" +
            "  id_rating  INT         NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "  id_pesanan INT         NOT NULL UNIQUE," +
            "  nilai      INT         NOT NULL," +
            "  ulasan     VARCHAR(255)," +
            "  FOREIGN KEY (id_pesanan) REFERENCES pesanan(id_pesanan) ON DELETE CASCADE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
        };

        try {
            Connection conn = KonektorMySQL.getConnection();
            if (conn == null) return;
            Statement stmt = conn.createStatement();
            for (String sql : ddl) stmt.executeUpdate(sql);
            stmt.close(); conn.close();
            System.out.println("[DB] Semua tabel siap.");
        } catch (Exception e) {
            System.out.println("[DB] Error buat tabel: " + e.getMessage());
        }
    }

    /**
     * Insert data awal hanya jika tabel masih kosong.
     * Sebelumnya 3 method terpisah (insertTenants, insertMenus, insertStoks)
     * — sekarang digabung jadi 1 method, exception handling: try-catch.
     */
    public static void insertSampleData() {
        try {
            Connection conn = KonektorMySQL.getConnection();
            if (conn == null) return;

            // Skip jika data sudah ada
            Statement check = conn.createStatement();
            ResultSet rs = check.executeQuery("SELECT COUNT(*) FROM tenant");
            rs.next();
            boolean sudahAda = rs.getInt(1) > 0;
            rs.close(); check.close();
            if (sudahAda) { conn.close(); return; }

            // Tenant
            String sqlT = "INSERT INTO tenant (id_tenant,nama_tenant,username,password) VALUES (?,?,?,?)";
            String[][] tnt = {
                {"T01","Warung Nasi Gudeg Bu Sari","tenant1","pass1"},
                {"T02","Mie Ayam Bakso Pak Budi",  "tenant2","pass2"},
                {"T03","Es Teh & Jus Segar",        "tenant3","pass3"},
                {"T04","Sate & Grill Corner",        "tenant4","pass4"},
                {"T05","Bakso Malang Pak Darto",     "tenant5","pass5"}
            };
            PreparedStatement psT = conn.prepareStatement(sqlT);
            for (String[] r : tnt) {
                psT.setString(1,r[0]); psT.setString(2,r[1]);
                psT.setString(3,r[2]); psT.setString(4,r[3]);
                psT.addBatch();
            }
            psT.executeBatch(); psT.close();

            // Menu  {id, nama, jenis, kupon, tenant}
            String sqlM = "INSERT INTO menu (id_menu,nama_menu,jenis,harga_kupon,id_tenant) VALUES (?,?,?,?,?)";
            Object[][] mnu = {
                {"M01","Nasi Gudeg Ayam",      "Makanan",3,"T01"},{"M02","Nasi Gudeg Telur",    "Makanan",2,"T01"},
                {"M03","Nasi Gudeg Tofu",      "Makanan",2,"T01"},{"M04","Nasi Kucing",          "Makanan",1,"T01"},
                {"M05","Mie Ayam Biasa",       "Makanan",2,"T02"},{"M06","Mie Ayam Bakso",       "Makanan",3,"T02"},
                {"M07","Mie Ayam Spesial",     "Makanan",4,"T02"},{"M08","Bakso Biasa",          "Makanan",2,"T02"},
                {"M09","Es Teh Manis",         "Minuman",1,"T03"},{"M10","Es Jeruk Segar",       "Minuman",2,"T03"},
                {"M11","Jus Alpukat",          "Minuman",2,"T03"},{"M12","Es Campur",            "Minuman",2,"T03"},
                {"M13","Sate Ayam (10 tusuk)", "Makanan",3,"T04"},{"M14","Sate Kambing (10 tusuk)","Makanan",4,"T04"},
                {"M15","Ayam Bakar",           "Makanan",4,"T04"},{"M16","Ikan Bakar",           "Makanan",4,"T04"},
                {"M17","Bakso Malang Biasa",   "Makanan",2,"T05"},{"M18","Bakso Malang Spesial", "Makanan",3,"T05"},
                {"M19","Mie Goreng Bakso",     "Makanan",3,"T05"},{"M20","Tahu Bakso",           "Makanan",2,"T05"}
            };
            PreparedStatement psM = conn.prepareStatement(sqlM);
            for (Object[] m : mnu) {
                psM.setString(1,(String)m[0]); psM.setString(2,(String)m[1]);
                psM.setString(3,(String)m[2]); psM.setInt(4,(int)m[3]);
                psM.setString(5,(String)m[4]); psM.addBatch();
            }
            psM.executeBatch(); psM.close();

            // Stok {id_menu, id_tenant, jumlah}
            String sqlS = "INSERT INTO stok_menu (id_menu,id_tenant,jumlah_stok) VALUES (?,?,?)";
            Object[][] stk = {
                {"M01","T01",20},{"M02","T01",25},{"M03","T01",20},{"M04","T01",30},
                {"M05","T02",30},{"M06","T02",25},{"M07","T02",20},{"M08","T02",35},
                {"M09","T03",50},{"M10","T03",40},{"M11","T03",30},{"M12","T03",35},
                {"M13","T04",20},{"M14","T04",15},{"M15","T04",20},{"M16","T04",20},
                {"M17","T05",30},{"M18","T05",25},{"M19","T05",25},{"M20","T05",35}
            };
            PreparedStatement psS = conn.prepareStatement(sqlS);
            for (Object[] s : stk) {
                psS.setString(1,(String)s[0]); psS.setString(2,(String)s[1]);
                psS.setInt(3,(int)s[2]); psS.addBatch();
            }
            psS.executeBatch(); psS.close();

            conn.close();
            System.out.println("[DB] Data awal berhasil dimasukkan.");

        } catch (Exception e) {
            System.out.println("[DB] Error insert data awal: " + e.getMessage());
        }
    }
}
