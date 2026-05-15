package WG58.pesanan;

import WG58.produk.Produk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Pesanan {
    protected String idPesanan;
    protected String statusPesanan;
    protected List<ItemPesanan> daftarItem;

    public Pesanan(String idPesanan, String statusPesanan) {
        this.idPesanan = idPesanan;
        this.statusPesanan = statusPesanan;
        this.daftarItem = new ArrayList<>();
    }

    public void tambahMenu(Produk p, int qty) {
        daftarItem.add(new ItemPesanan(p, qty));
    }

    public void tambahMenu(ItemPesanan itemPesanan) {
        daftarItem.add(itemPesanan);
    }

    public void ubahStatus(String statusBaru) {
        this.statusPesanan = statusBaru;
    }

    public void cetakStrukDigital() {
        System.out.println("=== STRUK DIGITAL ===");
        System.out.println("ID Pesanan: " + idPesanan);
        System.out.println("Status: " + statusPesanan);

        for (ItemPesanan item : daftarItem) {
            System.out.println("- " + item.getInfo());
        }

        System.out.println("Total: " + getTotalKupon() + " kupon");
    }

    public int getTotalKupon() {
        int total = 0;
        for (ItemPesanan item : daftarItem) {
            total += item.ambilSubTotal();
        }
        return total;
    }

    public String getIdPesanan() {
        return idPesanan;
    }

    public String getStatusPesanan() {
        return statusPesanan;
    }

    public List<ItemPesanan> getDaftarItem() {
        return Collections.unmodifiableList(daftarItem);
    }
}
