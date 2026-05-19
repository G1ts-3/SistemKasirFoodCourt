/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package WG58.pengguna;


public abstract class Pengguna {
    protected String idPengguna;
    protected String peran;

    public Pengguna(String idPengguna, String peran) {
        this.idPengguna = idPengguna;
        this.peran = peran;
    }

    public abstract void aksesSistem();
}
