package WG58.pengguna;

import WG58.database.KonektorMySQL;
import WG58.menu.Makanan;
import WG58.menu.Menu;
import WG58.menu.Minuman;
import WG58.menu.StokMenu;
import WG58.pesanan.Pesanan;
import WG58.pesanan.Rating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Tenant — subclass konkret dari Pengguna (Inheritance).
 *
 * Perubahan dari versi sebelumnya:
 *  - Semua method CLI dihapus (lihatDetailPesanan, ubahStatusPesanan,
 *    lihatRiwayatPesanan, lihatRating, prosesLogin, lihatPesananMasuk).
 *  - updateStatusPesananKeDB dipindah ke Pengguna — tidak ada lagi duplikasi.
 *    Tenant tetap bisa memanggil method ini via inheritance.
 *
 * Method yang tersisa: akses sistem, login, manajemen menu/pesanan in-memory,
 * dan seluruh operasi fetch dari database.
 */
public class Tenant extends Pengguna {

    // Encapsulation: semua field private
    private final String username;
    private final String password;
    private final String namaTenant;
    private ArrayList<Menu>     daftarMenu;
    private ArrayList<StokMenu> daftarStokMenu;
    private ArrayList<Pesanan>  daftarPesanan;

    public Tenant(String idPengguna, String peran, String namaTenant,
                  String username, String password) {
        super(idPengguna, peran);
        this.namaTenant     = namaTenant;
        this.username       = username;
        this.password       = password;
        this.daftarMenu     = new ArrayList<>();
        this.daftarStokMenu = new ArrayList<>();
        this.daftarPesanan  = new ArrayList<>();
    }

    /** Override — Polymorphism. */
    @Override
    public void aksesSistem() {
        System.out.println("[Tenant] Akses sistem sebagai: " + namaTenant);
    }

    /** Validasi kredensial login. */
    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    // ── Manajemen in-memory ───────────────────────────────────────────────

    public void tambahMenu(Menu menu, int jumlahStok) {
        daftarMenu.add(menu);
        daftarStokMenu.add(new StokMenu(menu, jumlahStok));
    }

    public void tambahPesanan(Pesanan pesanan) {
        daftarPesanan.add(pesanan);
    }

    // ── Getter (selalu ambil fresh dari DB) ───────────────────────────────

    public String getNamaTenant() { return namaTenant; }

    public ArrayList<Menu> getDaftarMenu() {
        daftarMenu = ambilMenuDariDB();
        return daftarMenu;
    }

    public ArrayList<StokMenu> getDaftarStokMenu() {
        daftarStokMenu = ambilStokMenuDariDB();
        return daftarStokMenu;
    }

    public ArrayList<Pesanan> getDaftarPesanan() {
        daftarPesanan = ambilPesananDariDB();
        return daftarPesanan;
    }

    // ── Operasi Database ──────────────────────────────────────────────────

    public ArrayList<Menu> ambilMenuDariDB() {
        ArrayList<Menu> hasil = new ArrayList<>();
        String sql = "SELECT id_menu, nama_menu, jenis, harga_kupon " +
                     "FROM menu WHERE id_tenant = ? ORDER BY id_menu";
        try {
            Connection conn = KonektorMySQL.getConnection();
            if (conn == null) return hasil;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, getIdPengguna());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                hasil.add(buatMenuDariRS(rs));
            }
            rs.close(); pstmt.close(); conn.close();
        } catch (Exception e) {
            System.out.println("[DB] Error ambil menu: " + e.getMessage());
        }
        return hasil;
    }

    public ArrayList<StokMenu> ambilStokMenuDariDB() {
        ArrayList<StokMenu> hasil = new ArrayList<>();
        String sql = "SELECT m.id_menu, m.nama_menu, m.jenis, m.harga_kupon, s.jumlah_stok " +
                     "FROM menu m JOIN stok_menu s ON m.id_menu = s.id_menu " +
                     "WHERE m.id_tenant = ? ORDER BY m.id_menu";
        try {
            Connection conn = KonektorMySQL.getConnection();
            if (conn == null) return hasil;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, getIdPengguna());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                hasil.add(new StokMenu(buatMenuDariRS(rs), rs.getInt("jumlah_stok")));
            }
            rs.close(); pstmt.close(); conn.close();
        } catch (Exception e) {
            System.out.println("[DB] Error ambil stok: " + e.getMessage());
        }
        return hasil;
    }

    public ArrayList<Pesanan> ambilPesananDariDB() {
        ArrayList<Pesanan> hasil = new ArrayList<>();
        String sql = "SELECT id_pesanan, no_meja, id_tenant, status_pesanan " +
                     "FROM pesanan WHERE id_tenant = ? ORDER BY id_pesanan";
        try {
            Connection conn = KonektorMySQL.getConnection();
            if (conn == null) return hasil;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, getIdPengguna());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Pesanan p = new Pesanan(
                    rs.getInt("id_pesanan"),
                    rs.getString("no_meja"),
                    rs.getString("id_tenant"),
                    getNamaTenant(),
                    rs.getString("status_pesanan"));
                isiItemDariDB(conn, p);
                isiRatingDariDB(conn, p);
                hasil.add(p);
            }
            rs.close(); pstmt.close(); conn.close();
        } catch (Exception e) {
            System.out.println("[DB] Error ambil pesanan: " + e.getMessage());
        }
        return hasil;
    }

    // ── Private helpers ───────────────────────────────────────────────────

    /**
     * Factory method kecil — menghilangkan duplikasi kode pembuatan
     * objek Menu di ambilMenuDariDB() dan ambilStokMenuDariDB().
     * Contoh sederhana encapsulation logika konstruksi.
     */
    private Menu buatMenuDariRS(ResultSet rs) throws Exception {
        String id    = rs.getString("id_menu");
        String nama  = rs.getString("nama_menu");
        int    harga = rs.getInt("harga_kupon");

        // Polymorphism: hasilkan Makanan atau Minuman tergantung jenis
        return "Makanan".equalsIgnoreCase(rs.getString("jenis"))
            ? new Makanan(id, nama, harga)
            : new Minuman(id, nama, harga);
    }

    private void isiItemDariDB(Connection conn, Pesanan pesanan) throws Exception {
        String sql = "SELECT m.id_menu, m.nama_menu, m.jenis, m.harga_kupon, i.jumlah, i.catatan " +
                     "FROM item_pesanan i JOIN menu m ON i.id_menu = m.id_menu " +
                     "WHERE i.id_pesanan = ? ORDER BY i.id_item";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, pesanan.getIdPesanan());
        ResultSet rs = pstmt.executeQuery();

        while (rs.next())
            pesanan.tambahItem(buatMenuDariRS(rs), rs.getInt("jumlah"), rs.getString("catatan"));

        rs.close(); pstmt.close();
    }

    private void isiRatingDariDB(Connection conn, Pesanan pesanan) throws Exception {
        String sql = "SELECT nilai, ulasan FROM rating WHERE id_pesanan = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, pesanan.getIdPesanan());
        ResultSet rs = pstmt.executeQuery();

        if (rs.next())
            pesanan.setRating(new Rating(rs.getInt("nilai"), rs.getString("ulasan")));

        rs.close(); pstmt.close();
    }
}
