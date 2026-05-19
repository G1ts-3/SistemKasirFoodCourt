package WG58.menu;

public class StokMenu {
    private Menu menu;
    private int jumlahStok;

    public StokMenu(Menu menu, int jumlahStok) {
        this.menu = menu;
        this.jumlahStok = jumlahStok;
    }

    public Menu getProduk() {
        return menu;
    }

    public int getJumlahStok() {
        return jumlahStok;
    }

    public void setJumlahStok(int jumlahStok) {
        this.jumlahStok = jumlahStok;
    }

    public boolean stokTersedia() {
        return jumlahStok > 0;
    }

    public void kurangiStok(int jumlah) {
        if (jumlahStok >= jumlah) {
            jumlahStok = jumlahStok - jumlah;
        }
    }

    public void tampilkanBarisTabel(int nomor) {
        System.out.printf("| %-4d | %-8s | %-15s | %-8s | %-5d | %-5d |\n",
                nomor,
                menu.getIdProduk(),
                menu.getNamaMenu(),
                menu.getJenis(),
                menu.getHargaKupon(),
                jumlahStok);
    }
}