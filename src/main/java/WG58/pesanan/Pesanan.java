package WG58.pesanan;

import WG58.menu.Menu;
import WG58.pembayaran.Pembayaran;
import java.util.ArrayList;

public class Pesanan {
    private String noMeja;
    private String statusPesanan;
    private ArrayList<ItemPesanan> daftarItem;
    private Pembayaran pembayaran;
    private Rating rating;
    private String namaTenant;

    public Pesanan(String noMeja) {
        this(noMeja, "-");
    }

    public Pesanan(String noMeja, String namaTenant) {
        this.noMeja = noMeja;
        this.namaTenant = namaTenant;
        this.statusPesanan = "Pesanan Diterima";
        this.daftarItem = new ArrayList<ItemPesanan>();
        this.pembayaran = null;
        this.rating = null;
    }

    public void tambahItem(Menu menu, int jumlah, String catatan) {
        ItemPesanan item = new ItemPesanan(menu, jumlah, catatan);
        daftarItem.add(item);
    }

    public int hitungTotalKupon() {
        int total = 0;

        for (int i = 0; i < daftarItem.size(); i++) {
            total = total + daftarItem.get(i).hitungSubTotalKupon();
        }

        return total;
    }

    public int hitungTotalRupiah() {
        return hitungTotalKupon() * 5000;
    }

    public void tampilkanPesananTabel() {
        System.out.println("+-----------------+----------+--------+--------+----------------------+");
        System.out.println("| Menu            | Jenis    | Jumlah | Kupon  | Catatan              |");
        System.out.println("+-----------------+----------+--------+--------+----------------------+");

        for (int i = 0; i < daftarItem.size(); i++) {
            daftarItem.get(i).tampilkanItemTabel();
        }

        System.out.println("+-----------------+----------+--------+--------+----------------------+");
        System.out.println("Tenant       : " + namaTenant);
        System.out.println("Meja         : " + noMeja);
        System.out.println("Status       : " + statusPesanan);
        System.out.println("Total Kupon  : " + hitungTotalKupon());
        System.out.println("Total Rupiah : Rp" + hitungTotalRupiah());
    }

    public void tampilkanRingkasTabel(int nomor) {
        System.out.printf("| %-4d | %-8s | %-18s | %-11d | %-12s |\n",
                nomor,
                noMeja,
                statusPesanan,
                hitungTotalKupon(),
                "Rp" + hitungTotalRupiah());
    }

    public void ubahStatus(String statusBaru) {
        this.statusPesanan = statusBaru;
    }

    public String getStatusPesanan() {
        return statusPesanan;
    }

    public String getNoMeja() {
        return noMeja;
    }

    public void setPembayaran(Pembayaran pembayaran) {
        this.pembayaran = pembayaran;
    }

    public Pembayaran getPembayaran() {
        return pembayaran;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Rating getRating() {
        return rating;
    }

    public String getNamaTenant() {
        return namaTenant;
    }
}