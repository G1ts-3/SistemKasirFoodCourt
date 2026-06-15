package WG58.manajemen;

import WG58.database.KonektorMySQL;
import WG58.menu.Menu;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * ManajemenMenu — service class untuk operasi menu ke database.
 *
 * Sebelumnya 5 method; 3 CLI (tampilkanMenuDenganStok, tambahMenuBaru,
 * ubahStokMenu) dihapus karena digantikan oleh tampilan tabel di MainGUI.
 * Tersisa 2 method murni DB.
 */
public class ManajemenMenu {

    /**
     * Tambah menu baru + stok awal ke DB dalam satu transaksi.
     * Exception handling: try-catch + autoCommit false (rollback otomatis jika gagal).
     */
    public boolean tambahMenuKeDB(String idTenant, Menu menu, int stokAwal) {
        String sqlMenu = "INSERT INTO menu (id_menu, nama_menu, jenis, harga_kupon, id_tenant) VALUES (?, ?, ?, ?, ?)";
        String sqlStok = "INSERT INTO stok_menu (id_menu, id_tenant, jumlah_stok) VALUES (?, ?, ?)";

        try {
            Connection conn = KonektorMySQL.getConnection();
            if (conn == null) return false;

            conn.setAutoCommit(false);

            PreparedStatement psMenu = conn.prepareStatement(sqlMenu);
            psMenu.setString(1, menu.getIdProduk());
            psMenu.setString(2, menu.getNamaMenu());
            psMenu.setString(3, menu.getJenis());
            psMenu.setInt(4, menu.getHargaKupon());
            psMenu.setString(5, idTenant);
            psMenu.executeUpdate();

            PreparedStatement psStok = conn.prepareStatement(sqlStok);
            psStok.setString(1, menu.getIdProduk());
            psStok.setString(2, idTenant);
            psStok.setInt(3, stokAwal);
            psStok.executeUpdate();

            conn.commit();
            psMenu.close(); psStok.close(); conn.close();
            return true;

        } catch (Exception e) {
            System.out.println("[DB] Error tambah menu: " + e.getMessage());
            return false;
        }
    }

    /** Update jumlah stok menu di DB. Exception handling: try-catch. */
    public boolean updateStokKeDB(String idMenu, int stokBaru) {
        String sql = "UPDATE stok_menu SET jumlah_stok = ? WHERE id_menu = ?";

        try {
            Connection conn = KonektorMySQL.getConnection();
            if (conn == null) return false;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, stokBaru);
            pstmt.setString(2, idMenu);

            int affected = pstmt.executeUpdate();
            pstmt.close(); conn.close();
            return affected > 0;

        } catch (Exception e) {
            System.out.println("[DB] Error update stok: " + e.getMessage());
            return false;
        }
    }
}
