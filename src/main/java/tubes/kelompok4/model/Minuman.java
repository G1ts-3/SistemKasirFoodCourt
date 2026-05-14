package tubes.kelompok4.model;

/**
 * [INHERITANCE]: Class Minuman mewarisi dari abstract class Menu.
 */
public class Minuman extends Menu {
    private boolean isDingin;

    public Minuman(int id, String nama, double harga, boolean isDingin) {
        super(id, nama, harga);
        this.isDingin = isDingin;
    }

    public boolean isDingin() { return isDingin; }

    /**
     * [OVERRIDING]: Menimpa method tampilkanInfo().
     */
    @Override
    public void tampilkanInfo() {
        String suhu = isDingin ? "(Dingin)" : "(Panas)";
        System.out.printf("[%d] Minuman : %s %s - Rp%,.2f\n", getId(), getNama(), suhu, getHarga());
    }
}