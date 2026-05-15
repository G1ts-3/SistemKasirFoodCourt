package WG58.pengguna;

public abstract class Pengguna {
    protected String idPengguna;
    protected String peran;

    public Pengguna(String idPengguna, String peran) {
        this.idPengguna = idPengguna;
        this.peran = peran;
    }

    public abstract boolean aksesSistem();

    public String getIdPengguna() {
        return idPengguna;
    }

    public String getPeran() {
        return peran;
    }
}
