package WG58.menu;

/**
 * Menu — abstract class (Abstraction).
 *
 * Perubahan dari versi sebelumnya:
 *  - tampilkanBarisTabel(int)          dihapus — CLI
 *  - tampilkanBarisTabel(int, String)  dihapus — CLI (keduanya adalah overloading,
 *    tapi sudah tidak relevan karena GUI menggunakan JTable, bukan System.out)
 *  - getIdMenu()  dihapus — duplikat dari getIdProduk(), keduanya return field idMenu
 *
 * Overloading masih dipertahankan melalui constructor overloading di Pesanan.java.
 */
public abstract class Menu {

    protected String idMenu;
    protected String namaMenu;
    protected int    hargaKupon;

    public Menu(String idMenu, String namaMenu, int hargaKupon) {
        this.idMenu     = idMenu;
        this.namaMenu   = namaMenu;
        this.hargaKupon = hargaKupon;
    }

    // Getter
    public String getIdProduk()   { return idMenu; }
    public String getNamaMenu()   { return namaMenu; }
    public int    getHargaKupon() { return hargaKupon; }

    /** Abstract — di-override oleh Makanan dan Minuman (Polymorphism). */
    public abstract String getJenis();
}
