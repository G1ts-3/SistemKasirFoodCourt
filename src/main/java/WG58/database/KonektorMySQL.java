package WG58.database;

// Import lib JDBC untuk koneksi dan eksekusi query ke MySQL.
import java.sql.*;

public class KonektorMySQL {
    private String host, namaDB;
    private int port;
    private Connection connection;

    // 
    // Inisialisasi konektor database.
    // Cara kerja: menyimpan konfigurasi koneksi (host, port, namaDB) ke field.
    // Penggunaan: buat instance baru lalu panggil `hubungkan()` untuk membuka koneksi.
    // Contoh: `KonektorMySQL k = new KonektorMySQL("localhost",3306,"db"); k.hubungkan();`
    // 
    public KonektorMySQL(String host, int port, String namaDB) {
        this.host = host;
        this.port = port;
        this.namaDB = namaDB;
        this.connection = null;
    }

    // 
    // Buka koneksi ke MySQL via JDBC.
    // Cara kerja: memuat driver JDBC, membangun URL koneksi, lalu memanggil
    // `DriverManager.getConnection` untuk mengatur field `connection`.
    // Penggunaan: panggil setelah membuat objek konektor; mengembalikan true jika sukses.
    // 
    public boolean hubungkan() {
        try {
            // Ini untuk memanggil Driver JDBC MySQL; pastikan Connector/J sudah ada di classpath.
            Class.forName(" ");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + namaDB
                       + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
            connection = DriverManager.getConnection(url, "root", "");
            System.out.println("[Database] Koneksi berhasil ke " + namaDB + " @ " + host);
            return true;
        } catch (Exception e) {
            System.err.println("[Database] Gagal koneksi " + e.getMessage());
            return false;
        }
    }

    // 
    // Tutup koneksi database jika sedang terbuka.
    // Cara kerja: memeriksa `connection` dan menutupnya jika belum tertutup.
    // Penggunaan: panggil saat aplikasi selesai memakai database untuk melepaskan sumber daya.
    // 
    public void putuskan() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[Database] Koneksi ditutup.");
            }
        } catch (Exception e) {
            System.err.println("[Database] Error menutup koneksi: " + e.getMessage());
        }
    }

    // 
    // Cek apakah koneksi masih aktif.
    // Cara kerja: memeriksa apakah `connection` bukan null dan belum ditutup.
    // Penggunaan: panggil sebelum menjalankan query untuk memastikan koneksi tersedia.
    // 
    public boolean isKoneksiAktif() {
        try {
            return connection != null && !connection.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    // 
    // Eksekusi query SELECT dan kembalikan `ResultSet`.
    // Cara kerja: membuat `Statement` lalu memanggil `executeQuery(sql)`.
    // Penggunaan: panggil dengan SQL SELECT; ingat untuk menutup `ResultSet` dan `Statement` oleh pemanggil.
    // 
    public ResultSet eksekusiQuery(String sql) {
        try {
            if (!isKoneksiAktif()) {
                System.err.println("[Database] Tidak ada koneksi yang aktif!");
                return null;
            }
            Statement statement = connection.createStatement();
            System.out.println("[Database] Eksekusi query: " + sql);
            return statement.executeQuery(sql);
        } catch (Exception e) {
            System.err.println("[Database] Eksekusi query gagal: " + e.getMessage());
            return null;
        }
    }

    // 
    // Eksekusi perintah INSERT/UPDATE/DELETE.
    // Cara kerja: membuat `Statement` lalu memanggil `executeUpdate(sql)`.
    // Penggunaan: panggil dengan SQL non-SELECT; mengembalikan true jika eksekusi sukses.
    // 
    public boolean simpanKeDB(String sql) {
        try {
            if (!isKoneksiAktif()) {
                System.err.println("[Database] Tidak ada koneksi yang aktif!");
                return false;
            }
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            System.out.println("[Database] Query berhasil: " + sql);
            return true;
        } catch (Exception e) {
            System.err.println("[Database] Gagal simpan ke DB: " + e.getMessage());
            return false;
        }
    }

    // Getters

    // Dapatkan host database yang dikonfigurasi.
    public String getHost() {
        return host;
    }
    // Dapatkan nama database yang dikonfigurasi.
    public String getNamaDB() {
        return namaDB;
    }
    // Dapatkan port database yang dikonfigurasi.
    public int getPort() {
        return port;
    }
    // Dapatkan objek `Connection` aktif (atau null jika belum terhubung).
    public Connection getConnection() {
        return connection;
    }
}
