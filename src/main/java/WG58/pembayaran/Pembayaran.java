package WG58.pembayaran;

public abstract class Pembayaran {
    protected int totalBayar;
    protected boolean statusBayar;

    public Pembayaran(int totalBayar) {
        this.totalBayar = totalBayar;
        this.statusBayar = false;
    }

    public abstract void prosesPembayaran(int nominalBayar);

    public int getTotalBayar() {
        return totalBayar;
    }

    public boolean getStatusBayar() {
        return statusBayar;
    }
}