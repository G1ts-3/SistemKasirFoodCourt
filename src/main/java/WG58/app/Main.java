package WG58.app;

import WG58.manajemen.ManajemenTenant;
import WG58.menu.Makanan;
import WG58.manajemen.ManajemenMenu;
import WG58.menu.Minuman;
import WG58.pengguna.Meja;
import WG58.pengguna.Pelanggan;
import WG58.pengguna.Tenant;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        ManajemenTenant manajemenTenant = new ManajemenTenant();
        ManajemenMenu manajemenMenu = new ManajemenMenu();

        Tenant tenant1 = new Tenant("T01", "Tenant", "Warung Nusantara", "admin1", "123");
        Tenant tenant2 = new Tenant("T02", "Tenant", "Kedai Minuman Segar", "admin2", "123");
        Tenant tenant3 = new Tenant("T03", "Tenant", "Bakso Barokah", "admin3", "123");

        manajemenTenant.tambahMenuKeTenant(tenant1, new Makanan("M01", "Nasi Goreng", 3), 10);
        manajemenTenant.tambahMenuKeTenant(tenant1, new Makanan("M02", "Ayam Geprek", 4), 8);
        manajemenTenant.tambahMenuKeTenant(tenant1, new Makanan("M03", "Mie Ayam", 3), 7);
        manajemenTenant.tambahMenuKeTenant(tenant1, new Minuman("D01", "Teh Hangat", 1), 20);

        manajemenTenant.tambahMenuKeTenant(tenant2, new Minuman("D02", "Jeruk Peras", 2), 15);
        manajemenTenant.tambahMenuKeTenant(tenant2, new Minuman("D03", "Es Kopi", 3), 12);
        manajemenTenant.tambahMenuKeTenant(tenant2, new Minuman("D04", "Es Coklat", 3), 10);

        manajemenTenant.tambahMenuKeTenant(tenant3, new Makanan("B01", "Bakso Urat", 4), 9);
        manajemenTenant.tambahMenuKeTenant(tenant3, new Makanan("B02", "Bakso Telur", 4), 8);
        manajemenTenant.tambahMenuKeTenant(tenant3, new Minuman("B03", "Es Teh", 1), 20);

        manajemenTenant.tambahTenant(tenant1);
        manajemenTenant.tambahTenant(tenant2);
        manajemenTenant.tambahTenant(tenant3);

        Pelanggan pelanggan = new Pelanggan("P01", "Pelanggan");

        int pilihRole = -1;

        while (pilihRole != 0) {
            System.out.println("\n=================================");
            System.out.println("======<< WG58 FOOD COURT >>======");
            System.out.println("=================================");
            System.out.println("1. Pelanggan");
            System.out.println("2. Tenant");
            System.out.println("0. Keluar");
            System.out.println("=================================");
            System.out.print("Pilih role: ");

            pilihRole = input.nextInt();
            input.nextLine();

            if (pilihRole == 1) {
                pelanggan.aksesSistem();

                Tenant tenantDipilih = manajemenTenant.pilihTenant(input);

                if (tenantDipilih == null) {
                    continue;
                }

                System.out.println("Tenant dipilih: " + tenantDipilih.getNamaTenant());

                System.out.print("Masukkan nomor meja: ");
                String nomorMeja = input.nextLine();

                Meja meja = new Meja(nomorMeja);

                int pilihPelanggan = -1;

                while (pilihPelanggan != 0) {
                    System.out.println("\n=================================");
                    System.out.println("========= MENU PELANGGAN ========");
                    System.out.println("Tenant: " + tenantDipilih.getNamaTenant());
                    System.out.println("Meja  : " + meja.getNoMeja());
                    System.out.println("=================================");
                    System.out.println("1. Lihat menu dan buat pesanan");
                    System.out.println("2. Cek status pesanan");
                    System.out.println("3. Ambil pesanan");
                    System.out.println("4. Beri rating");
                    System.out.println("0. Kembali");
                    System.out.print("Pilih: ");

                    pilihPelanggan = input.nextInt();
                    input.nextLine();

                    if (pilihPelanggan == 1) {
                        pelanggan.buatPesanan(input, tenantDipilih, meja.getNoMeja());
                    } else if (pilihPelanggan == 2) {
                        pelanggan.cekPesanan(tenantDipilih.getDaftarPesanan(), meja.getNoMeja());
                    } else if (pilihPelanggan == 3) {
                        pelanggan.ambilPesanan(input, tenantDipilih.getDaftarPesanan(), meja.getNoMeja());
                    } else if (pilihPelanggan == 4) {
                        pelanggan.beriRating(input, tenantDipilih.getDaftarPesanan(), meja.getNoMeja());
                    } else if (pilihPelanggan == 0) {
                        System.out.println("Kembali ke menu utama.");
                    } else {
                        System.out.println("Pilihan tidak valid.");
                    }
                }

            } else if (pilihRole == 2) {
                System.out.println("\n=== LOGIN TENANT ===");

                System.out.print("Username: ");
                String username = input.nextLine();

                System.out.print("Password: ");
                String password = input.nextLine();

                Tenant tenantLogin = manajemenTenant.loginTenant(username, password);

                if (tenantLogin != null) {
                    tenantLogin.aksesSistem();
                    System.out.println("Login berhasil sebagai " + tenantLogin.getNamaTenant() + ".");

                    int pilihTenant = -1;

                    while (pilihTenant != 0) {
                        System.out.println("\n=================================");
                        System.out.println("========= DASHBOARD TENANT ======");
                        System.out.println("Tenant: " + tenantLogin.getNamaTenant());
                        System.out.println("=================================");
                        System.out.println("1. Lihat detail pesanan aktif");
                        System.out.println("2. Update status pesanan");
                        System.out.println("3. Riwayat pesanan");
                        System.out.println("4. Lihat rating");
                        System.out.println("5. Tambah menu baru");
                        System.out.println("6. Ubah stok menu");
                        System.out.println("0. Logout");
                        System.out.print("Pilih: ");

                        pilihTenant = input.nextInt();
                        input.nextLine();

                        if (pilihTenant == 1) {
                            tenantLogin.lihatDetailPesanan(input, tenantLogin.getDaftarPesanan());
                        } else if (pilihTenant == 2) {
                            tenantLogin.ubahStatusPesanan(input, tenantLogin.getDaftarPesanan());
                        } else if (pilihTenant == 3) {
                            tenantLogin.lihatRiwayatPesanan(tenantLogin.getDaftarPesanan());
                        } else if (pilihTenant == 4) {
                            tenantLogin.lihatRating(tenantLogin.getDaftarPesanan());
                        } else if (pilihTenant == 5) {
                            manajemenMenu.tambahMenuBaru(
                                    input,
                                    tenantLogin.getDaftarMenu(),
                                    tenantLogin.getDaftarStokMenu());
                        } else if (pilihTenant == 6) {
                            manajemenMenu.ubahStokMenu(
                                    input,
                                    tenantLogin.getDaftarStokMenu());
                        } else if (pilihTenant == 0) {
                            System.out.println("Logout berhasil.");
                        } else {
                            System.out.println("Pilihan tidak valid.");
                        }
                    }
                } else {
                    System.out.println("Login gagal.");
                }

            } else if (pilihRole == 0) {
                System.out.println("Program selesai.");
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }

        //input.close();
    }
}