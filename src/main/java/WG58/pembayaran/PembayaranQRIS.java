package WG58.pembayaran;

import WG58.pesanan.KuponDigital;
import WG58.pesanan.Pesanan;

import java.util.UUID;

public class PembayaranQRIS extends Pembayaran {
    private String kodeQR;

    public PembayaranQRIS(Pesanan pesanan) {
        super("PAY-" + UUID.randomUUID().toString().substring(0, 8), pesanan);
        generateKodeQRIS();
    }

    @Override
    public boolean prosesPembayaran() {
        statusBayar = true;
        pesanan.ubahStatus("Pesanan Diterima");

        System.out.println("Pembayaran QRIS berhasil.");
        System.out.println("Kode QR: " + kodeQR);

        if (pesanan instanceof KuponDigital kuponDigital) {
            System.out.println("Nominal bayar: Rp" + (int) kuponDigital.konversiKeRupiah());
        }

        return statusBayar;
    }

    public void generateKodeQRIS() {
        this.kodeQR = "QRIS-" + pesanan.getIdPesanan() + "-" + System.currentTimeMillis();
    }

    public String getKodeQR() {
        return kodeQR;
    }
}
