package WG58.pengguna;

import WG58.pesanan.Pesanan;
import WG58.pesanan.Rating;
import WG58.menu.Menu;
import WG58.menu.StokMenu;
import WG58.menu.Makanan;
import WG58.menu.Minuman;
import WG58.database.KonektorMySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
        ArrayList<Pesanan> daftarAktif = new ArrayList<Pesanan>();

        System.out.println("\n=== PESANAN AKTIF ===");
        System.out.println("+------+----------+--------------------+-------------+--------------+");
        System.out.println("| No   | Meja     | Status             | Total Kupon | Total Rupiah  |");
        System.out.println("+------+----------+--------------------+-------------+--------------+");

        int nomorTampil = 1;

        for (int i = 0; i < daftarPesanan.size(); i++) {
            Pesanan pesanan = daftarPesanan.get(i);

            if (!pesanan.getStatusPesanan().equals("Selesai")) {
                pesanan.tampilkanRingkasTabel(nomorTampil);
                daftarAktif.add(pesanan);
                nomorTampil++;
            }
        }

        System.out.println("+------+----------+--------------------+-------------+--------------+");

        if (daftarAktif.size() == 0) {
            System.out.println("Tidak ada pesanan aktif.");
            return;
        }

        System.out.print("Pilih nomor pesanan: ");
        int nomor = input.nextInt();
        input.nextLine();

        if (nomor < 1 || nomor > daftarAktif.size()) {
            System.out.println("Nomor pesanan tidak valid.");
            return;
        }

        daftarAktif.get(nomor - 1).tampilkanPesananTabel();
    }

    public void ubahStatusPesanan(Scanner input, ArrayList<Pesanan> daftarPesanan) {
        ArrayList<Pesanan> daftarAktif = new ArrayList<Pesanan>();

        System.out.println("\n=== PESANAN AKTIF ===");
        System.out.println("+------+----------+--------------------+-------------+--------------+");
        System.out.println("| No   | Meja     | Status             | Total Kupon | Total Rupiah  |");
        System.out.println("+------+----------+--------------------+-------------+--------------+");

        int nomorTampil = 1;

        for (int i = 0; i < daftarPesanan.size(); i++) {
            Pesanan pesanan = daftarPesanan.get(i);

            if (!pesanan.getStatusPesanan().equals("Selesai")) {
                pesanan.tampilkanRingkasTabel(nomorTampil);
                daftarAktif.add(pesanan);
                nomorTampil++;
            }
        }

        System.out.println("+------+----------+--------------------+-------------+--------------+");

        if (daftarAktif.size() == 0) {
            System.out.println("Tidak ada pesanan aktif.");
            return;
        }

        System.out.print("Pilih nomor pesanan: ");
        int nomor = input.nextInt();
        input.nextLine();

        if (nomor < 1 || nomor > daftarAktif.size()) {
            System.out.println("Nomor pesanan tidak valid.");
            return;
        }

        Pesanan pesanan = daftarAktif.get(nomor - 1);

        System.out.println("1. Diproses");
        System.out.println("2. Siap Diambil");
        System.out.print("Pilih status: ");

        int pilihStatus = input.nextInt();
        input.nextLine();

        if (pilihStatus == 1) {
            boolean berhasil = updateStatusPesananKeDB(pesanan.getIdPesanan(), "Diproses");

            if (berhasil) {
                pesanan.ubahStatus("Diproses");
                System.out.println("Status diubah menjadi Diproses.");
            } else {
                System.out.println("Status gagal diubah di database.");
            }
        } else if (pilihStatus == 2) {
            boolean berhasil = updateStatusPesananKeDB(pesanan.getIdPesanan(), "Siap Diambil");

            if (berhasil) {
                pesanan.ubahStatus("Siap Diambil");
                System.out.println("Status diubah menjadi Siap Diambil.");
            } else {
                System.out.println("Status gagal diubah di database.");
            }
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

    public void tambahMenu(Menu menu, int jumlahStok) {
        daftarMenu.add(menu);
        daftarStokMenu.add(new StokMenu(menu, jumlahStok));
    }

    public void tambahPesanan(Pesanan pesanan) {
        daftarPesanan.add(pesanan);
    }

    public ArrayList<Menu> ambilMenuDariDB() {
        ArrayList<Menu> hasil = new ArrayList<Menu>();
        String sql = "SELECT id_menu, nama_menu, jenis, harga_kupon FROM menu WHERE id_tenant = ? ORDER BY id_menu";

        try {
            Connection conn = KonektorMySQL.getConnection();

            if (conn == null) {
                return hasil;
            }

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, getIdPengguna());

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String jenis = rs.getString("jenis");
                Menu menu;

                if ("Makanan".equalsIgnoreCase(jenis)) {
                    menu = new Makanan(rs.getString("id_menu"), rs.getString("nama_menu"), rs.getInt("harga_kupon"));
                } else {
                    menu = new Minuman(rs.getString("id_menu"), rs.getString("nama_menu"), rs.getInt("harga_kupon"));
                }

                hasil.add(menu);
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("[DB] Error ambil menu tenant: " + e.getMessage());
        }

        return hasil;
    }

    public ArrayList<StokMenu> ambilStokMenuDariDB() {
        ArrayList<StokMenu> hasil = new ArrayList<StokMenu>();

        String sql = "SELECT m.id_menu, m.nama_menu, m.jenis, m.harga_kupon, s.jumlah_stok " +
                    "FROM menu m " +
                    "JOIN stok_menu s ON m.id_menu = s.id_menu " +
                    "WHERE m.id_tenant = ? " +
                    "ORDER BY m.id_menu";

        try {
            Connection conn = KonektorMySQL.getConnection();

            if (conn == null) {
                return hasil;
            }

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, getIdPengguna());

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String jenis = rs.getString("jenis");
                Menu menu;

                if ("Makanan".equalsIgnoreCase(jenis)) {
                    menu = new Makanan(rs.getString("id_menu"), rs.getString("nama_menu"), rs.getInt("harga_kupon"));
                } else {
                    menu = new Minuman(rs.getString("id_menu"), rs.getString("nama_menu"), rs.getInt("harga_kupon"));
                }

                hasil.add(new StokMenu(menu, rs.getInt("jumlah_stok")));
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("[DB] Error ambil stok tenant: " + e.getMessage());
        }

        return hasil;
    }

    public ArrayList<Pesanan> ambilPesananDariDB() {
        ArrayList<Pesanan> hasil = new ArrayList<Pesanan>();

        String sql = "SELECT id_pesanan, no_meja, id_tenant, status_pesanan " +
                    "FROM pesanan WHERE id_tenant = ? ORDER BY id_pesanan";

        try {
            Connection conn = KonektorMySQL.getConnection();

            if (conn == null) {
                return hasil;
            }

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, getIdPengguna());

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Pesanan pesanan = new Pesanan(
                        rs.getInt("id_pesanan"),
                        rs.getString("no_meja"),
                        rs.getString("id_tenant"),
                        getNamaTenant(),
                        rs.getString("status_pesanan"));
                        
                isiItemPesananDariDB(conn, pesanan);
                isiRatingDariDB(conn, pesanan);
                hasil.add(pesanan);
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("[DB] Error ambil pesanan tenant: " + e.getMessage());
        }

        return hasil;
    }
    
    private void isiItemPesananDariDB(Connection conn, Pesanan pesanan) throws Exception {
        String sql = "SELECT m.id_menu, m.nama_menu, m.jenis, m.harga_kupon, i.jumlah, i.catatan " +
                    "FROM item_pesanan i " +
                    "JOIN menu m ON i.id_menu = m.id_menu " +
                    "WHERE i.id_pesanan = ? " +
                    "ORDER BY i.id_item";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, pesanan.getIdPesanan());

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Menu menu;

            if ("Makanan".equalsIgnoreCase(rs.getString("jenis"))) {
                menu = new Makanan(rs.getString("id_menu"), rs.getString("nama_menu"), rs.getInt("harga_kupon"));
            } else {
                menu = new Minuman(rs.getString("id_menu"), rs.getString("nama_menu"), rs.getInt("harga_kupon"));
            }

            pesanan.tambahItem(menu, rs.getInt("jumlah"), rs.getString("catatan"));
        }

        rs.close();
        pstmt.close();
    }

    public boolean updateStatusPesananKeDB(int idPesanan, String statusBaru) {
        String sql = "UPDATE pesanan SET status_pesanan = ? WHERE id_pesanan = ?";

        try {
            Connection conn = KonektorMySQL.getConnection();

            if (conn == null) {
                return false;
            }

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, statusBaru);
            pstmt.setInt(2, idPesanan);

            int affectedRows = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            return affectedRows > 0;
        } catch (Exception e) {
            System.out.println("[DB] Error update status pesanan: " + e.getMessage());
            return false;
        }
    }

    private void isiRatingDariDB(Connection conn, Pesanan pesanan) throws Exception {
        String sql = "SELECT nilai, ulasan FROM rating WHERE id_pesanan = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, pesanan.getIdPesanan());

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            Rating rating = new Rating(
                    rs.getInt("nilai"),
                    rs.getString("ulasan"));

            pesanan.setRating(rating);
        }

        rs.close();
        pstmt.close();
    }


}