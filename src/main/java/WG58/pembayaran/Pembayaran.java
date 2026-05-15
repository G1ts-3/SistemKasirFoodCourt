package WG58.pembayaran;

import WG58.pesanan.Pesanan;

public abstract class Pembayaran {
    protected String idPembayaran;
    protected boolean statusBayar;
    protected Pesanan pesanan;

    public Pembayaran(String idPembayaran, Pesanan pesanan) {
        this.idPembayaran = idPembayaran;
        this.pesanan = pesanan;
        this.statusBayar = false;
    }

    public abstract boolean prosesPembayaran();

    public String getIdPembayaran() {
        return idPembayaran;
    }

    public boolean isStatusBayar() {
        return statusBayar;
    }
}
