package WG58.pengguna;

import WG58.menu.Menu;
import WG58.menu.StokMenu;
import WG58.pesanan.ItemPesanan;
import WG58.pesanan.Pesanan;
import WG58.pesanan.Rating;
import WG58.pembayaran.PembayaranQRIS;
import WG58.database.KonektorMySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Scanner;

public class Pelanggan extends Pengguna {
    public Pelanggan(String idPengguna, String peran) {
        super(idPengguna, peran);
    }

    @Override
    public void aksesSistem() {
        System.out.println("Masuk sebagai pelanggan.");
    }

    public void tampilkanDaftarMenu(ArrayList<Menu> daftarMenu, ArrayList<StokMenu> daftarStokMenu) {
        System.out.println("\n+------+----------+-----------------+----------+-------+----------------+");
        System.out.println("| No   | ID       | Nama Menu       | Jenis    | Kupon | Status         |");
        System.out.println("+------+----------+-----------------+----------+-------+----------------+");

        for (int i = 0; i < daftarMenu.size(); i++) {
            String status;

            if (daftarStokMenu.get(i).getJumlahStok() <= 0) {
                status = "Tidak Tersedia";
            } else {
                status = "Tersedia";
            }

            daftarMenu.get(i).tampilkanBarisTabel(i + 1, status);
        }

        System.out.println("+------+----------+-----------------+----------+-------+----------------+");
        System.out.println("0. Selesai pilih");
    }

    public void buatPesanan(Scanner input, Tenant tenantDipilih, String noMeja) {
        ArrayList<Menu> daftarMenu = tenantDipilih.getDaftarMenu();
        ArrayList<StokMenu> daftarStokMenu = tenantDipilih.getDaftarStokMenu();

        Pesanan pesanan = new Pesanan(noMeja, tenantDipilih.getNamaTenant());

        int pilih = -1;

        while (pilih != 0) {
            tampilkanDaftarMenu(daftarMenu, daftarStokMenu);

            System.out.print("Pilih menu: ");
            pilih = input.nextInt();
            input.nextLine();

            if (pilih > 0 && pilih <= daftarMenu.size()) {
                Menu menuDipilih = daftarMenu.get(pilih - 1);
                StokMenu stokMenu = daftarStokMenu.get(pilih - 1);

                if (!stokMenu.stokTersedia()) {
                    System.out.println("Menu tidak tersedia karena stok habis.");
                    continue;
                }

                System.out.print("Jumlah: ");
                int jumlah = input.nextInt();
                input.nextLine();

                String catatan = "-";

                if (menuDipilih.getJenis().equals("Makanan")) {
                    System.out.print("Catatan makanan: ");
                    catatan = input.nextLine();
                } else if (menuDipilih.getJenis().equals("Minuman")) {
                    System.out.print("Dingin? y/n: ");
                    String jawab = input.nextLine();

                    if (jawab.equalsIgnoreCase("y")) {
                        catatan = "Dingin";
                    } else {
                        catatan = "Tidak dingin";
                    }
                }

                if (stokMenu.getJumlahStok() < jumlah) {
                    System.out.println("Stok tidak cukup.");
                } else {
                    stokMenu.kurangiStok(jumlah);
                    pesanan.tambahItem(menuDipilih, jumlah, catatan);
                    System.out.println("Menu berhasil ditambahkan.");
                }
            } else if (pilih != 0) {
                System.out.println("Menu tidak valid.");
            }
        }

        if (pesanan.hitungTotalKupon() == 0) {
            System.out.println("Pesanan kosong.");
            return;
        }

        System.out.println("\n=== KONFIRMASI PESANAN ===");
        pesanan.tampilkanPesananTabel();

        System.out.print("Bayar sekarang? y/n: ");
        String konfirmasi = input.nextLine();

        if (konfirmasi.equalsIgnoreCase("y")) {
            PembayaranQRIS pembayaran = new PembayaranQRIS(pesanan.hitungTotalRupiah());

            pembayaran.tampilkanQRIS();

            System.out.print("Masukkan nominal bayar: Rp");
            int nominalBayar = input.nextInt();
            input.nextLine();

            pembayaran.prosesPembayaran(nominalBayar);

            if (pembayaran.getStatusBayar()) {
                pesanan.setPembayaran(pembayaran);

                int idPesanan = simpanPesananKeDB(tenantDipilih.getIdPengguna(), pesanan);

                if (idPesanan != -1) {
                    System.out.println("Pembayaran berhasil.");
                    System.out.println("Pesanan dikirim ke tenant.");
                } else {
                    System.out.println("Pesanan gagal disimpan ke database.");
                }
            } else {
                System.out.println("Pembayaran gagal. Nominal tidak sesuai.");
            }
        } else {
            System.out.println("Pesanan dibatalkan.");
        }
    }

