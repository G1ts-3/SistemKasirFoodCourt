CREATE DATABASE IF NOT EXISTS wg58_foodcourt;
USE wg58_foodcourt;

CREATE TABLE IF NOT EXISTS meja (
    no_meja VARCHAR(20) PRIMARY KEY,
    qr_code VARCHAR(50) NOT NULL,
    dibuat_pada DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS menu (
    id_produk VARCHAR(30) PRIMARY KEY,
    nama_menu VARCHAR(100) NOT NULL,
    harga_kupon INT NOT NULL,
    tipe VARCHAR(20) NOT NULL,
    detail_tipe VARCHAR(255),
    stok INT NOT NULL DEFAULT 30,
    aktif TINYINT(1) NOT NULL DEFAULT 1,
    dibuat_pada DATETIME NOT NULL
);

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
);

CREATE TABLE IF NOT EXISTS item_pesanan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pesanan VARCHAR(30) NOT NULL,
    id_produk VARCHAR(30) NOT NULL,
    nama_menu VARCHAR(100) NOT NULL,
    harga_kupon INT NOT NULL,
    jumlah INT NOT NULL,
    subtotal_kupon INT NOT NULL,
    CONSTRAINT fk_item_pesanan FOREIGN KEY (id_pesanan) REFERENCES pesanan(id_pesanan) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rating (
    id_rating VARCHAR(30) PRIMARY KEY,
    id_pesanan VARCHAR(30) NOT NULL UNIQUE,
    nilai INT NOT NULL,
    ulasan TEXT,
    dibuat_pada DATETIME NOT NULL,
    CONSTRAINT fk_rating_pesanan FOREIGN KEY (id_pesanan) REFERENCES pesanan(id_pesanan)
);

CREATE TABLE IF NOT EXISTS log_aplikasi (
    id_log VARCHAR(30) PRIMARY KEY,
    pesan_log TEXT NOT NULL,
    dibuat_pada DATETIME NOT NULL
);

INSERT IGNORE INTO meja (no_meja, qr_code, dibuat_pada) VALUES
('WG-M-01', 'QR-WG-M-01', NOW()),
('WG-M-02', 'QR-WG-M-02', NOW()),
('WG-M-03', 'QR-WG-M-03', NOW()),
('WG-M-04', 'QR-WG-M-04', NOW()),
('WG-M-05', 'QR-WG-M-05', NOW());

SET @sql_tambah_stok = IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'menu' AND COLUMN_NAME = 'stok') = 0,
    'ALTER TABLE menu ADD COLUMN stok INT NOT NULL DEFAULT 30 AFTER detail_tipe',
    'SELECT 1'
);
PREPARE stmt_tambah_stok FROM @sql_tambah_stok;
EXECUTE stmt_tambah_stok;
DEALLOCATE PREPARE stmt_tambah_stok;

INSERT IGNORE INTO menu (id_produk, nama_menu, harga_kupon, tipe, detail_tipe, stok, aktif, dibuat_pada) VALUES
('MKN-001', 'Ayam Geprek', 3, 'Makanan', 'Level pedas bisa request', 30, 1, NOW()),
('MKN-002', 'Nasi Goreng', 4, 'Makanan', 'Telur mata sapi', 30, 1, NOW()),
('MKN-003', 'Mie Ayam', 3, 'Makanan', 'Pangsit kering', 30, 1, NOW()),
('MKN-004', 'Soto Ayam', 4, 'Makanan', 'Kuah hangat', 30, 1, NOW()),
('MNM-001', 'Es Teh', 1, 'Minuman', 'Dingin', 30, 1, NOW()),
('MNM-002', 'Kopi Susu', 2, 'Minuman', 'Dingin', 30, 1, NOW()),
('MNM-003', 'Air Mineral', 1, 'Minuman', 'Panas', 30, 1, NOW());
