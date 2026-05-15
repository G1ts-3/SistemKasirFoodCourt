package WG58.database;

import WG58.pesanan.ItemPesanan;
import WG58.pesanan.KuponDigital;
import WG58.produk.Makanan;
import WG58.produk.Minuman;
import WG58.produk.Produk;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KonektorMySQL implements ManajerDatabase {
    private static final String STATUS_SELESAI = "Selesai";
    private static final int DEFAULT_KURS = 5000;

    private final String host;
    private final int port;
    private final String namaDB;
    private final String username;
    private final String password;

    private boolean statusKoneksi;
    private Connection koneksi;
    private String pesanKesalahanTerakhir = "";

    public KonektorMySQL(String host, int port, String namaDB) {
        this(
                bacaEnv("WG58_DB_HOST", host),
                bacaPort("WG58_DB_PORT", port),
                bacaEnv("WG58_DB_NAME", namaDB),
                bacaEnv("WG58_DB_USER", "root"),
                bacaEnv("WG58_DB_PASSWORD", "")
        );
    }

    public KonektorMySQL(String host, int port, String namaDB, String username, String password) {
        this.host = host;
        this.port = port;
        this.namaDB = namaDB;
        this.username = username;
        this.password = password;
        this.statusKoneksi = false;
    }

    @Override
    public boolean hubungkan() {
        if (statusKoneksi && koneksi != null) {
            return true;
        }

        if (!muatDriverMySQL()) {
            statusKoneksi = false;
            koneksi = null;
            return false;
        }

        try {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + namaDB
                    + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia%2FBangkok";
            koneksi = DriverManager.getConnection(url, username, password);
            statusKoneksi = true;
            inisialisasiSchema();
            seedDataAwal();
            System.out.println("Terhubung ke database " + namaDB + " di " + host + ":" + port);
            return true;
        } catch (SQLException e) {
            statusKoneksi = false;
            koneksi = null;
            setError("Gagal terhubung ke database: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void putuskan() {
        if (koneksi != null) {
            try {
                koneksi.close();
            } catch (SQLException ignored) {
            }
        }
        statusKoneksi = false;
        koneksi = null;
        System.out.println("Koneksi database diputus.");
    }

    @Override
    public boolean simpanKeDB(Object data) {
        if (!pastikanTerkoneksi()) {
            System.out.println("Gagal simpan. Database belum terhubung.");
            return false;
        }

        try (PreparedStatement ps = koneksi.prepareStatement(
                "INSERT INTO log_aplikasi (id_log, pesan_log, dibuat_pada) VALUES (?, ?, ?)")) {
            ps.setString(1, "LOG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            ps.setString(2, String.valueOf(data));
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            setError("Gagal menyimpan log: " + e.getMessage());
            return false;
        }
    }

    public void eksekusiQuery(String q) {
        if (!pastikanTerkoneksi()) {
            System.out.println("Query gagal. Database belum terhubung.");
            return;
        }

        try (Statement statement = koneksi.createStatement()) {
            statement.execute(q);
        } catch (SQLException e) {
            setError("Gagal menjalankan query: " + e.getMessage());
        }
    }

    public List<String> ambilSemuaNomorMeja() {
        List<String> hasil = new ArrayList<>();
        if (!pastikanTerkoneksi()) {
            return hasil;
        }

        try (PreparedStatement ps = koneksi.prepareStatement(
                "SELECT no_meja FROM meja ORDER BY no_meja");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(rs.getString("no_meja"));
            }
        } catch (SQLException e) {
            setError("Gagal memuat meja: " + e.getMessage());
        }
        return hasil;
    }

    public void simpanMeja(String noMeja) {
        if (!pastikanTerkoneksi()) {
            return;
        }

        try (PreparedStatement ps = koneksi.prepareStatement(
                "INSERT INTO meja (no_meja, qr_code, dibuat_pada) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE qr_code = VALUES(qr_code)")) {
            ps.setString(1, noMeja);
            ps.setString(2, "QR-" + noMeja);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            setError("Gagal menyimpan meja: " + e.getMessage());
        }
    }

    public List<Produk> ambilDaftarMenu() {
        List<Produk> hasil = new ArrayList<>();
        if (!pastikanTerkoneksi()) {
            return hasil;
        }

        try (PreparedStatement ps = koneksi.prepareStatement(
                "SELECT id_produk, nama_menu, harga_kupon, tipe, detail_tipe " +
                        "FROM menu WHERE aktif = 1 AND stok > 0 ORDER BY nama_menu");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(bentukProduk(
                        rs.getString("id_produk"),
                        rs.getString("nama_menu"),
                        rs.getInt("harga_kupon"),
                        rs.getString("tipe"),
                        rs.getString("detail_tipe")
                ));
            }
        } catch (SQLException e) {
            setError("Gagal memuat menu: " + e.getMessage());
        }
        return hasil;
    }

    public boolean tambahMenu(Produk produk) {
        return tambahMenu(produk, 30);
    }

    public boolean tambahMenu(Produk produk, int stokAwal) {
        if (!pastikanTerkoneksi()) {
            return false;
        }

        try (PreparedStatement ps = koneksi.prepareStatement(
                "INSERT INTO menu (id_produk, nama_menu, harga_kupon, tipe, detail_tipe, stok, aktif, dibuat_pada) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 1, ?)")) {
            ps.setString(1, produk.getIdProduk());
            ps.setString(2, produk.getNamaMenu());
            ps.setInt(3, produk.getHargaKupon());
            ps.setString(4, tipeProduk(produk));
            ps.setString(5, detailProduk(produk));
            ps.setInt(6, Math.max(0, stokAwal));
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            setError("Gagal menambah menu: " + e.getMessage());
            return false;
        }
    }

    public boolean simpanPesanan(String pelangganId, String noMeja, KuponDigital pesanan,
                                 String idPembayaran, String kodeQR, boolean statusBayar) {
        if (!pastikanTerkoneksi()) {
            return false;
        }

        String sqlPesanan = "INSERT INTO pesanan " +
                "(id_pesanan, pelanggan_id, no_meja, status_pesanan, total_kupon, total_bayar, nilai_kurs, " +
                "id_pembayaran, kode_qris, status_bayar, dibuat_pada, diubah_pada) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE pelanggan_id = VALUES(pelanggan_id), no_meja = VALUES(no_meja), " +
                "status_pesanan = VALUES(status_pesanan), total_kupon = VALUES(total_kupon), " +
                "total_bayar = VALUES(total_bayar), nilai_kurs = VALUES(nilai_kurs), " +
                "id_pembayaran = VALUES(id_pembayaran), kode_qris = VALUES(kode_qris), " +
                "status_bayar = VALUES(status_bayar), diubah_pada = VALUES(diubah_pada)";
        String hapusItem = "DELETE FROM item_pesanan WHERE id_pesanan = ?";
        String sqlItem = "INSERT INTO item_pesanan " +
                "(id_pesanan, id_produk, nama_menu, harga_kupon, jumlah, subtotal_kupon) VALUES (?, ?, ?, ?, ?, ?)";
        String kurangiStok = "UPDATE menu SET stok = stok - ? WHERE id_produk = ? AND stok >= ?";

        try {
            koneksi.setAutoCommit(false);
            validasiStokPesanan(pesanan);

            try (PreparedStatement psPesanan = koneksi.prepareStatement(sqlPesanan)) {
                psPesanan.setString(1, pesanan.getIdPesanan());
                psPesanan.setString(2, pelangganId);
                psPesanan.setString(3, noMeja);
                psPesanan.setString(4, pesanan.getStatusPesanan());
                psPesanan.setInt(5, pesanan.getTotalKuponDigital());
                psPesanan.setInt(6, (int) pesanan.konversiKeRupiah());
                psPesanan.setDouble(7, pesanan.getNilaiKursRupiah());
                psPesanan.setString(8, idPembayaran);
                psPesanan.setString(9, kodeQR);
                psPesanan.setBoolean(10, statusBayar);
                psPesanan.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
                psPesanan.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
                psPesanan.executeUpdate();
            }

            try (PreparedStatement psHapus = koneksi.prepareStatement(hapusItem)) {
                psHapus.setString(1, pesanan.getIdPesanan());
                psHapus.executeUpdate();
            }

            try (PreparedStatement psItem = koneksi.prepareStatement(sqlItem)) {
                for (ItemPesanan item : pesanan.getDaftarItem()) {
                    psItem.setString(1, pesanan.getIdPesanan());
                    psItem.setString(2, item.getProduk().getIdProduk());
                    psItem.setString(3, item.getProduk().getNamaMenu());
                    psItem.setInt(4, item.getProduk().getHargaKupon());
                    psItem.setInt(5, item.getJumlah());
                    psItem.setInt(6, item.ambilSubTotal());
                    psItem.addBatch();
                }
                psItem.executeBatch();
            }

            try (PreparedStatement psStok = koneksi.prepareStatement(kurangiStok)) {
                for (ItemPesanan item : pesanan.getDaftarItem()) {
                    psStok.setInt(1, item.getJumlah());
                    psStok.setString(2, item.getProduk().getIdProduk());
                    psStok.setInt(3, item.getJumlah());
                    if (psStok.executeUpdate() == 0) {
                        throw new SQLException("Stok " + item.getProduk().getNamaMenu() + " tidak cukup.");
                    }
                }
            }

            koneksi.commit();
            return true;
        } catch (SQLException e) {
            rollbackDiamDiam();
            setError("Gagal menyimpan pesanan: " + e.getMessage());
            return false;
        } finally {
            pulihkanAutoCommit();
        }
    }

    public boolean stokCukup(KuponDigital pesanan) {
        if (!pastikanTerkoneksi()) {
            return false;
        }

        try {
            validasiStokPesanan(pesanan);
            return true;
        } catch (SQLException e) {
            setError(e.getMessage());
            return false;
        }
    }

    public PesananTersimpan ambilPesananAktifByMeja(String noMeja) {
        return ambilPesanan("""
                SELECT id_pesanan, pelanggan_id, no_meja, status_pesanan, total_kupon, total_bayar,
                       nilai_kurs, id_pembayaran, kode_qris, status_bayar, dibuat_pada, diubah_pada
                FROM pesanan
                WHERE no_meja = ?
                  AND status_pesanan <> ?
                ORDER BY diubah_pada DESC
                LIMIT 1
                """, noMeja, STATUS_SELESAI);
    }

    public PesananTersimpan ambilPesananAktifTerbaru() {
        return ambilPesanan("""
                SELECT id_pesanan, pelanggan_id, no_meja, status_pesanan, total_kupon, total_bayar,
                       nilai_kurs, id_pembayaran, kode_qris, status_bayar, dibuat_pada, diubah_pada
                FROM pesanan
                WHERE status_pesanan <> ?
                ORDER BY diubah_pada DESC
                LIMIT 1
                """, STATUS_SELESAI);
    }

    public List<PesananTersimpan> ambilSemuaPesananAktif() {
        return ambilBanyakPesanan("""
                SELECT id_pesanan, pelanggan_id, no_meja, status_pesanan, total_kupon, total_bayar,
                       nilai_kurs, id_pembayaran, kode_qris, status_bayar, dibuat_pada, diubah_pada
                FROM pesanan
                WHERE status_pesanan <> ?
                ORDER BY dibuat_pada ASC
                """, STATUS_SELESAI);
    }

    public List<PesananTersimpan> ambilRiwayatPesanan(int limit) {
        return ambilBanyakPesanan("""
                SELECT id_pesanan, pelanggan_id, no_meja, status_pesanan, total_kupon, total_bayar,
                       nilai_kurs, id_pembayaran, kode_qris, status_bayar, dibuat_pada, diubah_pada
                FROM pesanan
                ORDER BY dibuat_pada DESC
                LIMIT ?
                """, String.valueOf(limit));
    }

    public PesananTersimpan ambilPesananById(String idPesanan) {
        return ambilPesanan("""
                SELECT id_pesanan, pelanggan_id, no_meja, status_pesanan, total_kupon, total_bayar,
                       nilai_kurs, id_pembayaran, kode_qris, status_bayar, dibuat_pada, diubah_pada
                FROM pesanan
                WHERE id_pesanan = ?
                LIMIT 1
                """, idPesanan);
    }

    public boolean updateStatusPesanan(String idPesanan, String statusBaru) {
        if (!pastikanTerkoneksi()) {
            return false;
        }

        try (PreparedStatement ps = koneksi.prepareStatement(
                "UPDATE pesanan SET status_pesanan = ?, diubah_pada = ? WHERE id_pesanan = ?")) {
            ps.setString(1, statusBaru);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3, idPesanan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            setError("Gagal update status pesanan: " + e.getMessage());
            return false;
        }
    }

    public boolean simpanRating(String idPesanan, int nilai, String ulasan) {
        if (!pastikanTerkoneksi()) {
            return false;
        }

        try {
            koneksi.setAutoCommit(false);

            try (PreparedStatement ps = koneksi.prepareStatement(
                    "INSERT INTO rating (id_rating, id_pesanan, nilai, ulasan, dibuat_pada) " +
                            "VALUES (?, ?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE nilai = VALUES(nilai), ulasan = VALUES(ulasan)")) {
                ps.setString(1, "RT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                ps.setString(2, idPesanan);
                ps.setInt(3, nilai);
                ps.setString(4, ulasan);
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                ps.executeUpdate();
            }

            try (PreparedStatement ps = koneksi.prepareStatement(
                    "UPDATE pesanan SET status_pesanan = ?, diubah_pada = ? WHERE id_pesanan = ?")) {
                ps.setString(1, STATUS_SELESAI);
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(3, idPesanan);
                ps.executeUpdate();
            }

            koneksi.commit();
            return true;
        } catch (SQLException e) {
            rollbackDiamDiam();
            setError("Gagal menyimpan rating: " + e.getMessage());
            return false;
        } finally {
            pulihkanAutoCommit();
        }
    }

    public List<ProdukStok> ambilSemuaMenuDenganStok() {
        List<ProdukStok> hasil = new ArrayList<>();
        if (!pastikanTerkoneksi()) {
            return hasil;
        }

        try (PreparedStatement ps = koneksi.prepareStatement(
                "SELECT id_produk, nama_menu, harga_kupon, tipe, detail_tipe, stok, aktif " +
                        "FROM menu ORDER BY nama_menu");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(new ProdukStok(
                        rs.getString("id_produk"),
                        rs.getString("nama_menu"),
                        rs.getInt("harga_kupon"),
                        rs.getString("tipe"),
                        rs.getString("detail_tipe"),
                        rs.getInt("stok"),
                        rs.getBoolean("aktif")
                ));
            }
        } catch (SQLException e) {
            setError("Gagal memuat stok menu: " + e.getMessage());
        }
        return hasil;
    }

    public boolean updateStokProduk(String idProduk, int stokBaru) {
        if (!pastikanTerkoneksi()) {
            return false;
        }

        try (PreparedStatement ps = koneksi.prepareStatement(
                "UPDATE menu SET stok = ? WHERE id_produk = ?")) {
            ps.setInt(1, Math.max(0, stokBaru));
            ps.setString(2, idProduk);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            setError("Gagal update stok produk: " + e.getMessage());
            return false;
        }
    }

    public List<String> ambilRiwayatMeja(String noMeja, int limit) {
        List<String> hasil = new ArrayList<>();
        if (!pastikanTerkoneksi()) {
            return hasil;
        }

        try (PreparedStatement ps = koneksi.prepareStatement(
                "SELECT id_pesanan, status_pesanan, total_bayar, dibuat_pada " +
                        "FROM pesanan WHERE no_meja = ? ORDER BY dibuat_pada DESC LIMIT ?")) {
            ps.setString(1, noMeja);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(rs.getString("id_pesanan")
                            + " | " + rs.getString("status_pesanan")
                            + " | Rp" + rs.getInt("total_bayar")
                            + " | " + rs.getTimestamp("dibuat_pada").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            setError("Gagal memuat riwayat meja: " + e.getMessage());
        }
        return hasil;
    }

    public String getPesanKesalahanTerakhir() {
        return pesanKesalahanTerakhir;
    }

    public boolean isStatusKoneksi() {
        return statusKoneksi;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getNamaDB() {
        return namaDB;
    }

    private PesananTersimpan ambilPesanan(String sql, String... params) {
        if (!pastikanTerkoneksi()) {
            return null;
        }

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                KuponDigital pesanan = new KuponDigital(
                        rs.getString("id_pesanan"),
                        rs.getString("status_pesanan"),
                        rs.getDouble("nilai_kurs")
                );
                for (ItemPesanan item : ambilItemPesanan(rs.getString("id_pesanan"))) {
                    pesanan.tambahMenu(item);
                }

                return new PesananTersimpan(
                        rs.getString("pelanggan_id"),
                        rs.getString("no_meja"),
                        pesanan,
                        rs.getString("id_pembayaran"),
                        rs.getString("kode_qris"),
                        rs.getBoolean("status_bayar"),
                        rs.getTimestamp("dibuat_pada").toLocalDateTime(),
                        rs.getTimestamp("diubah_pada").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            setError("Gagal memuat pesanan: " + e.getMessage());
            return null;
        }
    }

    private List<PesananTersimpan> ambilBanyakPesanan(String sql, String... params) {
        List<PesananTersimpan> hasil = new ArrayList<>();
        if (!pastikanTerkoneksi()) {
            return hasil;
        }

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                if (params[i].matches("\\d+")) {
                    ps.setInt(i + 1, Integer.parseInt(params[i]));
                } else {
                    ps.setString(i + 1, params[i]);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    KuponDigital pesanan = new KuponDigital(
                            rs.getString("id_pesanan"),
                            rs.getString("status_pesanan"),
                            rs.getDouble("nilai_kurs")
                    );
                    for (ItemPesanan item : ambilItemPesanan(rs.getString("id_pesanan"))) {
                        pesanan.tambahMenu(item);
                    }

                    hasil.add(new PesananTersimpan(
                            rs.getString("pelanggan_id"),
                            rs.getString("no_meja"),
                            pesanan,
                            rs.getString("id_pembayaran"),
                            rs.getString("kode_qris"),
                            rs.getBoolean("status_bayar"),
                            rs.getTimestamp("dibuat_pada").toLocalDateTime(),
                            rs.getTimestamp("diubah_pada").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            setError("Gagal memuat daftar pesanan: " + e.getMessage());
        }
        return hasil;
    }

    private List<ItemPesanan> ambilItemPesanan(String idPesanan) {
        List<ItemPesanan> hasil = new ArrayList<>();
        try (PreparedStatement ps = koneksi.prepareStatement(
                "SELECT ip.id_produk, ip.nama_menu, ip.harga_kupon, ip.jumlah, m.tipe, m.detail_tipe " +
                        "FROM item_pesanan ip " +
                        "LEFT JOIN menu m ON m.id_produk = ip.id_produk " +
                        "WHERE ip.id_pesanan = ? ORDER BY ip.id ASC")) {
            ps.setString(1, idPesanan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tipe = rs.getString("tipe");
                    if (tipe == null || tipe.isBlank()) {
                        tipe = "Makanan";
                    }
                    Produk produk = bentukProduk(
                            rs.getString("id_produk"),
                            rs.getString("nama_menu"),
                            rs.getInt("harga_kupon"),
                            tipe,
                            rs.getString("detail_tipe")
                    );
                    hasil.add(new ItemPesanan(produk, rs.getInt("jumlah")));
                }
            }
        } catch (SQLException e) {
            setError("Gagal memuat item pesanan: " + e.getMessage());
        }
        return hasil;
    }

    private void validasiStokPesanan(KuponDigital pesanan) throws SQLException {
        try (PreparedStatement ps = koneksi.prepareStatement(
                "SELECT stok FROM menu WHERE id_produk = ? AND aktif = 1")) {
            for (ItemPesanan item : pesanan.getDaftarItem()) {
                ps.setString(1, item.getProduk().getIdProduk());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Produk " + item.getProduk().getNamaMenu() + " tidak ditemukan di database.");
                    }
                    int stok = rs.getInt("stok");
                    if (stok < item.getJumlah()) {
                        throw new SQLException("Stok " + item.getProduk().getNamaMenu()
                                + " tidak cukup. Tersedia " + stok + ", diminta " + item.getJumlah() + ".");
                    }
                }
            }
        }
    }

    private Produk bentukProduk(String idProduk, String namaMenu, int hargaKupon, String tipe, String detail) {
        if ("Minuman".equalsIgnoreCase(tipe)) {
            boolean dingin = detail == null || !detail.toLowerCase().contains("panas");
            return new Minuman(idProduk, namaMenu, hargaKupon, dingin);
        }
        String catatan = detail == null || detail.isBlank() ? "Menu tenant" : detail;
        return new Makanan(idProduk, namaMenu, hargaKupon, catatan);
    }

    private String tipeProduk(Produk produk) {
        return produk instanceof Minuman ? "Minuman" : "Makanan";
    }

    private String detailProduk(Produk produk) {
        if (produk instanceof Makanan makanan) {
            return makanan.getCatatan();
        }
        if (produk instanceof Minuman minuman) {
            return minuman.isDingin() ? "Dingin" : "Panas";
        }
        return "";
    }

    private boolean pastikanTerkoneksi() {
        if (statusKoneksi && koneksi != null) {
            return true;
        }
        return hubungkan();
    }

    private void inisialisasiSchema() throws SQLException {
        try (Statement statement = koneksi.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS meja (
                        no_meja VARCHAR(20) PRIMARY KEY,
                        qr_code VARCHAR(50) NOT NULL,
                        dibuat_pada DATETIME NOT NULL
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS menu (
                        id_produk VARCHAR(30) PRIMARY KEY,
                        nama_menu VARCHAR(100) NOT NULL,
                        harga_kupon INT NOT NULL,
                        tipe VARCHAR(20) NOT NULL,
                        detail_tipe VARCHAR(255),
                        stok INT NOT NULL DEFAULT 30,
                        aktif TINYINT(1) NOT NULL DEFAULT 1,
                        dibuat_pada DATETIME NOT NULL
                    )
                    """);
            pastikanKolomMenuStok();

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS pesanan (
                        id_pesanan VARCHAR(30) PRIMARY KEY,
                        pelanggan_id VARCHAR(30) NOT NULL,
                        no_meja VARCHAR(20) NOT NULL,
                        status_pesanan VARCHAR(40) NOT NULL,
                        total_kupon INT NOT NULL,
                        total_bayar INT NOT NULL,
                        nilai_kurs DOUBLE NOT NULL DEFAULT 5000,
                        id_pembayaran VARCHAR(40),
                        kode_qris VARCHAR(100),
                        status_bayar TINYINT(1) NOT NULL DEFAULT 0,
                        dibuat_pada DATETIME NOT NULL,
                        diubah_pada DATETIME NOT NULL,
                        CONSTRAINT fk_pesanan_meja FOREIGN KEY (no_meja) REFERENCES meja(no_meja)
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS item_pesanan (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        id_pesanan VARCHAR(30) NOT NULL,
                        id_produk VARCHAR(30) NOT NULL,
                        nama_menu VARCHAR(100) NOT NULL,
                        harga_kupon INT NOT NULL,
                        jumlah INT NOT NULL,
                        subtotal_kupon INT NOT NULL,
                        CONSTRAINT fk_item_pesanan FOREIGN KEY (id_pesanan) REFERENCES pesanan(id_pesanan) ON DELETE CASCADE
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS rating (
                        id_rating VARCHAR(30) PRIMARY KEY,
                        id_pesanan VARCHAR(30) NOT NULL UNIQUE,
                        nilai INT NOT NULL,
                        ulasan TEXT,
                        dibuat_pada DATETIME NOT NULL,
                        CONSTRAINT fk_rating_pesanan FOREIGN KEY (id_pesanan) REFERENCES pesanan(id_pesanan)
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS log_aplikasi (
                        id_log VARCHAR(30) PRIMARY KEY,
                        pesan_log TEXT NOT NULL,
                        dibuat_pada DATETIME NOT NULL
                    )
                    """);
        }
    }

    private void seedDataAwal() throws SQLException {
        seedMeja();
        seedMenu();
    }

    private void seedMeja() throws SQLException {
        try (PreparedStatement cek = koneksi.prepareStatement("SELECT COUNT(*) FROM meja");
             ResultSet rs = cek.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        try (PreparedStatement ps = koneksi.prepareStatement(
                "INSERT INTO meja (no_meja, qr_code, dibuat_pada) VALUES (?, ?, ?)")) {
            for (int i = 1; i <= 5; i++) {
                String noMeja = String.format("WG-M-%02d", i);
                ps.setString(1, noMeja);
                ps.setString(2, "QR-" + noMeja);
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void seedMenu() throws SQLException {
        try (PreparedStatement cek = koneksi.prepareStatement("SELECT COUNT(*) FROM menu");
             ResultSet rs = cek.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        List<Produk> defaultMenu = List.of(
                new Makanan("MKN-001", "Ayam Geprek", 3, "Level pedas bisa request"),
                new Makanan("MKN-002", "Nasi Goreng", 4, "Telur mata sapi"),
                new Makanan("MKN-003", "Mie Ayam", 3, "Pangsit kering"),
                new Makanan("MKN-004", "Soto Ayam", 4, "Kuah hangat"),
                new Minuman("MNM-001", "Es Teh", 1, true),
                new Minuman("MNM-002", "Kopi Susu", 2, true),
                new Minuman("MNM-003", "Air Mineral", 1, false)
        );

        try (PreparedStatement ps = koneksi.prepareStatement(
                "INSERT INTO menu (id_produk, nama_menu, harga_kupon, tipe, detail_tipe, stok, aktif, dibuat_pada) " +
                        "VALUES (?, ?, ?, ?, ?, 30, 1, ?)")) {
            for (Produk produk : defaultMenu) {
                ps.setString(1, produk.getIdProduk());
                ps.setString(2, produk.getNamaMenu());
                ps.setInt(3, produk.getHargaKupon());
                ps.setString(4, tipeProduk(produk));
                ps.setString(5, detailProduk(produk));
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void pastikanKolomMenuStok() throws SQLException {
        try (PreparedStatement ps = koneksi.prepareStatement("SHOW COLUMNS FROM menu LIKE 'stok'");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return;
            }
        }

        try (Statement statement = koneksi.createStatement()) {
            statement.execute("ALTER TABLE menu ADD COLUMN stok INT NOT NULL DEFAULT 30 AFTER detail_tipe");
        }
    }

    private void rollbackDiamDiam() {
        if (koneksi == null) {
            return;
        }
        try {
            koneksi.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void pulihkanAutoCommit() {
        if (koneksi == null) {
            return;
        }
        try {
            koneksi.setAutoCommit(true);
        } catch (SQLException ignored) {
        }
    }

    private void setError(String pesan) {
        pesanKesalahanTerakhir = pesan;
        System.out.println(pesan);
    }

    private boolean muatDriverMySQL() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return true;
        } catch (ClassNotFoundException eBaru) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                return true;
            } catch (ClassNotFoundException eLama) {
                setError("Driver JDBC MySQL belum ditemukan di classpath. " +
                        "Tambahkan dependency `mysql-connector-j` atau jalankan project sebagai Maven.");
                return false;
            }
        }
    }

    private static String bacaEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static int bacaPort(String key, int defaultPort) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            return defaultPort;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return defaultPort;
        }
    }

    public static class ProdukStok {
        private final String idProduk;
        private final String namaMenu;
        private final int hargaKupon;
        private final String tipe;
        private final String detail;
        private final int stok;
        private final boolean aktif;

        public ProdukStok(String idProduk, String namaMenu, int hargaKupon, String tipe,
                          String detail, int stok, boolean aktif) {
            this.idProduk = idProduk;
            this.namaMenu = namaMenu;
            this.hargaKupon = hargaKupon;
            this.tipe = tipe;
            this.detail = detail;
            this.stok = stok;
            this.aktif = aktif;
        }

        public String getIdProduk() {
            return idProduk;
        }

        public String getNamaMenu() {
            return namaMenu;
        }

        public int getHargaKupon() {
            return hargaKupon;
        }

        public String getTipe() {
            return tipe;
        }

        public String getDetail() {
            return detail;
        }

        public int getStok() {
            return stok;
        }

        public boolean isAktif() {
            return aktif;
        }

        @Override
        public String toString() {
            return idProduk + " - " + namaMenu + " (stok " + stok + ")";
        }
    }

    public static class PesananTersimpan {
        private final String pelangganId;
        private final String noMeja;
        private final KuponDigital pesanan;
        private final String idPembayaran;
        private final String kodeQris;
        private final boolean statusBayar;
        private final LocalDateTime dibuatPada;
        private final LocalDateTime diubahPada;

        public PesananTersimpan(String pelangganId, String noMeja, KuponDigital pesanan,
                                String idPembayaran, String kodeQris, boolean statusBayar,
                                LocalDateTime dibuatPada, LocalDateTime diubahPada) {
            this.pelangganId = pelangganId;
            this.noMeja = noMeja;
            this.pesanan = pesanan;
            this.idPembayaran = idPembayaran;
            this.kodeQris = kodeQris;
            this.statusBayar = statusBayar;
            this.dibuatPada = dibuatPada;
            this.diubahPada = diubahPada;
        }

        public String getPelangganId() {
            return pelangganId;
        }

        public String getNoMeja() {
            return noMeja;
        }

        public KuponDigital getPesanan() {
            return pesanan;
        }

        public String getIdPembayaran() {
            return idPembayaran;
        }

        public String getKodeQris() {
            return kodeQris;
        }

        public boolean isStatusBayar() {
            return statusBayar;
        }

        public LocalDateTime getDibuatPada() {
            return dibuatPada;
        }

        public LocalDateTime getDiubahPada() {
            return diubahPada;
        }
    }
}