    public void cekPesanan(ArrayList<Pesanan> daftarPesanan, String noMeja) {
        boolean ditemukan = false;

        System.out.println("\n=== STATUS PESANAN ===");

        for (int i = 0; i < daftarPesanan.size(); i++) {
            Pesanan pesanan = daftarPesanan.get(i);

            if (pesanan.getNoMeja().equals(noMeja)) {
                pesanan.tampilkanPesananTabel();
                ditemukan = true;
            }
        }

        if (!ditemukan) {
            System.out.println("Belum ada pesanan untuk meja ini.");
        }
    }

    public void ambilPesanan(Scanner input, ArrayList<Pesanan> daftarPesanan, String noMeja) {
        ArrayList<Pesanan> daftarSiap = new ArrayList<Pesanan>();

        System.out.println("\n=== PESANAN SIAP DIAMBIL ===");
        System.out.println("+------+----------+--------------------+");
        System.out.println("| No   | Meja     | Status             |");
        System.out.println("+------+----------+--------------------+");

        int nomor = 1;

        for (int i = 0; i < daftarPesanan.size(); i++) {
            Pesanan pesanan = daftarPesanan.get(i);

            if (pesanan.getNoMeja().equals(noMeja)
                    && pesanan.getStatusPesanan().equals("Siap Diambil")) {

                System.out.printf("| %-4d | %-8s | %-18s |\n",
                        nomor,
                        pesanan.getNoMeja(),
                        pesanan.getStatusPesanan());

                daftarSiap.add(pesanan);
                nomor++;
            }
        }

        System.out.println("+------+----------+--------------------+");

        if (daftarSiap.size() == 0) {
            System.out.println("Belum ada pesanan yang siap diambil.");
            return;
        }

        System.out.print("Pilih nomor pesanan yang diambil: ");
        int pilih = input.nextInt();
        input.nextLine();

        if (pilih < 1 || pilih > daftarSiap.size()) {
            System.out.println("Nomor tidak valid.");
            return;
        }

        Pesanan pesananDipilih = daftarSiap.get(pilih - 1);
        boolean berhasil = updateStatusPesananKeDB(pesananDipilih.getIdPesanan(), "Selesai");

        if (berhasil) {
            pesananDipilih.ubahStatus("Selesai");
            System.out.println("Pesanan berhasil diambil.");
            System.out.println("Sekarang kamu bisa memberi rating.");
        } else {
            System.out.println("Status pesanan gagal diubah di database.");
        }

    }

    public void beriRating(Scanner input, ArrayList<Pesanan> daftarPesanan, String noMeja) {
        ArrayList<Pesanan> daftarBisaRating = new ArrayList<Pesanan>();

        System.out.println("\n=== PESANAN YANG BISA DIRATING ===");
        System.out.println("+------+----------+--------------------+");
        System.out.println("| No   | Meja     | Status             |");
        System.out.println("+------+----------+--------------------+");

        int nomor = 1;

        for (int i = 0; i < daftarPesanan.size(); i++) {
            Pesanan pesanan = daftarPesanan.get(i);

            if (pesanan.getNoMeja().equals(noMeja)
                    && pesanan.getStatusPesanan().equals("Selesai")
                    && pesanan.getRating() == null) {

                System.out.printf("| %-4d | %-8s | %-18s |\n",
                        nomor,
                        pesanan.getNoMeja(),
                        pesanan.getStatusPesanan());

                daftarBisaRating.add(pesanan);
                nomor++;
            }
        }

        System.out.println("+------+----------+--------------------+");

        if (daftarBisaRating.size() == 0) {
            System.out.println("Belum ada pesanan selesai yang bisa dirating.");
            return;
        }

        System.out.print("Pilih nomor pesanan: ");
        int pilih = input.nextInt();
        input.nextLine();

        if (pilih < 1 || pilih > daftarBisaRating.size()) {
            System.out.println("Nomor tidak valid.");
            return;
        }

        System.out.print("Masukkan rating 1-5: ");
        int nilai = input.nextInt();
        input.nextLine();

        if (nilai < 1 || nilai > 5) {
            System.out.println("Rating harus 1 sampai 5.");
            return;
        }

        System.out.print("Masukkan ulasan: ");
        String ulasan = input.nextLine();

        Rating rating = new Rating(nilai, ulasan);
        Pesanan pesananDipilih = daftarBisaRating.get(pilih - 1);

        boolean berhasil = simpanRatingKeDB(pesananDipilih.getIdPesanan(), rating);

        if (berhasil) {
            pesananDipilih.setRating(rating);
            System.out.println("Rating berhasil diberikan.");
        } else {
            System.out.println("Rating gagal disimpan ke database.");
        }
    }

