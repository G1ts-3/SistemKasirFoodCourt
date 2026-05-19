package WG58.app;

import WG58.menu.Menu;
import WG58.menu.Makanan;
import WG58.menu.Minuman;
import WG58.pesanan.Pesanan;
import WG58.pengguna.Pelanggan;
import WG58.pengguna.Tenant;
import WG58.pengguna.Meja;
import WG58.menu.StokMenu;
import WG58.menu.ManajemenMenu;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        ArrayList<Menu> daftarMenu = new ArrayList<Menu>();
        ArrayList<StokMenu> daftarStokMenu = new ArrayList<StokMenu>();
        ManajemenMenu manajemenMenu = new ManajemenMenu();
        ArrayList<Pesanan> daftarPesanan = new ArrayList<Pesanan>();

        Menu m1 = new Makanan("M01", "Nasi Goreng", 3);
        Menu m2 = new Makanan("M02", "Ayam Geprek", 4);
        Menu m3 = new Makanan("M03", "Mie Ayam", 3);
        Menu d1 = new Minuman("D01", "Teh", 1);
        Menu d2 = new Minuman("D02", "Jeruk Peras", 2);

        daftarMenu.add(m1);
        daftarMenu.add(m2);
        daftarMenu.add(m3);
        daftarMenu.add(d1);
        daftarMenu.add(d2);

        daftarStokMenu.add(new StokMenu(m1, 10));
        daftarStokMenu.add(new StokMenu(m2, 8));
        daftarStokMenu.add(new StokMenu(m3, 7));
        daftarStokMenu.add(new StokMenu(d1, 20));
        daftarStokMenu.add(new StokMenu(d2, 15));

        Pelanggan pelanggan = new Pelanggan("P01", "Pelanggan");
        Tenant tenant = new Tenant("T01", "Tenant", "admin", "123");

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

                System.out.print("Masukkan nomor meja: ");
                String nomorMeja = input.nextLine();

                Meja meja = new Meja(nomorMeja);

                int pilihPelanggan = -1;

                while (pilihPelanggan != 0) {
                    System.out.println("\n=================================");
                    System.out.println("========= MENU PELANGGAN ========");
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
                        pelanggan.buatPesanan(input, daftarMenu, daftarStokMenu, daftarPesanan, meja.getNoMeja());
                    } else if (pilihPelanggan == 2) {
                        pelanggan.cekPesanan(daftarPesanan, meja.getNoMeja());
                    } else if (pilihPelanggan == 3) {
                        pelanggan.ambilPesanan(input, daftarPesanan, meja.getNoMeja());
                    } else if (pilihPelanggan == 4) {
                        pelanggan.beriRating(input, daftarPesanan, meja.getNoMeja());
                    } else if (pilihPelanggan == 0) {
                        System.out.println("Kembali ke menu utama.");
                    } else {
                        System.out.println("Pilihan tidak valid.");
                    }
                }

            } else if (pilihRole == 2) {
                boolean loginBerhasil = tenant.prosesLogin(input);

                if (loginBerhasil) {
                    int pilihTenant = -1;

                    while (pilihTenant != 0) {
                        System.out.println("\n=================================");
                        System.out.println("========= DASHBOARD TENANT ======");
                        System.out.println("=================================");
                        System.out.println("1. Lihat pesanan aktif");
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
                            tenant.lihatDetailPesanan(input, daftarPesanan);
                        } else if (pilihTenant == 2) {
                            tenant.ubahStatusPesanan(input, daftarPesanan);
                        } else if (pilihTenant == 3) {
                            tenant.lihatRiwayatPesanan(daftarPesanan);
                        } else if (pilihTenant == 4) {
                            tenant.lihatRating(daftarPesanan);
                        } else if (pilihTenant == 5) {
                            manajemenMenu.tambahMenuBaru(input, daftarMenu, daftarStokMenu);
                        } else if (pilihTenant == 6) {
                            manajemenMenu.ubahStokMenu(input, daftarStokMenu);
                        } else if (pilihTenant == 0) {
                            System.out.println("Logout berhasil.");
                        } else {
                            System.out.println("Pilihan tidak valid.");
                        }
                    }
                }

            } else if (pilihRole == 0) {
                System.out.println("Program selesai.");
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }

        // input.close();
    }
}