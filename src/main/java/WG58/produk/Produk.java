package WG58.produk;

public abstract class Produk {
    protected String idProduk;
    protected String namaMenu;
    protected int hargaKupon;

    public Produk(String idProduk, String namaMenu, int hargaKupon) {
        this.idProduk = idProduk;
        this.namaMenu = namaMenu;
        this.hargaKupon = hargaKupon;
    }

    public abstract String ambilDetail();

    public String getIdProduk() {
        return idProduk;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public int getHargaKupon() {
        return hargaKupon;
    }
}
