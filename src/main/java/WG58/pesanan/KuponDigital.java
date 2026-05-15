package WG58.pesanan;

import WG58.produk.Produk;

public class KuponDigital extends Pesanan {
    private int totalKupon;
    private double nilaiKursRupiah;

    public KuponDigital(String idPesanan, String statusPesanan, double nilaiKursRupiah) {
        super(idPesanan, statusPesanan);
        this.totalKupon = 0;
        this.nilaiKursRupiah = nilaiKursRupiah;
    }

    public KuponDigital(int totalKupon, double nilaiKursRupiah) {
        super("PSN-MANUAL", "Draft");
        this.totalKupon = totalKupon;
        this.nilaiKursRupiah = nilaiKursRupiah;
    }

    @Override
    public void tambahMenu(Produk p, int qty) {
        super.tambahMenu(p, qty);
        this.totalKupon = getTotalKupon();
    }

    @Override
    public void tambahMenu(ItemPesanan itemPesanan) {
        super.tambahMenu(itemPesanan);
        this.totalKupon = getTotalKupon();
    }

    public double konversiKeRupiah() {
        int totalAktif = getTotalKupon() > 0 ? getTotalKupon() : totalKupon;
        return totalAktif * nilaiKursRupiah;
    }

    public int getTotalKuponDigital() {
        return getTotalKupon() > 0 ? getTotalKupon() : totalKupon;
    }

    public double getNilaiKursRupiah() {
        return nilaiKursRupiah;
    }
}
