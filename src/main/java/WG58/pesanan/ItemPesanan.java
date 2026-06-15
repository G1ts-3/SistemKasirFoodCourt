package WG58.pesanan;

import WG58.menu.Menu;

/**
 * ItemPesanan — satu baris item dalam sebuah pesanan.
 *
 * Perubahan: tampilkanItemTabel() dihapus — CLI,
 * digantikan baris di JTable keranjang pada MainGUI.
 */
public class ItemPesanan {

    private Menu   menu;
    private int    jumlah;
    private String catatan;

    public ItemPesanan(Menu menu, int jumlah, String catatan) {
        this.menu    = menu;
        this.jumlah  = jumlah;
        this.catatan = catatan;
    }

    /** Hitung sub-total dalam satuan kupon. */
    public int hitungSubTotalKupon() {
        return menu.getHargaKupon() * jumlah;
    }

    public Menu   getMenu()    { return menu; }
    public int    getJumlah()  { return jumlah; }
    public String getCatatan() { return catatan; }
}
