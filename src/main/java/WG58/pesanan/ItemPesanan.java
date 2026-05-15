package WG58.pesanan;

import WG58.produk.Produk;

public class ItemPesanan {
    private Produk produk;
    private int jumlah;
    private int subTotalKupon;

    public ItemPesanan(Produk produk, int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah item harus lebih dari 0.");
        }

        this.produk = produk;
        this.jumlah = jumlah;
        this.subTotalKupon = produk.getHargaKupon() * jumlah;
    }

    public int ambilSubTotal() {
        return subTotalKupon;
    }

    public Produk getProduk() {
        return produk;
    }

    public int getJumlah() {
        return jumlah;
    }

    public String getInfo() {
        return produk.getNamaMenu() + " x" + jumlah + " = " + subTotalKupon + " kupon";
    }
}
