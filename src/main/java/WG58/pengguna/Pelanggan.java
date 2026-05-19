package WG58.pengguna;

import WG58.menu.Menu;
import WG58.menu.StokMenu;
import WG58.pesanan.Pesanan;
import WG58.pesanan.Rating;
import WG58.pembayaran.PembayaranQRIS;

import java.util.ArrayList;
import java.util.Scanner;

public class Pelanggan extends Pengguna {
    public Pelanggan(String idPengguna, String peran) {
        super(idPengguna, peran);
    }

    public void aksesSistem() {
        System.out.println("Masuk sebagai pelanggan.");
    }

    public void tampilkanDaftarMenu(ArrayList<Menu> daftarMenu) {
        System.out.println("\n+------+----------+-----------------+----------+-------+");
        System.out.println("| No   | ID       | Nama Menu       | Jenis    | Kupon |");
        System.out.println("+------+----------+-----------------+----------+-------+");

        for (int i = 0; i < daftarMenu.size(); i++) {
            daftarMenu.get(i).tampilkanBarisTabel(i + 1);
        }

        System.out.println("+------+----------+-----------------+----------+-------+");
        System.out.println("0. Selesai pilih");
    }

    public void buatPesanan(Scanner input,ArrayList<Menu> daftarMenu,ArrayList<StokMenu> daftarStokMenu,
            ArrayList<Pesanan> daftarPesanan,String noMeja) {
        Pesanan pesanan = new Pesanan(noMeja);

        int pilih = -1;

        while (pilih != 0) {
            tampilkanDaftarMenu(daftarMenu);

            System.out.print("Pilih menu: ");
            pilih = input.nextInt();
            input.nextLine();

            if (pilih > 0 && pilih <= daftarMenu.size()) {
                Menu menuDipilih = daftarMenu.get(pilih - 1);

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
                StokMenu stokMenu = daftarStokMenu.get(pilih - 1);
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
                daftarPesanan.add(pesanan);

                System.out.println("Pembayaran berhasil.");
                System.out.println("Pesanan dikirim ke tenant.");
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
        pesananDipilih.ubahStatus("Selesai");

        System.out.println("Pesanan berhasil diambil.");
        System.out.println("Sekarang kamu bisa memberi rating.");
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
        daftarBisaRating.get(pilih - 1).setRating(rating);

        System.out.println("Rating berhasil diberikan.");
    }
}