    public int simpanPesananKeDB(String idTenant, Pesanan pesanan) {
        int idPesanan = -1;

        String sqlPesanan = "INSERT INTO pesanan (no_meja, id_tenant, status_pesanan) VALUES (?, ?, ?)";
        String sqlItem = "INSERT INTO item_pesanan (id_pesanan, id_menu, jumlah, catatan) VALUES (?, ?, ?, ?)";
        String sqlPembayaran = "INSERT INTO pembayaran (id_pesanan, total_bayar, status_bayar, kode_qris) VALUES (?, ?, ?, ?)";
        String sqlKurangiStok = "UPDATE stok_menu SET jumlah_stok = jumlah_stok - ? WHERE id_menu = ? AND jumlah_stok >= ?";

        Connection conn = null;

        try {
            conn = KonektorMySQL.getConnection();

            if (conn == null) {
                return -1;
            }

            conn.setAutoCommit(false);

            PreparedStatement pstmtPesanan = conn.prepareStatement(sqlPesanan, Statement.RETURN_GENERATED_KEYS);
            pstmtPesanan.setString(1, pesanan.getNoMeja());
            pstmtPesanan.setString(2, idTenant);
            pstmtPesanan.setString(3, pesanan.getStatusPesanan());
            pstmtPesanan.executeUpdate();

            ResultSet generatedKeys = pstmtPesanan.getGeneratedKeys();

            if (generatedKeys.next()) {
                idPesanan = generatedKeys.getInt(1);
            } else {
                conn.rollback();
                return -1;
            }

            PreparedStatement pstmtItem = conn.prepareStatement(sqlItem);
            PreparedStatement pstmtStok = conn.prepareStatement(sqlKurangiStok);

            for (int i = 0; i < pesanan.getDaftarItem().size(); i++) {
                ItemPesanan item = pesanan.getDaftarItem().get(i);

                pstmtItem.setInt(1, idPesanan);
                pstmtItem.setString(2, item.getMenu().getIdProduk());
                pstmtItem.setInt(3, item.getJumlah());
                pstmtItem.setString(4, item.getCatatan());
                pstmtItem.addBatch();

                pstmtStok.setInt(1, item.getJumlah());
                pstmtStok.setString(2, item.getMenu().getIdProduk());
                pstmtStok.setInt(3, item.getJumlah());
                pstmtStok.addBatch();
            }

            pstmtItem.executeBatch();
            pstmtStok.executeBatch();

            if (pesanan.getPembayaran() instanceof PembayaranQRIS) {
                PembayaranQRIS pembayaran = (PembayaranQRIS) pesanan.getPembayaran();

                PreparedStatement pstmtPembayaran = conn.prepareStatement(sqlPembayaran);
                pstmtPembayaran.setInt(1, idPesanan);
                pstmtPembayaran.setInt(2, pembayaran.getTotalBayar());
                pstmtPembayaran.setBoolean(3, pembayaran.getStatusBayar());
                pstmtPembayaran.setString(4, pembayaran.getKodeQRIS());
                pstmtPembayaran.executeUpdate();
                pstmtPembayaran.close();
            }

            conn.commit();

            generatedKeys.close();
            pstmtPesanan.close();
            pstmtItem.close();
            pstmtStok.close();
            conn.close();

            return idPesanan;
        } catch (Exception e) {
            System.out.println("[DB] Error simpan pesanan: " + e.getMessage());

            try {
                if (conn != null) {
                    conn.rollback();
                    conn.close();
                }
            } catch (Exception rollbackError) {
                System.out.println("[DB] Error rollback: " + rollbackError.getMessage());
            }

            return -1;
        }
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

    public boolean simpanRatingKeDB(int idPesanan, Rating rating) {
        String sql = "INSERT INTO rating (id_pesanan, nilai, ulasan) VALUES (?, ?, ?)";

        try {
            Connection conn = KonektorMySQL.getConnection();

            if (conn == null) {
                return false;
            }

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idPesanan);
            pstmt.setInt(2, rating.getNilai());
            pstmt.setString(3, rating.getUlasan());

            int affectedRows = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            return affectedRows > 0;
        } catch (Exception e) {
            System.out.println("[DB] Error simpan rating: " + e.getMessage());
            return false;
        }
    }


}