package WG58.manajemen;

import java.util.ArrayList;
import java.util.Scanner;

import java.sql.Connection;
import java.sql.PreparedStatement;

import WG58.menu.Makanan;
import WG58.menu.Menu;
import WG58.menu.Minuman;
import WG58.menu.StokMenu;
import WG58.database.KonektorMySQL;

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

    public void tambahMenuBaru(Scanner input, String idTenant, ArrayList<Menu> daftarMenu, ArrayList<StokMenu> daftarStokMenu) {
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

        boolean berhasil = tambahMenuKeDB(idTenant, menuBaru, stok);

        if (berhasil) {
            daftarMenu.add(menuBaru);
            daftarStokMenu.add(new StokMenu(menuBaru, stok));
            System.out.println("Menu baru berhasil ditambahkan.");
        } else {
            System.out.println("Menu gagal ditambahkan ke database.");
        }
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

        StokMenu stokMenu = daftarStokMenu.get(nomor - 1);
        boolean berhasil = updateStokKeDB(stokMenu.getProduk().getIdProduk(), stokBaru);

        if (berhasil) {
            stokMenu.setJumlahStok(stokBaru);
            System.out.println("Stok berhasil diubah.");
        } else {
            System.out.println("Stok gagal diubah di database.");
        }

    }

    public boolean tambahMenuKeDB(String idTenant, Menu menu, int stokAwal) {
        String sqlMenu = "INSERT INTO menu (id_menu, nama_menu, jenis, harga_kupon, id_tenant) VALUES (?, ?, ?, ?, ?)";
        String sqlStok = "INSERT INTO stok_menu (id_menu, id_tenant, jumlah_stok) VALUES (?, ?, ?)";

        try {
            Connection conn = KonektorMySQL.getConnection();

            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);

            PreparedStatement pstmtMenu = conn.prepareStatement(sqlMenu);
            pstmtMenu.setString(1, menu.getIdProduk());
            pstmtMenu.setString(2, menu.getNamaMenu());
            pstmtMenu.setString(3, menu.getJenis());
            pstmtMenu.setInt(4, menu.getHargaKupon());
            pstmtMenu.setString(5, idTenant);
            pstmtMenu.executeUpdate();

            PreparedStatement pstmtStok = conn.prepareStatement(sqlStok);
            pstmtStok.setString(1, menu.getIdProduk());
            pstmtStok.setString(2, idTenant);
            pstmtStok.setInt(3, stokAwal);
            pstmtStok.executeUpdate();

            conn.commit();

            pstmtMenu.close();
            pstmtStok.close();
            conn.close();

            return true;
        } catch (Exception e) {
            System.out.println("[DB] Error tambah menu: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStokKeDB(String idMenu, int stokBaru) {
        String sql = "UPDATE stok_menu SET jumlah_stok = ? WHERE id_menu = ?";

        try {
            Connection conn = KonektorMySQL.getConnection();

            if (conn == null) {
                return false;
            }

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, stokBaru);
            pstmt.setString(2, idMenu);

            int affectedRows = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            return affectedRows > 0;
        } catch (Exception e) {
            System.out.println("[DB] Error update stok: " + e.getMessage());
            return false;
        }
    }


}