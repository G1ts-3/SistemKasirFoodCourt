package WG58.pengguna;

public class Meja {
    private String noMeja;
    private String qrCodeMeja;

    public Meja(String noMeja) {
        this.noMeja = noMeja;
        this.qrCodeMeja = "QR-" + noMeja;
    }

    public void ambilNoMeja() {
        System.out.println("Nomor meja terdeteksi: " + noMeja);
    }

    public String getNoMeja() {
        return noMeja;
    }

    public String getQrCodeMeja() {
        return qrCodeMeja;
    }
}
