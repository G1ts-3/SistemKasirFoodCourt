package WG58.pengguna;

import WG58.database.KonektorMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Pengguna — abstract class (Abstraction).
 *
 * Menjadi parent dari Pelanggan dan Tenant (Inheritance).
 * Menampung updateStatusPesananKeDB() yang sebelumnya duplikat
 * di kedua subclass — sekarang cukup satu implementasi,
 * keduanya mewarisinya (DRY via Inheritance).
 */
public abstract class Pengguna {

    protected String idPengguna;
    protected String peran;

    public Pengguna(String idPengguna, String peran) {
        this.idPengguna = idPengguna;
        this.peran      = peran;
    }

    // Getter
    public String getIdPengguna() { return idPengguna; }
    public String getPeran()      { return peran; }

    /** Abstract — wajib di-override oleh subclass (Polymorphism). */
    public abstract void aksesSistem();

    /**
     * Update status pesanan di database.
     * Diwarisi oleh Pelanggan (ubah ke "Selesai") dan Tenant (ubah ke "Diproses" / "Siap Diambil").
     * Exception handling: try-catch pada operasi JDBC.
     */
    public boolean updateStatusPesananKeDB(int idPesanan, String statusBaru) {
        String sql = "UPDATE pesanan SET status_pesanan = ? WHERE id_pesanan = ?";

        try {
            Connection conn = KonektorMySQL.getConnection();
            if (conn == null) return false;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, statusBaru);
            pstmt.setInt(2, idPesanan);

            int affected = pstmt.executeUpdate();
            pstmt.close();
            conn.close();

            return affected > 0;
        } catch (Exception e) {
            System.out.println("[DB] Error update status pesanan: " + e.getMessage());
            return false;
        }
    }
}
