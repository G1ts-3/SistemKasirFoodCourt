package WG58.pengguna;

import WG58.database.KonektorMySQL;
import WG58.pembayaran.PembayaranQRIS;
import WG58.pesanan.ItemPesanan;
import WG58.pesanan.Pesanan;
import WG58.pesanan.Rating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Pelanggan — subclass konkret dari Pengguna (Inheritance).
 *
 * Hanya menyimpan logika DB yang dibutuhkan oleh MainGUI:
 *   simpanPesananKeDB  → dipanggil saat pelanggan checkout
 *   simpanRatingKeDB   → dipanggil saat pelanggan memberi rating
 *   updateStatusPesananKeDB → diwarisi dari Pengguna (tidak perlu duplikat lagi)
 *
 * Semua method CLI (buatPesanan, cekPesanan, ambilPesanan, beriRating,
 * tampilkanDaftarMenu) sudah dihapus karena digantikan oleh MainGUI.
 */
public class Pelanggan extends Pengguna {

    public Pelanggan(String idPengguna, String peran) {
        super(idPengguna, peran);
    }

    /** Override aksesSistem() — Polymorphism. */
    @Override
    public void aksesSistem() {
        System.out.println("[Pelanggan] Akses sistem sebagai pelanggan.");
    }

    /**
     * Simpan pesanan baru ke DB dalam satu transaksi.
     * Exception handling: try-catch + rollback jika gagal.
     * @return id_pesanan yang baru dibuat, atau -1 jika gagal.
     */
    public int simpanPesananKeDB(String idTenant, Pesanan pesanan) {
        int idPesanan = -1;

        String sqlPesanan    = "INSERT INTO pesanan (no_meja, id_tenant, status_pesanan) VALUES (?, ?, ?)";
        String sqlItem       = "INSERT INTO item_pesanan (id_pesanan, id_menu, jumlah, catatan) VALUES (?, ?, ?, ?)";
        String sqlPembayaran = "INSERT INTO pembayaran (id_pesanan, total_bayar, status_bayar, kode_qris) VALUES (?, ?, ?, ?)";
        String sqlStok       = "UPDATE stok_menu SET jumlah_stok = jumlah_stok - ? WHERE id_menu = ? AND jumlah_stok >= ?";

        Connection conn = null;
        try {
            conn = KonektorMySQL.getConnection();
            if (conn == null) return -1;

            conn.setAutoCommit(false);

            // 1. Insert header pesanan, ambil generated key
            PreparedStatement psPesanan = conn.prepareStatement(sqlPesanan, Statement.RETURN_GENERATED_KEYS);
            psPesanan.setString(1, pesanan.getNoMeja());
            psPesanan.setString(2, idTenant);
            psPesanan.setString(3, pesanan.getStatusPesanan());
            psPesanan.executeUpdate();

            ResultSet keys = psPesanan.getGeneratedKeys();
            if (keys.next()) idPesanan = keys.getInt(1);
            else { conn.rollback(); return -1; }

            // 2. Insert setiap item + kurangi stok (batch)
            PreparedStatement psItem = conn.prepareStatement(sqlItem);
            PreparedStatement psStok = conn.prepareStatement(sqlStok);

            for (ItemPesanan item : pesanan.getDaftarItem()) {
                psItem.setInt(1, idPesanan);
                psItem.setString(2, item.getMenu().getIdProduk());
                psItem.setInt(3, item.getJumlah());
                psItem.setString(4, item.getCatatan());
                psItem.addBatch();

                psStok.setInt(1, item.getJumlah());
                psStok.setString(2, item.getMenu().getIdProduk());
                psStok.setInt(3, item.getJumlah());
                psStok.addBatch();
            }
            psItem.executeBatch();
            psStok.executeBatch();

            // 3. Insert pembayaran QRIS
            if (pesanan.getPembayaran() instanceof PembayaranQRIS) {
                PembayaranQRIS p = (PembayaranQRIS) pesanan.getPembayaran();
                PreparedStatement psPay = conn.prepareStatement(sqlPembayaran);
                psPay.setInt(1, idPesanan);
                psPay.setInt(2, p.getTotalBayar());
                psPay.setBoolean(3, p.getStatusBayar());
                psPay.setString(4, p.getKodeQRIS());
                psPay.executeUpdate();
                psPay.close();
            }

            conn.commit();
            keys.close();
            psPesanan.close();
            psItem.close();
            psStok.close();
            conn.close();

            return idPesanan;

        } catch (Exception e) {
            System.out.println("[DB] Error simpan pesanan: " + e.getMessage());
            try { if (conn != null) { conn.rollback(); conn.close(); } }
            catch (Exception re) { System.out.println("[DB] Rollback error: " + re.getMessage()); }
            return -1;
        }
    }

    /**
     * Simpan rating pelanggan ke DB.
     * Exception handling: try-catch pada operasi JDBC.
     */
    public boolean simpanRatingKeDB(int idPesanan, Rating rating) {
        String sql = "INSERT INTO rating (id_pesanan, nilai, ulasan) VALUES (?, ?, ?)";

        try {
            Connection conn = KonektorMySQL.getConnection();
            if (conn == null) return false;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idPesanan);
            pstmt.setInt(2, rating.getNilai());
            pstmt.setString(3, rating.getUlasan());

            int affected = pstmt.executeUpdate();
            pstmt.close();
            conn.close();

            return affected > 0;
        } catch (Exception e) {
            System.out.println("[DB] Error simpan rating: " + e.getMessage());
            return false;
        }
    }
}
