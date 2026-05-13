/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package WG58.pengguna;

/**
 *
 * @author hi
 */
import WG58.pesanan.Pesanan;
import WG58.database.KonektorMySQL;
import java.sql.*;
public class Meja {
    private String noMeja;
    private String qrCodeMeja;

    public Meja() {

    }
    
    public void ambilNoMeja(){
        int nomorTersedia = 1; // Default mulai dari 1
        
        // Query SQL untuk mencari angka terkecil yang belum ada di urutan (Find the gap)
        String sql = "SELECT (t1.no_meja_numeric + 1) AS next_available " +
                     "FROM meja t1 " +
                     "LEFT JOIN meja t2 ON t1.no_meja_numeric + 1 = t2.no_meja_numeric " +
                     "WHERE t2.no_meja_numeric IS NULL " +
                     "ORDER BY t1.no_meja_numeric LIMIT 1";
        try (Connection conn = KonektorMySQL.connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                // Jika ada meja yang terdaftar, ambil nomor berikutnya yang kosong
                nomorTersedia = rs.getInt("next_available");
            } else {
                // Jika database masih benar-benar kosong, cek apakah ada data sama sekali
                ResultSet rsEmpty = stmt.executeQuery("SELECT COUNT(*) FROM meja");
                rsEmpty.next();
                if (rsEmpty.getInt(1) == 0) nomorTersedia = 1;
            }

            // Set sesuai template yang kamu mau: WG-M- plus nomornya
            this.noMeja = "WG-M-" + nomorTersedia;
            this.qrCodeMeja = "QR-" + this.noMeja;

            System.out.println("Sistem Otomatis: Meja dialokasikan ke " + this.noMeja);
            
            // Simpan meja baru ini ke DB agar nomornya 'terpakai'
            simpanMejaKeDB(nomorTersedia);

        } catch (SQLException e) {
            System.out.println("Error saat auto-assign meja: " + e.getMessage());
        }
    }
    private void simpanMejaKeDB(int numericId) {
        String sql = "INSERT INTO meja (no_meja_numeric, no_meja_string) VALUES (?, ?)";
        try (Connection conn = KonektorMySQL.connect()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, numericId);
            ps.setString(2, this.noMeja);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getNoMeja() {
        return noMeja;
    }

    public String getQrCodeMeja() {
        return qrCodeMeja;
    }
}
