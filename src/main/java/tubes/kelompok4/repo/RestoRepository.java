package tubes.kelompok4.repo;

import tubes.kelompok4.config.DatabaseConfig;
import tubes.kelompok4.model.Makanan;
import tubes.kelompok4.model.Menu;
import tubes.kelompok4.model.Minuman;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestoRepository {

    /**
     * [CONSTRUCTOR]: Method khusus yang namanya sama dengan nama class.
     * Akan otomatis dipanggil saat objek RestoRepository dibuat.
     * Di sini kita gunakan untuk memicu inisialisasi tabel.
     */
    public RestoRepository() {
        inisialisasiTabelDanData();
    }

    /**
     * [ENCAPSULATION]: Method private, hanya digunakan secara internal oleh class ini
     * untuk membuat tabel dan data default (Seeding) jika masih kosong.
     */
    private void inisialisasiTabelDanData() {
        String createTableMenu = "CREATE TABLE IF NOT EXISTS daftar_menu ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "nama VARCHAR(100) NOT NULL, "
                + "harga DOUBLE NOT NULL, "
                + "tipe VARCHAR(20) NOT NULL, "
                + "keterangan VARCHAR(50)"
                + ")";

        String createTableTransaksi = "CREATE TABLE IF NOT EXISTS transaksi ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "total_bayar DOUBLE NOT NULL, "
                + "uang_diberikan DOUBLE NOT NULL, "
                + "kembalian DOUBLE NOT NULL, "
                + "waktu TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Eksekusi pembuatan tabel menu
            stmt.execute(createTableMenu);
            System.out.println("[Sistem] Tabel 'daftar_menu' siap digunakan.");

            // 2. Eksekusi pembuatan tabel transaksi
            stmt.execute(createTableTransaksi);
            System.out.println("[Sistem] Tabel 'transaksi' siap digunakan.");

            // 3. Cek apakah tabel menu masih kosong
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM daftar_menu");
            if (rs.next() && rs.getInt("total") == 0) {
                // 4. Jika kosong, masukkan data sample (Seeding Database)
                System.out.println("[Sistem] Tabel menu kosong, menambahkan data sample...");
                
                String insertData = "INSERT INTO daftar_menu (nama, harga, tipe, keterangan) VALUES "
                        + "('Nasi Goreng Gila', 25000, 'MAKANAN', 'Pedas'),"
                        + "('Ayam Bakar', 30000, 'MAKANAN', 'Tidak Pedas'),"
                        + "('Sate Ayam', 28000, 'MAKANAN', 'Tidak Pedas'),"
                        + "('Mie Goreng Pedas', 22000, 'MAKANAN', 'Pedas'),"
                        + "('Rendang Sapi', 35000, 'MAKANAN', 'Pedas'),"
                        + "('Es Teh Manis', 5000, 'MINUMAN', 'Dingin'),"
                        + "('Kopi Hitam', 10000, 'MINUMAN', 'Panas'),"
                        + "('Jus Jeruk', 12000, 'MINUMAN', 'Dingin'),"
                        + "('Teh Hangat', 7000, 'MINUMAN', 'Panas'),"
                        + "('Es Kelapa Muda', 15000, 'MINUMAN', 'Dingin')";
                
                stmt.execute(insertData);
                System.out.println("[Sistem] Data sample berhasil ditambahkan ke database!");
                System.out.println("[Sistem] Total 10 menu (5 Makanan + 5 Minuman) telah tersedia.\n");
            } else {
                System.out.println("[Sistem] Data menu sudah ada, tidak perlu menambahkan data sample.\n");
            }

        } catch (SQLException e) {
            System.out.println("GAGAL menginisialisasi tabel/data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Mengambil semua menu dari database
    public List<Menu> getAllMenu() {
        List<Menu> menuList = new ArrayList<>(); 
        String query = "SELECT * FROM daftar_menu";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nama = rs.getString("nama");
                double harga = rs.getDouble("harga");
                String tipe = rs.getString("tipe");
                String keterangan = rs.getString("keterangan");

                if (tipe.equalsIgnoreCase("MAKANAN")) {
                    boolean pedas = keterangan.equalsIgnoreCase("Pedas");
                    menuList.add(new Makanan(id, nama, harga, pedas));
                } else if (tipe.equalsIgnoreCase("MINUMAN")) {
                    boolean dingin = keterangan.equalsIgnoreCase("Dingin");
                    menuList.add(new Minuman(id, nama, harga, dingin));
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal memuat menu: " + e.getMessage());
        }
        return menuList;
    }

    // Menyimpan transaksi ke database
    public void simpanTransaksi(double totalBayar, double uangDiberikan, double kembalian) {
        String query = "INSERT INTO transaksi (total_bayar, uang_diberikan, kembalian) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             
            pstmt.setDouble(1, totalBayar);
            pstmt.setDouble(2, uangDiberikan);
            pstmt.setDouble(3, kembalian);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Gagal menyimpan transaksi: " + e.getMessage());
        }
    }
}