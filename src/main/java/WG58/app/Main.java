package WG58.app;

/**
 * Main — entry point lama, sekarang hanya meneruskan ke MainGUI.
 *
 * Seluruh logika CLI (Scanner, loop menu, switch-case) dihapus karena
 * sudah digantikan oleh MainGUI.java dengan tampilan Java Swing.
 * Class ini dipertahankan agar jumlah class dalam class diagram tidak
 * berkurang.
 */
public class Main {

    public static void main(String[] args) {
        MainGUI.main(args);
    }
}
