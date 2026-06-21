package WG58.app;

import WG58.database.DatabaseInitializer;
import WG58.manajemen.ManajemenMenu;
import WG58.manajemen.ManajemenTenant;
import WG58.menu.Makanan;
import WG58.menu.Menu;
import WG58.menu.Minuman;
import WG58.menu.StokMenu;
import WG58.pembayaran.PembayaranQRIS;
import WG58.pengguna.Meja;
import WG58.pengguna.Pelanggan;
import WG58.pengguna.Tenant;
import WG58.pesanan.ItemPesanan;
import WG58.pesanan.Pesanan;
import WG58.pesanan.Rating;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * MainGUI adalah tampilan Java Swing sederhana untuk Sistem Kasir Food Court.
 *
 * Cara menggunakan:
 * 1. Jalankan class Main atau MainGUI.
 * 2. Pilih mode Pelanggan untuk memilih tenant, meja, menu, checkout, ambil pesanan,
 *    dan memberi rating.
 * 3. Pilih mode Tenant untuk login, melihat pesanan, mengubah status pesanan,
 *    menambah menu, dan mengubah stok.
 *
 * File ini sengaja memakai komponen Swing dasar saja: JFrame, JPanel, JButton,
 * JTable, JTextField, JComboBox, JSpinner, dan JTabbedPane. Tidak ada warna custom,
 * ikon, atau styling khusus agar lebih mudah dipahami dan dijelaskan.
 */
