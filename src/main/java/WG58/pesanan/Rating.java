package WG58.pesanan;

import java.util.UUID;

public class Rating {
    private String idRating;
    private Pesanan pesanan;
    private int nilai;
    private String ulasan;

    public Rating(Pesanan pesanan, int nilai, String ulasan) {
        if (nilai < 1 || nilai > 5) {
            throw new IllegalArgumentException("Nilai rating harus 1 sampai 5.");
        }

        this.idRating = "RT-" + UUID.randomUUID().toString().substring(0, 8);
        this.pesanan = pesanan;
        this.nilai = nilai;
        this.ulasan = ulasan;
    }

    public int getNilai() {
        return nilai;
    }

    public String getUlasan() {
        return ulasan;
    }

    public String getInfo() {
        return "Rating " + nilai + "/5 untuk pesanan " + pesanan.getIdPesanan()
                + " | Ulasan: " + ulasan;
    }

    public String getIdRating() {
        return idRating;
    }
}
