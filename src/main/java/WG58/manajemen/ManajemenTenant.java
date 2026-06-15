package WG58.manajemen;

import WG58.database.KonektorMySQL;
import WG58.pengguna.Tenant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * ManajemenTenant — service class untuk manajemen data tenant.
 *
 * Perubahan dari versi sebelumnya:
 *  - tambahTenant()         dihapus — tidak pernah dipanggil
 *  - tambahMenuKeTenant()   dihapus — tidak pernah dipanggil
 *  - tampilkanDaftarTenant() dihapus — CLI, digantikan JTable di GUI
 *  - pilihTenant(Scanner)   dihapus — CLI, digantikan row selection di GUI
 *
 * Tersisa 3 method yang aktif dipakai oleh MainGUI.
 */
public class ManajemenTenant {

    private ArrayList<Tenant> daftarTenant;

    public ManajemenTenant() {
        daftarTenant = ambilSemuaTenantDariDB();
    }

    /** Kembalikan daftar tenant terbaru dari DB. Dipakai GUI untuk isi tabel. */
    public ArrayList<Tenant> getDaftarTenant() {
        daftarTenant = ambilSemuaTenantDariDB();
        return daftarTenant;
    }

    /**
     * Login tenant dengan verifikasi langsung ke DB.
     * Exception handling: try-catch pada operasi JDBC.
     * @return Tenant jika kredensial cocok, null jika tidak.
     */
    public Tenant loginTenant(String username, String password) {
        String sql = "SELECT id_tenant, nama_tenant, username, password " +
                     "FROM tenant WHERE username = ? AND password = ?";
        try {
            Connection conn = KonektorMySQL.getConnection();
            if (conn == null) return null;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            Tenant tenant = null;

            if (rs.next()) {
                tenant = new Tenant(
                    rs.getString("id_tenant"), "Tenant",
                    rs.getString("nama_tenant"),
                    rs.getString("username"),
                    rs.getString("password"));
            }

            rs.close(); pstmt.close(); conn.close();
            return tenant;

        } catch (Exception e) {
            System.out.println("[DB] Error login tenant: " + e.getMessage());
            return null;
        }
    }

    /** Ambil semua tenant dari DB — Collections (ArrayList). */
    public ArrayList<Tenant> ambilSemuaTenantDariDB() {
        ArrayList<Tenant> hasil = new ArrayList<>();
        String sql = "SELECT id_tenant, nama_tenant, username, password FROM tenant ORDER BY id_tenant";

        try {
            Connection conn = KonektorMySQL.getConnection();
            if (conn == null) return hasil;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                hasil.add(new Tenant(
                    rs.getString("id_tenant"), "Tenant",
                    rs.getString("nama_tenant"),
                    rs.getString("username"),
                    rs.getString("password")));
            }

            rs.close(); pstmt.close(); conn.close();
        } catch (Exception e) {
            System.out.println("[DB] Error ambil tenant: " + e.getMessage());
        }

        return hasil;
    }
}
