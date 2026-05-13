/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package WG58.pengguna;

/**
 *
 * @author hi
 */

public abstract class Pengguna {

    private String idPengguna;
    private String peran;

    public Pengguna(String idPengguna, String peran) {
        this.idPengguna = idPengguna;
        this.peran = peran;
    }
    

    public abstract boolean aksesSistem();
}
