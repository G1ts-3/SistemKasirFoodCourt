package tubes.kelompok4.model;

/**
 * [INHERITANCE]: Class Makanan mewarisi sifat dan atribut dari abstract class Menu.
 */
public class Makanan extends Menu {
    private boolean isPedas;

    public Makanan(int id, String nama, double harga, boolean isPedas) {
        // Memanggil constructor class induk (Menu)
        super(id, nama, harga); 
        this.isPedas = isPedas;
    }

    public boolean isPedas() { return isPedas; }

    /**
     * [OVERRIDING]: Menimpa method tampilkanInfo() dari class induk.
     */
    @Override
    public void tampilkanInfo() {
        String level = isPedas ? "(Pedas)" : "(Tidak Pedas)";
        System.out.printf("[%d] Makanan : %s %s - Rp%,.2f\n", getId(), getNama(), level, getHarga());
    }
}