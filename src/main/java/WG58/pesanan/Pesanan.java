package WG58.pesanan;

import WG58.pembayaran.Pembayaran;
import WG58.menu.Menu;

import java.util.ArrayList;

/**
 * Pesanan — satu transaksi pemesanan.
 *
 * Perubahan dari versi sebelumnya:
 *  - tampilkanPesananTabel() dihapus  — CLI
 *  - tampilkanRingkasTabel() dihapus  — CLI
 *  - Pesanan(String noMeja)  dihapus  — 1-param, tidak pernah dipakai
 *
 * Constructor overloading dipertahankan pada 2 constructor:
 *  1. Pesanan(noMeja, namaTenant)             -> buat pesanan baru dari GUI
 *  2. Pesanan(id, noMeja, idTenant, nama, st) -> rekonstruksi dari DB
 */
public class Pesanan {

    private int                    idPesanan;
    private String                 noMeja;
    private String                 idTenant;
    private String                 namaTenant;
    private String                 statusPesanan;
    private ArrayList<ItemPesanan> daftarItem;
    private Pembayaran             pembayaran;
    private Rating                 rating;

    /** Constructor 1 — buat pesanan baru (Overloading). */
    public Pesanan(String noMeja, String namaTenant) {
        this.noMeja        = noMeja;
        this.namaTenant    = namaTenant;
        this.statusPesanan = "Menunggu";
        this.daftarItem    = new ArrayList<>();
    }

    /** Constructor 2 — rekonstruksi dari database (Overloading). */
    public Pesanan(int idPesanan, String noMeja, String idTenant,
                   String namaTenant, String statusPesanan) {
        this.idPesanan     = idPesanan;
        this.noMeja        = noMeja;
        this.idTenant      = idTenant;
        this.namaTenant    = namaTenant;
        this.statusPesanan = statusPesanan;
        this.daftarItem    = new ArrayList<>();
    }

    public void tambahItem(Menu menu, int jumlah, String catatan) {
        daftarItem.add(new ItemPesanan(menu, jumlah, catatan));
    }

    /** Total harga dalam satuan kupon. */
    public int hitungTotalKupon() {
        int total = 0;
        for (ItemPesanan item : daftarItem) total += item.hitungSubTotalKupon();
        return total;
    }

    /** Total harga dalam Rupiah (1 kupon = Rp5.000). */
    public int hitungTotalRupiah() { return hitungTotalKupon() * 5000; }

    public void ubahStatus(String statusBaru) { this.statusPesanan = statusBaru; }

    // Getter & Setter
    public int                    getIdPesanan()     { return idPesanan; }
    public String                 getNoMeja()        { return noMeja; }
    public String                 getIdTenant()      { return idTenant; }
    public String                 getNamaTenant()    { return namaTenant; }
    public String                 getStatusPesanan() { return statusPesanan; }
    public ArrayList<ItemPesanan> getDaftarItem()    { return daftarItem; }
    public Pembayaran             getPembayaran()    { return pembayaran; }
    public Rating                 getRating()        { return rating; }

    public void setPembayaran(Pembayaran p) { this.pembayaran = p; }
    public void setRating(Rating r)         { this.rating = r; }
}
