package WG58.pengguna;

import WG58.pesanan.Pesanan;

public class Pelanggan extends Pengguna {

    private String noMeja;
    private int uangPembayaran;

    public Pelanggan(String idPengguna, String peran,String noMeja, int uangPembayaran) {
        super(idPengguna, peran);
        this.noMeja = noMeja;
        this.uangPembayaran = uangPembayaran;
    }

    @Override
    public boolean aksesSistem() {
        return false; // dia sebagai pelanggan
    }

    public void lihatMenu() {
        
    }

    public void konfirmasiPesanan() {
        
    }

    public void beriRating(Pesanan p, int r, String u) {
        
    }

}
