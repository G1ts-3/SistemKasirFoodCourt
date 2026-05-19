package WG58.pembayaran;

public class PembayaranQRIS implements Pembayaran {
    private int totalBayar;
    private boolean statusBayar;
    private String kodeQRIS;

    public PembayaranQRIS(int totalBayar) {
        this.totalBayar = totalBayar;
        this.statusBayar = false;
        this.kodeQRIS = "QRIS-WG58-" + System.currentTimeMillis();
    }

    public void tampilkanQRIS() {
        System.out.println("Kode QRIS : " + kodeQRIS);
        System.out.println("Total     : Rp" + totalBayar);
    }

    @Override
    public void prosesPembayaran(int nominalBayar) {
        if (nominalBayar == totalBayar) {
            statusBayar = true;
        } else {
            statusBayar = false;
        }
    }

    @Override
    public int getTotalBayar() {
        return totalBayar;
    }

    @Override
    public boolean getStatusBayar() {
        return statusBayar;
    }
}