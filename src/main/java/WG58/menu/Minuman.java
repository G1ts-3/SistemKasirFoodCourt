/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package WG58.menu;

/**
 *
 * @author hi
 */
public class Minuman extends Menu{
    public Minuman(String idMenu, String namaMenu, int hargaKupon) {
        super(idMenu, namaMenu, hargaKupon);
    }

    public String getJenis() {
        return "Minuman";
    }
}
