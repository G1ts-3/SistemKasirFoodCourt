package WG58.manajemen;

import WG58.menu.Menu;
import WG58.pengguna.Tenant;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ManajemenTenant {
    private ArrayList<Tenant> daftarTenant;
    private Map<String, Tenant> tenantByUsername;
    private Set<String> usernameTerdaftar;

    public ManajemenTenant() {
        daftarTenant = new ArrayList<Tenant>();
        tenantByUsername = new HashMap<String, Tenant>();
        usernameTerdaftar = new HashSet<String>();
    }
    
    public void tambahTenant(Tenant tenant) {
        if (usernameTerdaftar.contains(tenant.getUsername())) {
            System.out.println("Username sudah terdaftar. Tenant tidak dapat ditambahkan.");
            return;
        }

        daftarTenant.add(tenant);
        tenantByUsername.put(tenant.getUsername(), tenant);
        usernameTerdaftar.add(tenant.getUsername());
    }

    public ArrayList<Tenant> getDaftarTenant() {
        return daftarTenant;
    }

    public void tambahMenuKeTenant(Tenant tenant, Menu menu, int jumlahStok) {
        tenant.tambahMenu(menu, jumlahStok);
    }

    public void tampilkanDaftarTenant() {
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
        Tenant tenant = tenantByUsername.get(username);

        if (tenant != null && tenant.login(username, password)) {
            return tenant;
        }

        return null;

    }

}
