/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package WG58.menu;

/**
 *
 * @author hi
 */
public class Makanan extends Menu{
    public Makanan(String idMenu, String namaMenu, int hargaKupon) {
        super(idMenu, namaMenu, hargaKupon);
    }

    public String getJenis() {
        return "Makanan";
    }
}
