package WG58.menu;

/**
 * StokMenu — menyimpan stok untuk satu item Menu.
 *
 * Perubahan: tampilkanBarisTabel(int) dihapus — CLI,
 * digantikan kolom "Stok" di JTable pada MainGUI.
 */
public class StokMenu {

    private Menu menu;
    private int  jumlahStok;

    public StokMenu(Menu menu, int jumlahStok) {
        this.menu       = menu;
        this.jumlahStok = jumlahStok;
    }

    public Menu getProduk()     { return menu; }
    public int  getJumlahStok() { return jumlahStok; }

    public void setJumlahStok(int jumlahStok) {
        this.jumlahStok = jumlahStok;
    }

    public boolean stokTersedia() {
        return jumlahStok > 0;
    }

    public void kurangiStok(int jumlah) {
        if (jumlah > 0 && jumlah <= jumlahStok)
            jumlahStok -= jumlah;
    }
}
