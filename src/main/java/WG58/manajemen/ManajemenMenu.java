package WG58.manajemen;

import java.util.ArrayList;
import java.util.Scanner;

import WG58.menu.Makanan;
import WG58.menu.Menu;
import WG58.menu.Minuman;
import WG58.menu.StokMenu;

public class ManajemenMenu {
    public void tampilkanMenuDenganStok(ArrayList<StokMenu> daftarStokMenu) {
        System.out.println("\n+------+----------+-----------------+----------+-------+-------+");
        System.out.println("| No   | ID       | Nama Menu       | Jenis    | Kupon | Stok  |");
        System.out.println("+------+----------+-----------------+----------+-------+-------+");

        for (int i = 0; i < daftarStokMenu.size(); i++) {
            daftarStokMenu.get(i).tampilkanBarisTabel(i + 1);
        }

        System.out.println("+------+----------+-----------------+----------+-------+-------+");
    }

    public void tambahMenuBaru(Scanner input, ArrayList<Menu> daftarMenu, ArrayList<StokMenu> daftarStokMenu) {
        System.out.println("\n=== TAMBAH MENU BARU ===");
        System.out.println("1. Makanan");
        System.out.println("2. Minuman");
        System.out.print("Pilih jenis: ");

        int jenis = input.nextInt();
        input.nextLine();

        System.out.print("ID Menu: ");
        String id = input.nextLine();

        System.out.print("Nama Menu: ");
        String nama = input.nextLine();

        System.out.print("Harga Kupon: ");
        int harga = input.nextInt();
        input.nextLine();

        System.out.print("Stok Awal: ");
        int stok = input.nextInt();
        input.nextLine();

        Menu menuBaru = null;

        if (jenis == 1) {
            menuBaru = new Makanan(id, nama, harga);
        } else if (jenis == 2) {
            menuBaru = new Minuman(id, nama, harga);
        } else {
            System.out.println("Jenis tidak valid.");
            return;
        }

        daftarMenu.add(menuBaru);
        daftarStokMenu.add(new StokMenu(menuBaru, stok));

        System.out.println("Menu baru berhasil ditambahkan.");
    }

    public void ubahStokMenu(Scanner input, ArrayList<StokMenu> daftarStokMenu) {
        if (daftarStokMenu.size() == 0) {
            System.out.println("Belum ada menu.");
            return;
        }

        tampilkanMenuDenganStok(daftarStokMenu);

        System.out.print("Pilih nomor menu: ");
        int nomor = input.nextInt();
        input.nextLine();

        if (nomor < 1 || nomor > daftarStokMenu.size()) {
            System.out.println("Nomor menu tidak valid.");
            return;
        }

        System.out.print("Masukkan stok baru: ");
        int stokBaru = input.nextInt();
        input.nextLine();

        daftarStokMenu.get(nomor - 1).setJumlahStok(stokBaru);

        System.out.println("Stok berhasil diubah.");
    }
}