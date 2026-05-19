package WG58.pembayaran;

public class PembayaranQRIS extends Pembayaran {
    private String kodeQRIS;

    public PembayaranQRIS(int totalBayar) {
        super(totalBayar);
        this.kodeQRIS = "QRIS-WG58-" + System.currentTimeMillis();
    }

    public void tampilkanQRIS() {
        System.out.println("Kode QRIS : " + kodeQRIS);
        System.out.println("Total     : Rp" + totalBayar);
    }

    public void prosesPembayaran(int nominalBayar) {
        if (nominalBayar == totalBayar) {
            statusBayar = true;
        } else {
            statusBayar = false;
        }
    }
}