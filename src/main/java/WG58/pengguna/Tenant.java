package WG58.pengguna;

import WG58.pesanan.Pesanan;
import WG58.pesanan.Rating;
import WG58.menu.Menu;
import WG58.menu.StokMenu;

import java.util.ArrayList;
import java.util.Scanner;

public class Tenant extends Pengguna {
    private String username;
    private String password;
    private String namaTenant;
    private ArrayList<Menu> daftarMenu;
    private ArrayList<StokMenu> daftarStokMenu;
    private ArrayList<Pesanan> daftarPesanan;

    public Tenant(String idPengguna, String peran, String namaTenant, String username, String password) {
        super(idPengguna, peran);
        this.namaTenant = namaTenant;
        this.username = username;
        this.password = password;
        this.daftarMenu = new ArrayList<Menu>();
        this.daftarStokMenu = new ArrayList<StokMenu>();
        this.daftarPesanan = new ArrayList<Pesanan>();
    }

    public void aksesSistem() {
        System.out.println("Masuk sebagai tenant.");
    }

    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public boolean prosesLogin(Scanner input) {
        aksesSistem();

        System.out.print("Username: ");
        String user = input.nextLine();

        System.out.print("Password: ");
        String pass = input.nextLine();

        if (login(user, pass)) {
            System.out.println("Login berhasil.");
            return true;
        } else {
            System.out.println("Login gagal.");
            return false;
        }
    }

    public void lihatPesananMasuk(ArrayList<Pesanan> daftarPesanan) {
        boolean ada = false;

        System.out.println("\n=== PESANAN AKTIF ===");
        System.out.println("+------+----------+--------------------+-------------+--------------+");
        System.out.println("| No   | Meja     | Status             | Total Kupon | Total Rupiah  |");
        System.out.println("+------+----------+--------------------+-------------+--------------+");

        for (int i = 0; i < daftarPesanan.size(); i++) {
            Pesanan pesanan = daftarPesanan.get(i);

            if (!pesanan.getStatusPesanan().equals("Selesai")) {
                pesanan.tampilkanRingkasTabel(i + 1);
                ada = true;
            }
        }

        System.out.println("+------+----------+--------------------+-------------+--------------+");

        if (!ada) {
            System.out.println("Tidak ada pesanan aktif.");
        }
    }

    public void lihatDetailPesanan(Scanner input, ArrayList<Pesanan> daftarPesanan) {
        if (daftarPesanan.size() == 0) {
            System.out.println("Belum ada pesanan.");
            return;
        }

        lihatPesananMasuk(daftarPesanan);

        System.out.print("Pilih nomor pesanan: ");
        int nomor = input.nextInt();
        input.nextLine();

        if (nomor < 1 || nomor > daftarPesanan.size()) {
            System.out.println("Nomor pesanan tidak valid.");
            return;
        }

        daftarPesanan.get(nomor - 1).tampilkanPesananTabel();
    }

    public void ubahStatusPesanan(Scanner input, ArrayList<Pesanan> daftarPesanan) {
        if (daftarPesanan.size() == 0) {
            System.out.println("Belum ada pesanan.");
            return;
        }

        lihatPesananMasuk(daftarPesanan);

        System.out.print("Pilih nomor pesanan: ");
        int nomor = input.nextInt();
        input.nextLine();

        if (nomor < 1 || nomor > daftarPesanan.size()) {
            System.out.println("Nomor pesanan tidak valid.");
            return;
        }

        Pesanan pesanan = daftarPesanan.get(nomor - 1);

        if (pesanan.getStatusPesanan().equals("Selesai")) {
            System.out.println("Pesanan sudah selesai, tidak bisa diubah.");
            return;
        }

        System.out.println("1. Diproses");
        System.out.println("2. Siap Diambil");
        System.out.print("Pilih status: ");

        int pilihStatus = input.nextInt();
        input.nextLine();

        if (pilihStatus == 1) {
            pesanan.ubahStatus("Diproses");
            System.out.println("Status diubah menjadi Diproses.");
        } else if (pilihStatus == 2) {
            pesanan.ubahStatus("Siap Diambil");
            System.out.println("Status diubah menjadi Siap Diambil.");
        } else {
            System.out.println("Pilihan status tidak valid.");
        }
    }

    public void lihatRiwayatPesanan(ArrayList<Pesanan> daftarPesanan) {
        boolean ada = false;

        System.out.println("\n=== RIWAYAT PESANAN ===");
        System.out.println("+------+----------+--------------------+-------------+--------------+");
        System.out.println("| No   | Meja     | Status             | Total Kupon | Total Rupiah  |");
        System.out.println("+------+----------+--------------------+-------------+--------------+");

        for (int i = 0; i < daftarPesanan.size(); i++) {
            Pesanan pesanan = daftarPesanan.get(i);

            if (pesanan.getStatusPesanan().equals("Selesai")) {
                pesanan.tampilkanRingkasTabel(i + 1);
                ada = true;
            }
        }

        System.out.println("+------+----------+--------------------+-------------+--------------+");

        if (!ada) {
            System.out.println("Belum ada riwayat pesanan.");
        }
    }

    public void lihatRating(ArrayList<Pesanan> daftarPesanan) {
        boolean ada = false;

        System.out.println("\n=== DATA RATING ===");
        System.out.println("+------+----------+--------+------------------------------+");
        System.out.println("| No   | Meja     | Rating | Ulasan                       |");
        System.out.println("+------+----------+--------+------------------------------+");

        for (int i = 0; i < daftarPesanan.size(); i++) {
            Pesanan pesanan = daftarPesanan.get(i);
            Rating rating = pesanan.getRating();

            if (rating != null) {
                System.out.printf("| %-4d | %-8s | %-6d | %-28s |\n",
                        (i + 1),
                        pesanan.getNoMeja(),
                        rating.getNilai(),
                        rating.getUlasan());
                ada = true;
            }
        }

        System.out.println("+------+----------+--------+------------------------------+");

        if (!ada) {
            System.out.println("Belum ada rating.");
        }
    }

    public String getNamaTenant() {
        return namaTenant;
    }

    public ArrayList<Menu> getDaftarMenu() {
        return daftarMenu;
    }

    public ArrayList<StokMenu> getDaftarStokMenu() {
        return daftarStokMenu;
    }

    public ArrayList<Pesanan> getDaftarPesanan() {
        return daftarPesanan;
    }

    public void tambahMenu(Menu menu, int jumlahStok) {
        daftarMenu.add(menu);
        daftarStokMenu.add(new StokMenu(menu, jumlahStok));
    }

    public void tambahPesanan(Pesanan pesanan) {
        daftarPesanan.add(pesanan);
    }

}