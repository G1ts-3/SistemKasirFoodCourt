/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package WG58.menu;

/**
 *
 * @author hi
 */
public abstract class Menu {
    protected String idMenu;
    protected String namaMenu;
    protected int hargaKupon;

    public Menu(String idMenu, String namaMenu, int hargaKupon) {
        this.idMenu = idMenu;
        this.namaMenu = namaMenu;
        this.hargaKupon = hargaKupon;
    }

    public String getIdProduk() {
        return idMenu;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public int getHargaKupon() {
        return hargaKupon;
    }

    public abstract String getJenis();
    
    public void tampilkanBarisTabel(int nomor, String status) {
        System.out.printf("| %-4d | %-8s | %-15s | %-8s | %-5d | %-14s |\n",
                nomor,
                idMenu,
                namaMenu,
                getJenis(),
                hargaKupon,
                status);
    }
}
