package WG58.pesanan;

public class Rating {
    private int nilai;
    private String ulasan;

    public Rating(int nilai, String ulasan) {
        this.nilai = nilai;
        this.ulasan = ulasan;
    }

    public int getNilai() {
        return nilai;
    }

    public String getUlasan() {
        return ulasan;
    }
}