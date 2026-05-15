package WG58.pengguna;

import WG58.pesanan.Pesanan;
import WG58.pesanan.Rating;
import WG58.produk.Produk;

import java.util.List;

public class Pelanggan extends Pengguna {
    private String noMeja;
    private int uangPembayaran;

    public Pelanggan(String idPengguna, String noMeja, int uangPembayaran) {
        super(idPengguna, "Pelanggan");
        this.noMeja = noMeja;
        this.uangPembayaran = uangPembayaran;
    }

    @Override
    public boolean aksesSistem() {
        System.out.println("Pelanggan " + idPengguna + " masuk sebagai guest.");
        return true;
    }

    public void pindaiQRMeja(Meja mejaYangDiScan) {
        mejaYangDiScan.ambilNoMeja();
        this.noMeja = mejaYangDiScan.getNoMeja();
        System.out.println("Sesi pelanggan terhubung ke meja " + noMeja);
    }

    public void lihatMenu(List<Produk> daftarMenu) {
        System.out.println("=== DAFTAR MENU ===");
        for (Produk produk : daftarMenu) {
            System.out.println(produk.ambilDetail());
        }
    }

    public void lihatMenu() {
        System.out.println("Menampilkan daftar menu untuk meja " + noMeja + ".");
    }

    public void konfirmasiPesanan() {
        System.out.println("Pesanan dikonfirmasi oleh pelanggan meja " + noMeja + ".");
    }

    public void konfirmasiPesanan(Pesanan pesanan) {
        System.out.println("Pesanan " + pesanan.getIdPesanan() + " dikonfirmasi oleh meja " + noMeja + ".");
    }

    public void berikanRating(Pesanan p, int r, String u) {
        Rating rating = new Rating(p, r, u);
        System.out.println(rating.getInfo());
    }

    public String getNoMeja() {
        return noMeja;
    }

    public int getUangPembayaran() {
        return uangPembayaran;
    }
}
