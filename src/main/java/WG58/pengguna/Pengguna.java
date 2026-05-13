/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package WG58.pengguna;

/**
 *
 * @author hi
 */

public abstract class Pengguna {

    protected String idPengguna; //// Menggunakan 'protected' agar variabel ini bisa diakses langsung
    protected String peran;      // oleh class turunannya (Pelanggan, Tenant) tapi tetap tersembunyi dari class luar.

    public Pengguna(String idPengguna, String peran) {
        this.idPengguna = idPengguna;
        this.peran = peran;
    }


    public abstract boolean aksesSistem();
}
