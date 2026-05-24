/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package WG58.pesanan;

import WG58.menu.Menu;

public class ItemPesanan {
    private Menu menu;
    private int jumlah;
    private String catatan;

    public ItemPesanan(Menu menu, int jumlah, String catatan) {
        this.menu = menu;
        this.jumlah = jumlah;
        this.catatan = catatan;
    }

    public int hitungSubTotalKupon() {
        return menu.getHargaKupon() * jumlah;
    }

    public void tampilkanItemTabel() {
        System.out.printf("| %-15s | %-8s | %-6d | %-6d | %-20s |\n",
                menu.getNamaMenu(),
                menu.getJenis(),
                jumlah,
                hitungSubTotalKupon(),
                catatan);
    }

    public Menu getMenu() {
        return menu;
    }
    public int getJumlah() {
        return jumlah;
    }
    public String getCatatan() {
        return catatan;
    }

}