public class MainGUI extends JFrame {
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 28);
    private static final Font FONT_PAGE_TITLE = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 15);
    private static final Font FONT_TABLE = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_TABLE_HEADER = new Font("SansSerif", Font.BOLD, 14);
    private static final int GAP = 12;

    private final ManajemenTenant manajemenTenant;
    private final ManajemenMenu manajemenMenu;
    private final Pelanggan pelanggan;
    private final ArrayList<ItemPesanan> keranjang = new ArrayList<>();

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private Tenant tenantDipilih;
    private Tenant tenantLogin;
    private Meja mejaDipilih;

    private DefaultTableModel modelTenant;
    private DefaultTableModel modelMenu;
    private DefaultTableModel modelKeranjang;
    private DefaultTableModel modelPesananPelanggan;
    private DefaultTableModel modelAmbil;
    private DefaultTableModel modelRatingPelanggan;
    private DefaultTableModel modelPesananTenant;
    private DefaultTableModel modelRiwayatTenant;
    private DefaultTableModel modelRatingTenant;
    private DefaultTableModel modelMenuTenant;
    private DefaultTableModel modelStokTenant;

    private JTable tabelTenant;
    private JTable tabelMenu;
    private JTable tabelKeranjang;
    private JTable tabelPesananPelanggan;
    private JTable tabelAmbil;
    private JTable tabelRatingPelanggan;
    private JTable tabelPesananTenant;
    private JTable tabelStokTenant;

    private JLabel labelTotalKeranjang;
    private JLabel labelTenantDipilih;
    private JLabel labelTenantLogin;

    public MainGUI() {
        DatabaseInitializer.initialize();

        manajemenTenant = new ManajemenTenant();
        manajemenMenu = new ManajemenMenu();
        pelanggan = new Pelanggan("P01", "Pelanggan");

        setTitle("WG58 Food Court - Sistem Kasir");
        setSize(1020, 700);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buatPanelUtama();
        aturTampilanKomponen(mainPanel);
        pindahPanel("HOME");
    }

    /**
     * CardLayout dipakai untuk pindah halaman tanpa membuka banyak JFrame.
     */
    private void buatPanelUtama() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(buatPanelHome(), "HOME");
        mainPanel.add(buatPanelPilihTenant(), "PILIH_TENANT");
        mainPanel.add(buatPanelPelanggan(), "PELANGGAN");
        mainPanel.add(buatPanelLoginTenant(), "LOGIN_TENANT");
        mainPanel.add(buatPanelTenant(), "TENANT");

        add(mainPanel);
    }

    /**
     * Method navigasi juga menjalankan refresh tabel agar data terbaru terlihat.
     */
    private void pindahPanel(String namaPanel) {
        if ("PILIH_TENANT".equals(namaPanel)) {
            isiTabelTenant();
        } else if ("PELANGGAN".equals(namaPanel)) {
            isiSemuaTabelPelanggan();
        } else if ("TENANT".equals(namaPanel)) {
            isiSemuaTabelTenant();
        }
        cardLayout.show(mainPanel, namaPanel);
    }

    private JPanel buatPanelHome() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel kotakTengah = new JPanel(new GridBagLayout());
        kotakTengah.setBorder(new EmptyBorder(36, 48, 36, 48));

        JLabel judul = new JLabel("WG58 Food Court", JLabel.CENTER);
        judul.setFont(FONT_TITLE);
        JLabel subjudul = new JLabel("Sistem Kasir Food Court", JLabel.CENTER);
        subjudul.setFont(FONT_NORMAL);
        JButton tombolPelanggan = new JButton("Masuk sebagai Pelanggan");
        JButton tombolTenant = new JButton("Login sebagai Tenant");
        tombolPelanggan.setPreferredSize(new Dimension(260, 44));
        tombolTenant.setPreferredSize(new Dimension(260, 44));

        tombolPelanggan.addActionListener(e -> {
            pelanggan.aksesSistem();
            pindahPanel("PILIH_TENANT");
        });
        tombolTenant.addActionListener(e -> pindahPanel("LOGIN_TENANT"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, GAP, 0);
        kotakTengah.add(judul, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 28, 0);
        kotakTengah.add(subjudul, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, GAP, 0);
        kotakTengah.add(tombolPelanggan, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        kotakTengah.add(tombolTenant, gbc);

        panel.add(kotakTengah);

        return panel;
    }

    /**
     * Pelanggan memilih tenant dan memasukkan nomor meja sebelum melihat menu.
     */
    private JPanel buatPanelPilihTenant() {
        JPanel panel = panelBerpadding(new BorderLayout(GAP, GAP));
        panel.add(header("Pilih Tenant", e -> pindahPanel("HOME")), BorderLayout.NORTH);

        modelTenant = modelTabel("No", "ID Tenant", "Nama Tenant", "Jumlah Menu");
        tabelTenant = new JTable(modelTenant);
        panel.add(new JScrollPane(tabelTenant), BorderLayout.CENTER);

        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.LEFT, GAP, GAP));
        bawah.setBorder(new EmptyBorder(4, 0, 0, 0));
        JTextField inputMeja = new JTextField(10);
        JButton tombolLanjut = new JButton("Lanjut");
        tombolLanjut.setPreferredSize(new Dimension(110, 36));

        tombolLanjut.addActionListener(e -> {
            int row = tabelTenant.getSelectedRow();
            if (row < 0) {
                tampilError("Pilih tenant terlebih dahulu.");
                return;
            }
            if (inputMeja.getText().trim().isEmpty()) {
                tampilError("Nomor meja harus diisi.");
                return;
            }

            ArrayList<Tenant> daftarTenant = manajemenTenant.getDaftarTenant();
            tenantDipilih = daftarTenant.get(row);
            mejaDipilih = new Meja(inputMeja.getText().trim());
            keranjang.clear();
            pindahPanel("PELANGGAN");
        });

        bawah.add(new JLabel("Nomor meja:"));
        bawah.add(inputMeja);
        bawah.add(tombolLanjut);
        panel.add(bawah, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buatPanelPelanggan() {
        JPanel panel = panelBerpadding(new BorderLayout(GAP, GAP));
        labelTenantDipilih = new JLabel("Pelanggan");
        panel.add(headerDenganLabel(labelTenantDipilih, e -> pindahPanel("PILIH_TENANT")), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Pesan", buatTabPesan());
        tabs.addTab("Status", buatTabStatusPelanggan());
        tabs.addTab("Ambil", buatTabAmbil());
        tabs.addTab("Rating", buatTabRatingPelanggan());
        tabs.addChangeListener(e -> isiSemuaTabelPelanggan());

        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Tab Pesan berisi tabel menu, form jumlah/catatan, dan tabel keranjang.
     */
    private JPanel buatTabPesan() {
        JPanel panel = panelBerpadding(new BorderLayout(GAP, GAP));

        modelMenu = modelTabel("No", "ID", "Nama", "Jenis", "Kupon", "Stok");
        tabelMenu = new JTable(modelMenu);
        panel.add(new JScrollPane(tabelMenu), BorderLayout.CENTER);

        modelKeranjang = modelTabel("No", "Nama", "Jumlah", "Catatan", "Subtotal Kupon");
        tabelKeranjang = new JTable(modelKeranjang);

        JPanel kanan = new JPanel(new BorderLayout());
        kanan.setPreferredSize(new Dimension(380, 0));
        kanan.setBorder(new EmptyBorder(0, GAP, 0, 0));
        kanan.add(new JLabel("Keranjang"), BorderLayout.NORTH);
        kanan.add(new JScrollPane(tabelKeranjang), BorderLayout.CENTER);
        panel.add(kanan, BorderLayout.EAST);

        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.LEFT, GAP, GAP));
        JSpinner inputJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        JTextField inputCatatan = new JTextField(12);
        JButton tombolTambah = new JButton("Tambah");
        JButton tombolHapus = new JButton("Hapus Item");
        JButton tombolCheckout = new JButton("Checkout");
        labelTotalKeranjang = new JLabel("Total: 0 kupon");

        tombolTambah.addActionListener(e -> tambahKeKeranjang(inputJumlah, inputCatatan));
        tombolHapus.addActionListener(e -> hapusDariKeranjang());
        tombolCheckout.addActionListener(e -> checkout());

        bawah.add(new JLabel("Jumlah:"));
        bawah.add(inputJumlah);
        bawah.add(new JLabel("Catatan:"));
        bawah.add(inputCatatan);
        bawah.add(tombolTambah);
        bawah.add(tombolHapus);
        bawah.add(tombolCheckout);
        bawah.add(labelTotalKeranjang);
        panel.add(bawah, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buatTabStatusPelanggan() {
        JPanel panel = panelBerpadding(new BorderLayout(GAP, GAP));
        modelPesananPelanggan = modelTabel("ID", "Meja", "Status", "Total Kupon", "Total Rupiah");
        tabelPesananPelanggan = new JTable(modelPesananPelanggan);
        panel.add(new JScrollPane(tabelPesananPelanggan), BorderLayout.CENTER);

        JButton tombolRefresh = new JButton("Refresh");
        tombolRefresh.addActionListener(e -> isiTabelPesananPelanggan());
        panel.add(panelTombol(tombolRefresh), BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Pesanan hanya bisa diambil jika statusnya sudah "Siap Diambil".
     */
    private JPanel buatTabAmbil() {
        JPanel panel = panelBerpadding(new BorderLayout(GAP, GAP));
        modelAmbil = modelTabel("ID", "Meja", "Status", "Total Rupiah");
        tabelAmbil = new JTable(modelAmbil);
        panel.add(new JScrollPane(tabelAmbil), BorderLayout.CENTER);

        JButton tombolAmbil = new JButton("Ambil Pesanan");
        tombolAmbil.addActionListener(e -> ambilPesanan());
        panel.add(panelTombol(tombolAmbil), BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Rating disimpan ke database untuk pesanan yang sudah selesai.
     */
    private JPanel buatTabRatingPelanggan() {
        JPanel panel = panelBerpadding(new BorderLayout(GAP, GAP));
        modelRatingPelanggan = modelTabel("ID", "Meja", "Status", "Sudah Rating");
        tabelRatingPelanggan = new JTable(modelRatingPelanggan);
        panel.add(new JScrollPane(tabelRatingPelanggan), BorderLayout.CENTER);

        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.LEFT, GAP, GAP));
        JSpinner inputNilai = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        JTextField inputUlasan = new JTextField(20);
        JButton tombolRating = new JButton("Simpan Rating");

        tombolRating.addActionListener(e -> simpanRating(inputNilai, inputUlasan));

        bawah.add(new JLabel("Nilai:"));
        bawah.add(inputNilai);
        bawah.add(new JLabel("Ulasan:"));
        bawah.add(inputUlasan);
        bawah.add(tombolRating);
        panel.add(bawah, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buatPanelLoginTenant() {
        JPanel panel = panelBerpadding(new BorderLayout(GAP, GAP));
        panel.add(header("Login Tenant", e -> pindahPanel("HOME")), BorderLayout.NORTH);

        JPanel tengah = new JPanel(new GridBagLayout());
        JPanel form = new JPanel(new GridLayout(0, 2, GAP, GAP));
        form.setBorder(new EmptyBorder(32, 40, 32, 40));
        form.setPreferredSize(new Dimension(430, 230));
        JTextField inputUsername = new JTextField();
        JPasswordField inputPassword = new JPasswordField();
        JButton tombolLogin = new JButton("Login");

        tombolLogin.addActionListener(e -> {
            String username = inputUsername.getText().trim();
            String password = new String(inputPassword.getPassword());
            tenantLogin = manajemenTenant.loginTenant(username, password);

            if (tenantLogin == null) {
                tampilError("Login gagal. Periksa username dan password.");
                return;
            }

            tenantLogin.aksesSistem();
            inputPassword.setText("");
            pindahPanel("TENANT");
        });

        form.add(new JLabel("Username:"));
        form.add(inputUsername);
        form.add(new JLabel("Password:"));
        form.add(inputPassword);
        form.add(new JLabel(""));
        form.add(tombolLogin);
        tengah.add(form);
        panel.add(tengah, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buatPanelTenant() {
        JPanel panel = panelBerpadding(new BorderLayout(GAP, GAP));
        labelTenantLogin = new JLabel("Tenant");
        panel.add(headerDenganLabel(labelTenantLogin, e -> {
            tenantLogin = null;
            pindahPanel("HOME");
        }), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Pesanan", buatTabPesananTenant());
        tabs.addTab("Riwayat", buatTabRiwayatTenant());
        tabs.addTab("Rating", buatTabRatingTenant());
        tabs.addTab("Tambah Menu", buatTabTambahMenu());
        tabs.addTab("Stok", buatTabStokTenant());
        tabs.addChangeListener(e -> isiSemuaTabelTenant());

        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Tenant memilih pesanan lalu menekan tombol status yang sesuai.
     */
    private JPanel buatTabPesananTenant() {
        JPanel panel = panelBerpadding(new BorderLayout(GAP, GAP));
        modelPesananTenant = modelTabel("ID", "Meja", "Status", "Total Kupon", "Total Rupiah");
        tabelPesananTenant = new JTable(modelPesananTenant);
        panel.add(new JScrollPane(tabelPesananTenant), BorderLayout.CENTER);

        JButton tombolDiproses = new JButton("Ubah ke Diproses");
        JButton tombolSiap = new JButton("Ubah ke Siap Diambil");
        JButton tombolRefresh = new JButton("Refresh");
        tombolDiproses.addActionListener(e -> ubahStatusTenant("Diproses"));
        tombolSiap.addActionListener(e -> ubahStatusTenant("Siap Diambil"));
        tombolRefresh.addActionListener(e -> isiTabelPesananTenant());

        panel.add(panelTombol(tombolDiproses, tombolSiap, tombolRefresh), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buatTabRiwayatTenant() {
        JPanel panel = panelBerpadding(new BorderLayout(GAP, GAP));
        modelRiwayatTenant = modelTabel("ID", "Meja", "Status", "Total Kupon", "Total Rupiah");
        panel.add(new JScrollPane(new JTable(modelRiwayatTenant)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buatTabRatingTenant() {
        JPanel panel = panelBerpadding(new BorderLayout(GAP, GAP));
        modelRatingTenant = modelTabel("ID Pesanan", "Meja", "Nilai", "Ulasan");
        panel.add(new JScrollPane(new JTable(modelRatingTenant)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buatTabTambahMenu() {
        JPanel panel = panelBerpadding(new BorderLayout(GAP, GAP));

        JPanel form = new JPanel(new GridLayout(0, 2, GAP, GAP));
        form.setBorder(new EmptyBorder(0, 0, GAP, 0));
        JComboBox<String> inputJenis = new JComboBox<>(new String[]{"Makanan", "Minuman"});
        JTextField inputId = new JTextField();
        JTextField inputNama = new JTextField();
        JSpinner inputHarga = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        JSpinner inputStok = new JSpinner(new SpinnerNumberModel(10, 0, 9999, 1));
        JButton tombolTambah = new JButton("Tambah Menu");

        tombolTambah.addActionListener(e -> tambahMenuTenant(inputJenis, inputId, inputNama, inputHarga, inputStok));

        form.add(new JLabel("Jenis:"));
        form.add(inputJenis);
        form.add(new JLabel("ID Menu:"));
        form.add(inputId);
        form.add(new JLabel("Nama Menu:"));
        form.add(inputNama);
        form.add(new JLabel("Harga Kupon:"));
        form.add(inputHarga);
        form.add(new JLabel("Stok Awal:"));
        form.add(inputStok);
        form.add(new JLabel(""));
        form.add(tombolTambah);
        panel.add(form, BorderLayout.NORTH);

        modelMenuTenant = modelTabel("ID", "Nama", "Jenis", "Kupon");
        panel.add(new JScrollPane(new JTable(modelMenuTenant)), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buatTabStokTenant() {
        JPanel panel = panelBerpadding(new BorderLayout(GAP, GAP));
        modelStokTenant = modelTabel("No", "ID", "Nama", "Jenis", "Kupon", "Stok");
        tabelStokTenant = new JTable(modelStokTenant);
        panel.add(new JScrollPane(tabelStokTenant), BorderLayout.CENTER);

        JSpinner inputStokBaru = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        JButton tombolUpdate = new JButton("Update Stok");
        tombolUpdate.addActionListener(e -> updateStokTenant(inputStokBaru));

        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.LEFT, GAP, GAP));
        bawah.add(new JLabel("Stok baru:"));
        bawah.add(inputStokBaru);
        bawah.add(tombolUpdate);
        panel.add(bawah, BorderLayout.SOUTH);

        return panel;
    }

    private void isiTabelTenant() {
        modelTenant.setRowCount(0);
        ArrayList<Tenant> daftarTenant = manajemenTenant.getDaftarTenant();
        for (int i = 0; i < daftarTenant.size(); i++) {
            Tenant tenant = daftarTenant.get(i);
            modelTenant.addRow(new Object[]{
                i + 1,
                tenant.getIdPengguna(),
                tenant.getNamaTenant(),
                tenant.getDaftarMenu().size()
            });
        }
    }

    private void isiSemuaTabelPelanggan() {
        if (tenantDipilih == null) {
            return;
        }
        labelTenantDipilih.setText("Pelanggan - " + tenantDipilih.getNamaTenant()
            + " - Meja " + mejaDipilih.getNoMeja());
        isiTabelMenu();
        isiTabelKeranjang();
        isiTabelPesananPelanggan();
        isiTabelAmbil();
        isiTabelRatingPelanggan();
    }

    private void isiTabelMenu() {
        modelMenu.setRowCount(0);
        ArrayList<StokMenu> daftarStok = tenantDipilih.getDaftarStokMenu();
        for (int i = 0; i < daftarStok.size(); i++) {
            StokMenu stok = daftarStok.get(i);
            Menu menu = stok.getProduk();
            modelMenu.addRow(new Object[]{
                i + 1,
                menu.getIdProduk(),
                menu.getNamaMenu(),
                menu.getJenis(),
                menu.getHargaKupon(),
                stok.getJumlahStok()
            });
        }
    }

    private void isiTabelKeranjang() {
        modelKeranjang.setRowCount(0);
        int total = 0;
        for (int i = 0; i < keranjang.size(); i++) {
            ItemPesanan item = keranjang.get(i);
            int subtotal = item.hitungSubTotalKupon();
            total += subtotal;
            modelKeranjang.addRow(new Object[]{
                i + 1,
                item.getMenu().getNamaMenu(),
                item.getJumlah(),
                item.getCatatan(),
                subtotal
            });
        }
        labelTotalKeranjang.setText("Total: " + total + " kupon / Rp" + (total * 5000));
    }

    private void isiTabelPesananPelanggan() {
        modelPesananPelanggan.setRowCount(0);
        for (Pesanan pesanan : pesananUntukMejaSaatIni()) {
            modelPesananPelanggan.addRow(barisPesanan(pesanan));
        }
    }

    private void isiTabelAmbil() {
        modelAmbil.setRowCount(0);
        for (Pesanan pesanan : pesananUntukMejaSaatIni()) {
            if ("Siap Diambil".equals(pesanan.getStatusPesanan())) {
                modelAmbil.addRow(new Object[]{
                    pesanan.getIdPesanan(),
                    pesanan.getNoMeja(),
                    pesanan.getStatusPesanan(),
                    "Rp" + pesanan.hitungTotalRupiah()
                });
            }
        }
    }

    private void isiTabelRatingPelanggan() {
        modelRatingPelanggan.setRowCount(0);
        for (Pesanan pesanan : pesananUntukMejaSaatIni()) {
            if ("Selesai".equals(pesanan.getStatusPesanan())) {
                modelRatingPelanggan.addRow(new Object[]{
                    pesanan.getIdPesanan(),
                    pesanan.getNoMeja(),
                    pesanan.getStatusPesanan(),
                    pesanan.getRating() == null ? "Belum" : "Sudah"
                });
            }
        }
    }

    private void isiSemuaTabelTenant() {
        if (tenantLogin == null) {
            return;
        }
        labelTenantLogin.setText("Tenant - " + tenantLogin.getNamaTenant());
        isiTabelPesananTenant();
        isiTabelRiwayatTenant();
        isiTabelRatingTenant();
        isiTabelMenuTenant();
        isiTabelStokTenant();
    }

    private void isiTabelPesananTenant() {
        modelPesananTenant.setRowCount(0);
        for (Pesanan pesanan : tenantLogin.getDaftarPesanan()) {
            if (!"Selesai".equals(pesanan.getStatusPesanan())) {
                modelPesananTenant.addRow(barisPesanan(pesanan));
            }
        }
    }

    private void isiTabelRiwayatTenant() {
        modelRiwayatTenant.setRowCount(0);
        for (Pesanan pesanan : tenantLogin.getDaftarPesanan()) {
            if ("Selesai".equals(pesanan.getStatusPesanan())) {
                modelRiwayatTenant.addRow(barisPesanan(pesanan));
            }
        }
    }

    private void isiTabelRatingTenant() {
        modelRatingTenant.setRowCount(0);
        for (Pesanan pesanan : tenantLogin.getDaftarPesanan()) {
            Rating rating = pesanan.getRating();
            if (rating != null) {
                modelRatingTenant.addRow(new Object[]{
                    pesanan.getIdPesanan(),
                    pesanan.getNoMeja(),
                    rating.getNilai(),
                    rating.getUlasan()
                });
            }
        }
    }

    private void isiTabelMenuTenant() {
        modelMenuTenant.setRowCount(0);
        for (Menu menu : tenantLogin.getDaftarMenu()) {
            modelMenuTenant.addRow(new Object[]{
                menu.getIdProduk(),
                menu.getNamaMenu(),
                menu.getJenis(),
                menu.getHargaKupon()
            });
        }
    }

    private void isiTabelStokTenant() {
        modelStokTenant.setRowCount(0);
        ArrayList<StokMenu> daftarStok = tenantLogin.getDaftarStokMenu();
        for (int i = 0; i < daftarStok.size(); i++) {
            StokMenu stok = daftarStok.get(i);
            Menu menu = stok.getProduk();
            modelStokTenant.addRow(new Object[]{
                i + 1,
                menu.getIdProduk(),
                menu.getNamaMenu(),
                menu.getJenis(),
                menu.getHargaKupon(),
                stok.getJumlahStok()
            });
        }
    }

    private void tambahKeKeranjang(JSpinner inputJumlah, JTextField inputCatatan) {
        int row = tabelMenu.getSelectedRow();
        if (row < 0) {
            tampilError("Pilih menu terlebih dahulu.");
            return;
        }

        ArrayList<StokMenu> daftarStok = tenantDipilih.getDaftarStokMenu();
        StokMenu stok = daftarStok.get(row);
        int jumlah = (int) inputJumlah.getValue();
        if (stok.getJumlahStok() < jumlah) {
            tampilError("Stok tidak cukup.");
            return;
        }

        String catatan = inputCatatan.getText().trim();
        if (catatan.isEmpty()) {
            catatan = "-";
        }
        keranjang.add(new ItemPesanan(stok.getProduk(), jumlah, catatan));
        inputCatatan.setText("");
        isiTabelKeranjang();
    }

    private void hapusDariKeranjang() {
        int row = tabelKeranjang.getSelectedRow();
        if (row < 0) {
            tampilError("Pilih item keranjang yang akan dihapus.");
            return;
        }
        keranjang.remove(row);
        isiTabelKeranjang();
    }

    private void checkout() {
        if (keranjang.isEmpty()) {
            tampilError("Keranjang masih kosong.");
            return;
        }

        Pesanan pesanan = new Pesanan(mejaDipilih.getNoMeja(), tenantDipilih.getNamaTenant());
        for (ItemPesanan item : keranjang) {
            pesanan.tambahItem(item.getMenu(), item.getJumlah(), item.getCatatan());
        }

        PembayaranQRIS pembayaran = new PembayaranQRIS(pesanan.hitungTotalRupiah());
        pembayaran.prosesPembayaran(pesanan.hitungTotalRupiah());
        pesanan.setPembayaran(pembayaran);

        int idPesanan = pelanggan.simpanPesananKeDB(tenantDipilih.getIdPengguna(), pesanan);
        if (idPesanan < 0) {
            tampilError("Checkout gagal. Periksa koneksi database atau stok menu.");
            return;
        }

        keranjang.clear();
        isiSemuaTabelPelanggan();
        tampilInfo("Checkout berhasil. ID pesanan: " + idPesanan
            + "\nKode QRIS: " + pembayaran.getKodeQRIS());
    }

    private void ambilPesanan() {
        int row = tabelAmbil.getSelectedRow();
        if (row < 0) {
            tampilError("Pilih pesanan yang akan diambil.");
            return;
        }

        int idPesanan = (int) modelAmbil.getValueAt(row, 0);
        boolean sukses = pelanggan.updateStatusPesananKeDB(idPesanan, "Selesai");
        if (!sukses) {
            tampilError("Gagal mengubah status pesanan.");
            return;
        }

        isiSemuaTabelPelanggan();
        tampilInfo("Pesanan sudah selesai.");
    }

    private void simpanRating(JSpinner inputNilai, JTextField inputUlasan) {
        int row = tabelRatingPelanggan.getSelectedRow();
        if (row < 0) {
            tampilError("Pilih pesanan yang akan diberi rating.");
            return;
        }

        String statusRating = String.valueOf(modelRatingPelanggan.getValueAt(row, 3));
        if ("Sudah".equals(statusRating)) {
            tampilError("Pesanan ini sudah diberi rating.");
            return;
        }

        int idPesanan = (int) modelRatingPelanggan.getValueAt(row, 0);
        int nilai = (int) inputNilai.getValue();
        String ulasan = inputUlasan.getText().trim();
        if (ulasan.isEmpty()) {
            ulasan = "-";
        }

        boolean sukses = pelanggan.simpanRatingKeDB(idPesanan, new Rating(nilai, ulasan));
        if (!sukses) {
            tampilError("Gagal menyimpan rating.");
            return;
        }

        inputUlasan.setText("");
        isiSemuaTabelPelanggan();
        tampilInfo("Rating berhasil disimpan.");
    }

    private void ubahStatusTenant(String statusBaru) {
        int row = tabelPesananTenant.getSelectedRow();
        if (row < 0) {
            tampilError("Pilih pesanan terlebih dahulu.");
            return;
        }

        int idPesanan = (int) modelPesananTenant.getValueAt(row, 0);
        boolean sukses = tenantLogin.updateStatusPesananKeDB(idPesanan, statusBaru);
        if (!sukses) {
            tampilError("Gagal mengubah status.");
            return;
        }

        isiSemuaTabelTenant();
        tampilInfo("Status pesanan diubah menjadi " + statusBaru + ".");
    }

    private void tambahMenuTenant(JComboBox<String> inputJenis, JTextField inputId,
                                  JTextField inputNama, JSpinner inputHarga,
                                  JSpinner inputStok) {
        String id = inputId.getText().trim();
        String nama = inputNama.getText().trim();
        if (id.isEmpty() || nama.isEmpty()) {
            tampilError("ID dan nama menu harus diisi.");
            return;
        }

        String jenis = String.valueOf(inputJenis.getSelectedItem());
        int harga = (int) inputHarga.getValue();
        int stok = (int) inputStok.getValue();
        Menu menu = "Makanan".equals(jenis)
            ? new Makanan(id, nama, harga)
            : new Minuman(id, nama, harga);

        boolean sukses = manajemenMenu.tambahMenuKeDB(tenantLogin.getIdPengguna(), menu, stok);
        if (!sukses) {
            tampilError("Gagal menambah menu. ID mungkin sudah dipakai.");
            return;
        }

        inputId.setText("");
        inputNama.setText("");
        isiSemuaTabelTenant();
        tampilInfo("Menu berhasil ditambahkan.");
    }

    private void updateStokTenant(JSpinner inputStokBaru) {
        int row = tabelStokTenant.getSelectedRow();
        if (row < 0) {
            tampilError("Pilih menu yang stoknya akan diubah.");
            return;
        }

        String idMenu = String.valueOf(modelStokTenant.getValueAt(row, 1));
        int stokBaru = (int) inputStokBaru.getValue();
        boolean sukses = manajemenMenu.updateStokKeDB(idMenu, stokBaru);
        if (!sukses) {
            tampilError("Gagal mengubah stok.");
            return;
        }

        isiTabelStokTenant();
        tampilInfo("Stok berhasil diubah.");
    }

    private ArrayList<Pesanan> pesananUntukMejaSaatIni() {
        ArrayList<Pesanan> hasil = new ArrayList<>();
        if (tenantDipilih == null || mejaDipilih == null) {
            return hasil;
        }

        for (Pesanan pesanan : tenantDipilih.getDaftarPesanan()) {
            if (mejaDipilih.getNoMeja().equals(pesanan.getNoMeja())) {
                hasil.add(pesanan);
            }
        }
        return hasil;
    }

    private Object[] barisPesanan(Pesanan pesanan) {
        return new Object[]{
            pesanan.getIdPesanan(),
            pesanan.getNoMeja(),
            pesanan.getStatusPesanan(),
            pesanan.hitungTotalKupon(),
            "Rp" + pesanan.hitungTotalRupiah()
        };
    }

    private DefaultTableModel modelTabel(String... kolom) {
        return new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JPanel panelBerpadding(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBorder(new EmptyBorder(18, 22, 18, 22));
        return panel;
    }

    /**
     * Helper sederhana untuk membuat seluruh GUI lebih rapi tanpa styling warna.
     * Komponen tetap Swing standar, hanya font, tinggi tabel, padding, dan ukuran
     * tombol yang diseragamkan.
     */
    private void aturTampilanKomponen(Component component) {
        if (component instanceof JLabel label) {
            if (label.getFont().getSize() < FONT_NORMAL.getSize()) {
                label.setFont(FONT_NORMAL);
            }
        } else if (component instanceof JButton button) {
            button.setFont(FONT_NORMAL);
            if (button.getPreferredSize().height < 34) {
                Dimension ukuran = button.getPreferredSize();
                button.setPreferredSize(new Dimension(Math.max(ukuran.width, 110), 36));
            }
        } else if (component instanceof JTable table) {
            table.setFont(FONT_TABLE);
            table.setRowHeight(28);
            table.getTableHeader().setFont(FONT_TABLE_HEADER);
            table.setFillsViewportHeight(true);
        } else if (component instanceof JTextField textField) {
            textField.setFont(FONT_NORMAL);
        } else if (component instanceof JPasswordField passwordField) {
            passwordField.setFont(FONT_NORMAL);
        } else if (component instanceof JSpinner spinner) {
            spinner.setFont(FONT_NORMAL);
        } else if (component instanceof JComboBox<?> comboBox) {
            comboBox.setFont(FONT_NORMAL);
        } else if (component instanceof JTabbedPane tabbedPane) {
            tabbedPane.setFont(FONT_NORMAL);
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                aturTampilanKomponen(child);
            }
        }
    }

    private JPanel header(String teks, java.awt.event.ActionListener kembali) {
        JLabel label = new JLabel(teks);
        label.setFont(FONT_PAGE_TITLE);
        return headerDenganLabel(label, kembali);
    }

    private JPanel headerDenganLabel(JLabel label, java.awt.event.ActionListener kembali) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(0, 0, GAP, 0));
        label.setFont(FONT_PAGE_TITLE);
        JButton tombolKembali = new JButton("Kembali");
        tombolKembali.addActionListener(kembali);
        panel.add(label, BorderLayout.WEST);
        panel.add(tombolKembali, BorderLayout.EAST);
        return panel;
    }

    private JPanel panelTombol(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, GAP, GAP));
        panel.setBorder(new EmptyBorder(4, 0, 0, 0));
        for (JButton button : buttons) {
            panel.add(button);
        }
        return panel;
    }

    private void tampilError(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Perhatian", JOptionPane.ERROR_MESSAGE);
    }

    private void tampilInfo(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Jika look and feel sistem gagal dipakai, Swing tetap memakai default.
        }

        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}
