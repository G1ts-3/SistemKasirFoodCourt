package WG58.pengguna;

import WG58.pesanan.Pesanan;

public class Pelanggan extends Pengguna {

    private String noMejaAktif;
    private int uangPembayaran;

    @Override
    public boolean aksesSistem() {
        return false;
    }

    public void lihatMenu() {

    }

    public void konfirmasiPesanan() {

    }

    public void beriRating(Pesanan p, int r, String u) {
        
    }

}
