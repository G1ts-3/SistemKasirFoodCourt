package WG58.manajemen;

import WG58.menu.Menu;
import WG58.pengguna.Tenant;
import WG58.database.KonektorMySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Scanner;

public class ManajemenTenant {
    private ArrayList<Tenant> daftarTenant;

    public ManajemenTenant() {
        daftarTenant = ambilSemuaTenantDariDB();
    }

    public void tambahTenant(Tenant tenant) {
        daftarTenant.add(tenant);
    }

    public ArrayList<Tenant> getDaftarTenant() {
        return daftarTenant;
    }

    public void tambahMenuKeTenant(Tenant tenant, Menu menu, int jumlahStok) {
        tenant.tambahMenu(menu, jumlahStok);
    }

    public void tampilkanDaftarTenant() {
        daftarTenant = ambilSemuaTenantDariDB();

        System.out.println("\n===== DAFTAR TENANT =====");
        System.out.println("+------+----------+----------------------+----------------+");
        System.out.println("| No   | ID       | Nama Tenant          | Jumlah Menu    |");
        System.out.println("+------+----------+----------------------+----------------+");

        for (int i = 0; i < daftarTenant.size(); i++) {
            Tenant t = daftarTenant.get(i);

            System.out.printf("| %-4d | %-8s | %-20s | %-14d |\n",
                    i + 1,
                    t.getIdPengguna(),
                    t.getNamaTenant(),
                    t.getDaftarMenu().size());
        }
        System.out.println("+------+----------+----------------------+----------------+");
    }

    public Tenant pilihTenant(Scanner in) {
        daftarTenant = ambilSemuaTenantDariDB();

        if (daftarTenant.size() == 0) {
            System.out.println("Belum ada tenant.");
            return null;
        }

        tampilkanDaftarTenant();

        System.out.print("Pilih tenant: ");
        int pilihT = in.nextInt();
        in.nextLine();

        if (pilihT < 1 || pilihT > daftarTenant.size()) {
            System.out.println("Tenant tidak valid.");
            return null;
        }

        return daftarTenant.get(pilihT - 1);
    }

    public Tenant loginTenant(String username, String password) {
        String sql = "SELECT id_tenant, nama_tenant, username, password FROM tenant WHERE username = ? AND password = ?";

        try {
            Connection conn = KonektorMySQL.getConnection();

            if (conn == null) {
                return null;
            }

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            Tenant tenant = null;

            if (rs.next()) {
                tenant = new Tenant(
                        rs.getString("id_tenant"),
                        "Tenant",
                        rs.getString("nama_tenant"),
                        rs.getString("username"),
                        rs.getString("password"));
            }

            rs.close();
            pstmt.close();
            conn.close();

            return tenant;
        } catch (Exception e) {
            System.out.println("[DB] Error login tenant: " + e.getMessage());
            return null;
        }

    }

    public ArrayList<Tenant> ambilSemuaTenantDariDB() {
        ArrayList<Tenant> hasil = new ArrayList<Tenant>();
        String sql = "SELECT id_tenant, nama_tenant, username, password FROM tenant ORDER BY id_tenant";

        try {
            Connection conn = KonektorMySQL.getConnection();

            if (conn == null) {
                return hasil;
            }

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Tenant tenant = new Tenant(
                        rs.getString("id_tenant"),
                        "Tenant",
                        rs.getString("nama_tenant"),
                        rs.getString("username"),
                        rs.getString("password"));

                hasil.add(tenant);
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("DB] Error ambil tenant: " + e.getMessage());
        }

        return hasil;
    }

}
