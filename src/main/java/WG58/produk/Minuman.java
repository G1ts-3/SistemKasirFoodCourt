package WG58.produk;

public class Minuman extends Produk {
    private boolean dingin;

    public Minuman(String idProduk, String namaMenu, int hargaKupon, boolean dingin) {
        super(idProduk, namaMenu, hargaKupon);
        this.dingin = dingin;
    }

    @Override
    public String ambilDetail() {
        String suhu = dingin ? "Dingin" : "Panas";
        return "[Minuman] " + namaMenu + " | " + hargaKupon + " kupon | " + suhu;
    }

    public boolean isDingin() {
        return dingin;
    }
}
