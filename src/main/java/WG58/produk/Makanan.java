package WG58.produk;

public class Makanan extends Produk {
    private String catatan;

    public Makanan(String idProduk, String namaMenu, int hargaKupon, String catatan) {
        super(idProduk, namaMenu, hargaKupon);
        this.catatan = catatan;
    }


    @Override
    public String ambilDetail() {
        return "[Makanan] " + namaMenu + " | " + hargaKupon + " kupon | Catatan: " + catatan;
    }

    public String getCatatan() {
        return catatan;
    }
}
