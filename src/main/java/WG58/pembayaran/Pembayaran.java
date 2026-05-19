package WG58.pembayaran;

public interface Pembayaran {
    void prosesPembayaran(int nominalBayar);

    int getTotalBayar();

    boolean getStatusBayar();
}