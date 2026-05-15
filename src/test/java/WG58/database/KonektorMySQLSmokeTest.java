package WG58.database;

import WG58.pesanan.KuponDigital;
import WG58.produk.Makanan;
import WG58.produk.Produk;

public class KonektorMySQLSmokeTest {
    public static void main(String[] args) {
        KonektorMySQL database = new KonektorMySQL("localhost", 3306, "wg58_foodcourt");
        if (!database.hubungkan()) {
            throw new IllegalStateException("Tidak bisa konek ke MySQL. Cek JDBC driver, user, password, dan database.");
        }

        String suffix = Long.toString(System.currentTimeMillis()).substring(7);
        String noMeja = "WG-T-" + suffix;
        String idProduk = "TST-" + suffix;
        String idPesanan = "PSN-T-" + suffix;

        database.simpanMeja(noMeja);

        Produk produk = new Makanan(idProduk, "Smoke Test Menu " + suffix, 2, "Dibuat oleh smoke test");
        if (!database.tambahMenu(produk, 5)) {
            throw new IllegalStateException("Menu gagal disimpan.");
        }

        KuponDigital pesanan = new KuponDigital(idPesanan, "Pesanan Diterima", 5000);
        pesanan.tambahMenu(produk, 2);
        if (!database.simpanPesanan("PLG-T-" + suffix, noMeja, pesanan, "PAY-T-" + suffix, "QRIS-T-" + suffix, true)) {
            throw new IllegalStateException("Pesanan gagal disimpan: " + database.getPesanKesalahanTerakhir());
        }

        KonektorMySQL.ProdukStok stokSesudahPesan = database.ambilSemuaMenuDenganStok().stream()
                .filter(item -> idProduk.equals(item.getIdProduk()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Produk test tidak ditemukan setelah pesan."));
        if (stokSesudahPesan.getStok() != 3) {
            throw new IllegalStateException("Stok tidak berkurang sesuai pesanan. Stok sekarang: " + stokSesudahPesan.getStok());
        }

        KonektorMySQL.PesananTersimpan pesananAwal = database.ambilPesananAktifByMeja(noMeja);
        if (pesananAwal == null || !idPesanan.equals(pesananAwal.getPesanan().getIdPesanan())) {
            throw new IllegalStateException("Pesanan aktif gagal dibaca dari meja.");
        }

        if (database.ambilSemuaPesananAktif().stream().noneMatch(item -> idPesanan.equals(item.getPesanan().getIdPesanan()))) {
            throw new IllegalStateException("Pesanan masuk gagal tampil di daftar tenant.");
        }

        if (!database.updateStatusPesanan(idPesanan, "Siap Diambil")) {
            throw new IllegalStateException("Status pesanan gagal diupdate.");
        }

        KonektorMySQL.PesananTersimpan pesananSiap = database.ambilPesananById(idPesanan);
        if (pesananSiap == null || !"Siap Diambil".equals(pesananSiap.getPesanan().getStatusPesanan())) {
            throw new IllegalStateException("Status Siap Diambil gagal dibaca ulang.");
        }

        if (!database.simpanRating(idPesanan, 5, "Smoke test berhasil")) {
            throw new IllegalStateException("Rating gagal disimpan.");
        }

        KonektorMySQL.PesananTersimpan pesananSelesai = database.ambilPesananById(idPesanan);
        if (pesananSelesai == null || !"Selesai".equals(pesananSelesai.getPesanan().getStatusPesanan())) {
            throw new IllegalStateException("Pesanan tidak berubah menjadi Selesai setelah rating.");
        }

        System.out.println("SMOKE_TEST_OK idPesanan=" + idPesanan + " noMeja=" + noMeja);
        database.putuskan();
    }
}
