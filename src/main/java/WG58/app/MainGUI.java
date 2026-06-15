package WG58.app;

import WG58.database.DatabaseInitializer;
import WG58.manajemen.ManajemenMenu;
import WG58.manajemen.ManajemenTenant;
import WG58.menu.*;
import WG58.menu.Menu;
import WG58.pembayaran.PembayaranQRIS;
import WG58.pengguna.Meja;
import WG58.pengguna.Pelanggan;
import WG58.pengguna.Tenant;
import WG58.pesanan.ItemPesanan;
import WG58.pesanan.Pesanan;
import WG58.pesanan.Rating;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * MainGUI — Entry point berbasis Java Swing untuk WG58 Food Court System.
 *
 * Menggantikan alur Scanner di Main.java dengan tampilan grafis.
 * Semua business logic tetap berjalan di class yang sudah ada
 * (Pelanggan, Tenant, ManajemenMenu, ManajemenTenant, dst.)
 *
 * OOP yang terlihat di sini:
 *  - Polymorphism : aksesSistem() dipanggil pada Pelanggan & Tenant (hasil override)
 *  - Encapsulation: state GUI (tenantLogin, tenantDipilih, dll.) disimpan private
 *  - Collections  : ArrayList dipakai untuk keranjang, data tabel, dll.
 *  - Exception    : try-catch untuk parsing input user dan operasi DB
 */
public class MainGUI extends JFrame {

    // ═══════════════════════════════════════════════
    //  KONSTANTA DESAIN
    // ═══════════════════════════════════════════════
    private static final Color COLOR_PRIMARY      = new Color(255, 140,  0);
    private static final Color COLOR_PRIMARY_DARK = new Color(200, 100,  0);
    private static final Color COLOR_BG           = new Color(248, 249, 250);
    private static final Color COLOR_CARD         = Color.WHITE;
    private static final Color COLOR_TEXT         = new Color( 33,  37,  41);
    private static final Color COLOR_MUTED        = new Color(108, 117, 125);
    private static final Color COLOR_ROW_SEL      = new Color(255, 230, 190);

    private static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD,  24);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD,  16);
    private static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_TH       = new Font("Segoe UI", Font.BOLD,  13);

    // ═══════════════════════════════════════════════
    //  STATE APLIKASI  (encapsulation: semua private)
    // ═══════════════════════════════════════════════
    private final ManajemenTenant manajemenTenant;
    private final ManajemenMenu   manajemenMenu;
    private final Pelanggan       pelanggan;

    private Tenant tenantLogin;    // tenant yang sedang login
    private Tenant tenantDipilih;  // tenant yang dipilih pelanggan
    private Meja   mejaDipilih;    // meja pelanggan

    // Keranjang sementara milik pelanggan (Collection I - ArrayList)
    private final ArrayList<ItemPesanan> keranjang = new ArrayList<>();

    // ═══════════════════════════════════════════════
    //  PANEL & NAVIGASI
    // ═══════════════════════════════════════════════
    private CardLayout cardLayout;
    private JPanel     mainPanel;

    // --- Model tabel (dideklarasikan di level instance agar refresh() bisa akses) ---
    // Panel: Pilih Tenant
    private DefaultTableModel modelTblTenant;

    // Panel: Menu Pelanggan → Tab Pesan
    private DefaultTableModel modelTblMenu;
    private DefaultTableModel modelTblKeranjang;
    private JLabel            lblTotalKeranjang;

    // Panel: Menu Pelanggan → Tab Status
    private DefaultTableModel modelTblCekPesanan;

    // Panel: Menu Pelanggan → Tab Ambil
    private DefaultTableModel modelTblAmbil;
    private JTable            tblAmbil;

    // Panel: Menu Pelanggan → Tab Rating
    private DefaultTableModel modelTblRatingPelanggan;
    private JTable            tblRating;

    // Panel: Dashboard Tenant
    private JLabel            lblTenantName;
    private DefaultTableModel modelTblAktif;
    private DefaultTableModel modelTblUpdateStatus;
    private JTable            tblUpdateStatus;
    private DefaultTableModel modelTblRiwayat;
    private DefaultTableModel modelTblRatingTenant;
    private DefaultTableModel modelTblMenuTenant;
    private DefaultTableModel modelTblStok;
    private JTable            tblStok;

    // ═══════════════════════════════════════════════
    //  KONSTRUKTOR & INISIALISASI
    // ═══════════════════════════════════════════════
    public MainGUI() {
        // Inisialisasi database (buat tabel + sample data jika belum ada)
        DatabaseInitializer.initialize();

        manajemenTenant = new ManajemenTenant();
        manajemenMenu   = new ManajemenMenu();
        pelanggan       = new Pelanggan("P01", "Pelanggan");

        setupFrame();
        buildAllPanels();
        navigateTo("HOME");
    }

    private void setupFrame() {
        setTitle("WG58 Food Court — Sistem Kasir");
        setSize(960, 680);
        setMinimumSize(new Dimension(820, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
    }

    private void buildAllPanels() {
        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        mainPanel.setBackground(COLOR_BG);

        mainPanel.add(buildPanelHome(),           "HOME");
        mainPanel.add(buildPanelPilihTenant(),    "PILIH_TENANT");
        mainPanel.add(buildPanelMenuPelanggan(),  "MENU_PELANGGAN");
        mainPanel.add(buildPanelLoginTenant(),    "LOGIN_TENANT");
        mainPanel.add(buildPanelDashboardTenant(),"DASHBOARD_TENANT");

        add(mainPanel);
    }

    /**
     * Navigasi antar panel.  Sebelum show, refresh data terkini dari DB.
     */
    private void navigateTo(String panelName) {
        switch (panelName) {
            case "PILIH_TENANT"     -> refreshPilihTenant();
            case "MENU_PELANGGAN"   -> refreshSemuaTabPelanggan();
            case "DASHBOARD_TENANT" -> refreshSemuaTabTenant();
        }
        cardLayout.show(mainPanel, panelName);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PANEL 1 — HOME
    // ═══════════════════════════════════════════════════════════════════════
    private JPanel buildPanelHome() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);

        JPanel card = createCard(420, 360);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(Box.createVerticalStrut(28));

        JLabel iconLabel = new JLabel("🍽", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel   = makeLabel("WG58 FOOD COURT",  FONT_TITLE,    COLOR_TEXT);
        JLabel subtitleLabel= makeLabel("Sistem Kasir Digital", FONT_BODY,  COLOR_MUTED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(32));

        // Tombol Pelanggan — memanggil aksesSistem() (polymorphism)
        JButton btnPelanggan = makePrimaryBtn("🧑  Masuk sebagai Pelanggan", 320, 50);
        btnPelanggan.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPelanggan.addActionListener(e -> {
            pelanggan.aksesSistem(); // override di Pelanggan
            navigateTo("PILIH_TENANT");
        });

        JButton btnTenant = makeSecondaryBtn("🏪  Login sebagai Tenant", 320, 44);
        btnTenant.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnTenant.addActionListener(e -> navigateTo("LOGIN_TENANT"));

        card.add(btnPelanggan);
        card.add(Box.createVerticalStrut(12));
        card.add(btnTenant);
        card.add(Box.createVerticalStrut(28));

        panel.add(card);
        return panel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PANEL 2 — PILIH TENANT  (Pelanggan)
    // ═══════════════════════════════════════════════════════════════════════
    private JPanel buildPanelPilihTenant() {
        JPanel panel = wrapWithPadding(20, 30);

        panel.add(buildHeader("Pilih Tenant", "Mode: Pelanggan",
                e -> navigateTo("HOME")), BorderLayout.NORTH);

        // Tabel tenant
        modelTblTenant = newTableModel("No", "ID", "Nama Tenant", "Jml Menu");
        JTable tbl = makeTable(modelTblTenant);
        setColWidths(tbl, 40, 70, 220, 90);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setBackground(COLOR_BG);
        center.setBorder(new EmptyBorder(12, 0, 0, 0));
        center.add(new JScrollPane(tbl), BorderLayout.CENTER);

        // Bar bawah: input meja + tombol lanjut
        JPanel barBawah = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        barBawah.setBackground(COLOR_BG);

        barBawah.add(makeLabel("Nomor Meja:", FONT_BODY, COLOR_TEXT));
        JTextField tfMeja = new JTextField(8);
        tfMeja.setFont(FONT_BODY);
        tfMeja.setPreferredSize(new Dimension(100, 36));
        barBawah.add(tfMeja);

        JButton btnLanjut = makePrimaryBtn("Pilih Tenant & Lanjut →", 220, 38);
        btnLanjut.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row < 0) { showErr("Pilih tenant terlebih dahulu."); return; }

            String noMeja = tfMeja.getText().trim();
            if (noMeja.isEmpty()) { showErr("Nomor meja tidak boleh kosong."); return; }

            tenantDipilih = manajemenTenant.getDaftarTenant().get(row);
            mejaDipilih   = new Meja(noMeja);
            keranjang.clear();
            navigateTo("MENU_PELANGGAN");
        });
        barBawah.add(btnLanjut);

        center.add(barBawah, BorderLayout.SOUTH);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private void refreshPilihTenant() {
        if (modelTblTenant == null) return;
        modelTblTenant.setRowCount(0);
        ArrayList<Tenant> list = manajemenTenant.getDaftarTenant();
        for (int i = 0; i < list.size(); i++) {
            Tenant t = list.get(i);
            modelTblTenant.addRow(new Object[]{
                i + 1, t.getIdPengguna(), t.getNamaTenant(), t.getDaftarMenu().size()
            });
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PANEL 3 — MENU PELANGGAN  (TabbedPane)
    // ═══════════════════════════════════════════════════════════════════════
    private JPanel buildPanelMenuPelanggan() {
        JPanel panel = wrapWithPadding(20, 30);

        // Header dinamis — diperbarui saat navigate
        JLabel lblInfo = makeLabel("", FONT_SUBTITLE, COLOR_MUTED);
        JPanel header  = buildHeader("Menu Pelanggan", "", e -> navigateTo("PILIH_TENANT"));
        header.add(lblInfo, BorderLayout.SOUTH);
        panel.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BODY);
        tabs.addTab("🛒 Pesan",          buildTabPesan());
        tabs.addTab("📋 Status Pesanan", buildTabCekPesanan());
        tabs.addTab("📦 Ambil Pesanan",  buildTabAmbil());
        tabs.addTab("⭐ Beri Rating",    buildTabRatingPelanggan());

        // Refresh tab yang aktif saat di-klik
        tabs.addChangeListener(e -> {
            switch (tabs.getSelectedIndex()) {
                case 1 -> refreshCekPesanan();
                case 2 -> refreshAmbilPesanan();
                case 3 -> refreshRatingPelanggan();
            }
        });

        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private void refreshSemuaTabPelanggan() {
        refreshMenuTable();
        refreshKeranjang();
    }

    // ── Tab 1: Pesan ──────────────────────────────────────────────────────
    private JPanel buildTabPesan() {
        JPanel panel = new JPanel(new BorderLayout(14, 0));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // --- Sisi kiri: daftar menu ---
        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setBackground(COLOR_CARD);
        leftPanel.add(makeLabel("Daftar Menu", FONT_SUBTITLE, COLOR_TEXT), BorderLayout.NORTH);

        modelTblMenu = newTableModel("No", "Nama Menu", "Jenis", "Kupon", "Status");
        JTable tblMenu = makeTable(modelTblMenu);

        leftPanel.add(new JScrollPane(tblMenu), BorderLayout.CENTER);

        // Bar tambah ke keranjang
        JPanel barTambah = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        barTambah.setBackground(COLOR_CARD);

        barTambah.add(makeLabel("Jml:", FONT_BODY, COLOR_TEXT));
        JSpinner spJml = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        spJml.setFont(FONT_BODY);
        spJml.setPreferredSize(new Dimension(60, 32));
        barTambah.add(spJml);

        barTambah.add(makeLabel("Catatan:", FONT_BODY, COLOR_TEXT));
        JTextField tfCatatan = new JTextField(10);
        tfCatatan.setFont(FONT_BODY);
        barTambah.add(tfCatatan);

        JButton btnTambah = makePrimaryBtn("+ Tambah ke Keranjang", 200, 32);
        btnTambah.addActionListener(e -> {
            int row = tblMenu.getSelectedRow();
            if (row < 0) { showErr("Pilih menu dulu."); return; }

            ArrayList<Menu>     menus = tenantDipilih.getDaftarMenu();
            ArrayList<StokMenu> stoks = tenantDipilih.getDaftarStokMenu();
            if (row >= menus.size()) return;

            Menu     menu = menus.get(row);
            StokMenu stok = stoks.get(row);

            if (!stok.stokTersedia()) { showErr("Stok habis!"); return; }

            int jml = (int) spJml.getValue();
            if (stok.getJumlahStok() < jml) {
                showErr("Stok tidak cukup. Tersedia: " + stok.getJumlahStok()); return;
            }

            String cat = tfCatatan.getText().trim();
            if (cat.isEmpty()) cat = "-";

            stok.kurangiStok(jml);
            keranjang.add(new ItemPesanan(menu, jml, cat));
            tfCatatan.setText("");
            refreshMenuTable();
            refreshKeranjang();
        });
        barTambah.add(btnTambah);
        leftPanel.add(barTambah, BorderLayout.SOUTH);
        panel.add(leftPanel, BorderLayout.CENTER);

        // --- Sisi kanan: keranjang ---
        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(COLOR_CARD);
        rightPanel.setPreferredSize(new Dimension(310, 0));
        rightPanel.add(makeLabel("Keranjang", FONT_SUBTITLE, COLOR_TEXT), BorderLayout.NORTH);

        modelTblKeranjang = newTableModel("Menu", "Jml", "Kupon", "Catatan");
        rightPanel.add(new JScrollPane(makeTable(modelTblKeranjang)), BorderLayout.CENTER);

        // Total + tombol
        JPanel bottomRight = new JPanel(new GridLayout(0, 1, 4, 6));
        bottomRight.setBackground(COLOR_CARD);
        bottomRight.setBorder(new EmptyBorder(8, 0, 0, 0));

        lblTotalKeranjang = makeLabel("Total: 0 Kupon = Rp0", FONT_BODY, COLOR_TEXT);
        bottomRight.add(lblTotalKeranjang);

        JButton btnBayar = makePrimaryBtn("💳  Bayar Sekarang", 280, 42);
        btnBayar.addActionListener(e -> prosesPembayaran());
        bottomRight.add(btnBayar);

        JButton btnKosong = makeSecondaryBtn("🗑  Kosongkan Keranjang", 280, 34);
        btnKosong.addActionListener(e -> {
            keranjang.clear();
            refreshMenuTable();
            refreshKeranjang();
        });
        bottomRight.add(btnKosong);

        rightPanel.add(bottomRight, BorderLayout.SOUTH);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private void refreshMenuTable() {
        if (modelTblMenu == null || tenantDipilih == null) return;
        modelTblMenu.setRowCount(0);

        ArrayList<Menu>     menus = tenantDipilih.getDaftarMenu();
        ArrayList<StokMenu> stoks = tenantDipilih.getDaftarStokMenu();

        for (int i = 0; i < menus.size(); i++) {
            Menu m     = menus.get(i);
            StokMenu s = stoks.get(i);
            String status = s.stokTersedia()
                ? "Tersedia (" + s.getJumlahStok() + ")"
                : "Habis";
            modelTblMenu.addRow(new Object[]{
                i + 1, m.getNamaMenu(), m.getJenis(), m.getHargaKupon(), status
            });
        }
    }

    private void refreshKeranjang() {
        if (modelTblKeranjang == null) return;
        modelTblKeranjang.setRowCount(0);
        int totalKupon = 0;

        for (ItemPesanan item : keranjang) {
            modelTblKeranjang.addRow(new Object[]{
                item.getMenu().getNamaMenu(),
                item.getJumlah(),
                item.hitungSubTotalKupon(),
                item.getCatatan()
            });
            totalKupon += item.hitungSubTotalKupon();
        }

        if (lblTotalKeranjang != null)
            lblTotalKeranjang.setText("Total: " + totalKupon + " Kupon = Rp" + (totalKupon * 5000));
    }

    /**
     * Proses pembayaran via dialog QRIS.
     * Exception handling: try-catch untuk parsing nominal input user.
     */
    private void prosesPembayaran() {
        if (keranjang.isEmpty()) { showErr("Keranjang masih kosong!"); return; }

        // Buat pesanan dari isi keranjang
        Pesanan pesanan = new Pesanan(mejaDipilih.getNoMeja(), tenantDipilih.getNamaTenant());
        for (ItemPesanan item : keranjang)
            pesanan.tambahItem(item.getMenu(), item.getJumlah(), item.getCatatan());

        int totalRupiah = pesanan.hitungTotalRupiah();
        PembayaranQRIS pembayaran = new PembayaranQRIS(totalRupiah);

        // Dialog QRIS
        JPanel dlgPanel = new JPanel(new GridLayout(0, 1, 4, 6));
        dlgPanel.add(new JLabel("Total Bayar   : Rp" + totalRupiah));
        dlgPanel.add(new JLabel("Kode QRIS     : " + pembayaran.getKodeQRIS()));
        dlgPanel.add(new JLabel("Nominal Bayar :"));
        JTextField tfNominal = new JTextField(String.valueOf(totalRupiah));
        dlgPanel.add(tfNominal);

        int result = JOptionPane.showConfirmDialog(
            this, dlgPanel, "Pembayaran QRIS", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return;

        // Exception handling — input nominal bisa non-angka
        try {
            int nominal = Integer.parseInt(tfNominal.getText().trim());
            pembayaran.prosesPembayaran(nominal);

            if (pembayaran.getStatusBayar()) {
                pesanan.setPembayaran(pembayaran);
                int idPesanan = pelanggan.simpanPesananKeDB(
                    tenantDipilih.getIdPengguna(), pesanan);

                if (idPesanan != -1) {
                    keranjang.clear();
                    refreshKeranjang();
                    refreshMenuTable();
                    showInfo("✅ Pembayaran berhasil!\nPesanan dikirim ke tenant.");
                } else {
                    showErr("Pesanan gagal disimpan ke database.");
                }
            } else {
                showErr("Nominal tidak sesuai.\nTotal yang harus dibayar: Rp" + totalRupiah);
            }
        } catch (NumberFormatException ex) {
            showErr("Nominal tidak valid. Masukkan angka saja.");
        }
    }

    // ── Tab 2: Cek Status Pesanan ─────────────────────────────────────────
    private JPanel buildTabCekPesanan() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.add(makeLabel("Status Pesanan di Meja Anda", FONT_SUBTITLE, COLOR_TEXT), BorderLayout.NORTH);

        modelTblCekPesanan = newTableModel("ID", "Meja", "Status", "Kupon", "Rupiah", "Items");
        panel.add(new JScrollPane(makeTable(modelTblCekPesanan)), BorderLayout.CENTER);

        JButton btnRefresh = makeSecondaryBtn("🔄 Refresh", 120, 32);
        btnRefresh.addActionListener(e -> refreshCekPesanan());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.setBackground(COLOR_CARD);
        south.add(btnRefresh);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshCekPesanan() {
        if (modelTblCekPesanan == null || tenantDipilih == null) return;
        modelTblCekPesanan.setRowCount(0);

        for (Pesanan p : tenantDipilih.getDaftarPesanan()) {
            if (p.getNoMeja().equals(mejaDipilih.getNoMeja())) {
                modelTblCekPesanan.addRow(new Object[]{
                    p.getIdPesanan(), p.getNoMeja(), p.getStatusPesanan(),
                    p.hitungTotalKupon(), "Rp" + p.hitungTotalRupiah(),
                    p.getDaftarItem().size() + " item"
                });
            }
        }
    }

    // ── Tab 3: Ambil Pesanan ──────────────────────────────────────────────
    private JPanel buildTabAmbil() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.add(makeLabel("Pesanan Siap Diambil", FONT_SUBTITLE, COLOR_TEXT), BorderLayout.NORTH);

        modelTblAmbil = newTableModel("ID", "Meja", "Status", "Total");
        tblAmbil      = makeTable(modelTblAmbil);
        panel.add(new JScrollPane(tblAmbil), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        south.setBackground(COLOR_CARD);

        JButton btnRefresh = makeSecondaryBtn("🔄 Refresh", 100, 32);
        btnRefresh.addActionListener(e -> refreshAmbilPesanan());
        south.add(btnRefresh);

        JButton btnAmbil = makePrimaryBtn("✅ Ambil Pesanan Ini", 170, 36);
        btnAmbil.addActionListener(e -> ambilPesananAction());
        south.add(btnAmbil);

        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshAmbilPesanan() {
        if (modelTblAmbil == null || tenantDipilih == null) return;
        modelTblAmbil.setRowCount(0);

        for (Pesanan p : tenantDipilih.getDaftarPesanan()) {
            if (p.getNoMeja().equals(mejaDipilih.getNoMeja())
                    && "Siap Diambil".equals(p.getStatusPesanan())) {
                modelTblAmbil.addRow(new Object[]{
                    p.getIdPesanan(), p.getNoMeja(),
                    p.getStatusPesanan(), "Rp" + p.hitungTotalRupiah()
                });
            }
        }
    }

    private void ambilPesananAction() {
        int row = tblAmbil.getSelectedRow();
        if (row < 0) { showErr("Pilih pesanan yang akan diambil."); return; }

        int idPesanan = (int) modelTblAmbil.getValueAt(row, 0);
        boolean ok = pelanggan.updateStatusPesananKeDB(idPesanan, "Selesai");

        if (ok) {
            // Sinkronisasi status in-memory
            for (Pesanan p : tenantDipilih.getDaftarPesanan())
                if (p.getIdPesanan() == idPesanan) { p.ubahStatus("Selesai"); break; }

            refreshAmbilPesanan();
            showInfo("Pesanan berhasil diambil!\nSilakan beri rating pada tab ⭐.");
        } else {
            showErr("Gagal memperbarui status pesanan.");
        }
    }

    // ── Tab 4: Beri Rating ────────────────────────────────────────────────
    private JPanel buildTabRatingPelanggan() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.add(makeLabel("Beri Rating untuk Pesanan Selesai", FONT_SUBTITLE, COLOR_TEXT), BorderLayout.NORTH);

        modelTblRatingPelanggan = newTableModel("ID", "Meja", "Status", "Total");
        tblRating = makeTable(modelTblRatingPelanggan);
        panel.add(new JScrollPane(tblRating), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        south.setBackground(COLOR_CARD);

        JButton btnRefresh = makeSecondaryBtn("🔄 Refresh", 100, 32);
        btnRefresh.addActionListener(e -> refreshRatingPelanggan());
        south.add(btnRefresh);

        JButton btnRate = makePrimaryBtn("⭐ Beri Rating", 150, 36);
        btnRate.addActionListener(e -> beriRatingAction());
        south.add(btnRate);

        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshRatingPelanggan() {
        if (modelTblRatingPelanggan == null || tenantDipilih == null) return;
        modelTblRatingPelanggan.setRowCount(0);

        for (Pesanan p : tenantDipilih.getDaftarPesanan()) {
            if (p.getNoMeja().equals(mejaDipilih.getNoMeja())
                    && "Selesai".equals(p.getStatusPesanan())
                    && p.getRating() == null) {
                modelTblRatingPelanggan.addRow(new Object[]{
                    p.getIdPesanan(), p.getNoMeja(),
                    p.getStatusPesanan(), "Rp" + p.hitungTotalRupiah()
                });
            }
        }
    }

    private void beriRatingAction() {
        int row = tblRating.getSelectedRow();
        if (row < 0) { showErr("Pilih pesanan yang ingin dirating."); return; }

        int idPesanan = (int) modelTblRatingPelanggan.getValueAt(row, 0);

        JPanel dlg = new JPanel(new GridLayout(0, 1, 4, 6));
        dlg.add(new JLabel("Rating (1–5):"));
        JSpinner spNilai = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        dlg.add(spNilai);
        dlg.add(new JLabel("Ulasan:"));
        JTextField tfUlasan = new JTextField();
        dlg.add(tfUlasan);

        int result = JOptionPane.showConfirmDialog(this, dlg, "Beri Rating", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        int    nilai  = (int) spNilai.getValue();
        String ulasan = tfUlasan.getText().trim();
        if (ulasan.isEmpty()) ulasan = "-";

        Rating rating = new Rating(nilai, ulasan);
        boolean ok = pelanggan.simpanRatingKeDB(idPesanan, rating);

        if (ok) {
            for (Pesanan p : tenantDipilih.getDaftarPesanan())
                if (p.getIdPesanan() == idPesanan) { p.setRating(rating); break; }

            refreshRatingPelanggan();
            showInfo("Rating " + nilai + "⭐ berhasil diberikan!");
        } else {
            showErr("Gagal menyimpan rating.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PANEL 4 — LOGIN TENANT
    // ═══════════════════════════════════════════════════════════════════════
    private JTextField     tfUsername;
    private JPasswordField tfPassword;

    private JPanel buildPanelLoginTenant() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);

        JPanel card = createCard(380, 330);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(Box.createVerticalStrut(24));

        JLabel icon  = new JLabel("🏪", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = makeLabel("Login Tenant", FONT_TITLE, COLOR_TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(icon);
        card.add(Box.createVerticalStrut(8));
        card.add(title);
        card.add(Box.createVerticalStrut(20));

        // Form grid
        JPanel form = new JPanel(new GridLayout(0, 1, 4, 8));
        form.setBackground(COLOR_CARD);
        form.setBorder(new EmptyBorder(0, 24, 0, 24));
        form.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(makeLabel("Username:", FONT_BODY, COLOR_TEXT));
        tfUsername = new JTextField();
        tfUsername.setFont(FONT_BODY);
        tfUsername.setPreferredSize(new Dimension(0, 36));
        form.add(tfUsername);

        form.add(makeLabel("Password:", FONT_BODY, COLOR_TEXT));
        tfPassword = new JPasswordField();
        tfPassword.setFont(FONT_BODY);
        form.add(tfPassword);

        card.add(form);
        card.add(Box.createVerticalStrut(16));

        JButton btnLogin = makePrimaryBtn("🔑  Login", 310, 46);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> loginTenantAction());
        tfPassword.addActionListener(e -> loginTenantAction()); // Enter di password field

        JButton btnKembali = makeSecondaryBtn("← Kembali", 310, 36);
        btnKembali.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnKembali.addActionListener(e -> navigateTo("HOME"));

        card.add(btnLogin);
        card.add(Box.createVerticalStrut(10));
        card.add(btnKembali);
        card.add(Box.createVerticalStrut(24));

        panel.add(card);
        return panel;
    }

    private void loginTenantAction() {
        String user = tfUsername.getText().trim();
        String pass = new String(tfPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            showErr("Username dan password tidak boleh kosong.");
            return;
        }

        tenantLogin = manajemenTenant.loginTenant(user, pass);

        if (tenantLogin != null) {
            tenantLogin.aksesSistem(); // polymorphism — override di Tenant
            tfUsername.setText("");
            tfPassword.setText("");
            navigateTo("DASHBOARD_TENANT");
        } else {
            showErr("Username atau password salah.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PANEL 5 — DASHBOARD TENANT  (TabbedPane)
    // ═══════════════════════════════════════════════════════════════════════
    private JPanel buildPanelDashboardTenant() {
        JPanel panel = wrapWithPadding(20, 30);

        // Header dengan nama tenant
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BG);
        header.setBorder(new EmptyBorder(0, 0, 14, 0));

        lblTenantName = makeLabel("Dashboard Tenant", FONT_TITLE, COLOR_TEXT);
        header.add(lblTenantName, BorderLayout.WEST);

        JButton btnLogout = makeSecondaryBtn("⬅ Logout", 110, 34);
        btnLogout.addActionListener(e -> { tenantLogin = null; navigateTo("HOME"); });
        header.add(btnLogout, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BODY);
        tabs.addTab("📋 Pesanan Aktif",  buildTabPesananAktif());
        tabs.addTab("🔄 Update Status",  buildTabUpdateStatus());
        tabs.addTab("📜 Riwayat",        buildTabRiwayat());
        tabs.addTab("⭐ Rating",          buildTabRatingTenant());
        tabs.addTab("➕ Tambah Menu",     buildTabTambahMenu());
        tabs.addTab("📦 Kelola Stok",    buildTabKelolastok());

        tabs.addChangeListener(e -> {
            switch (tabs.getSelectedIndex()) {
                case 0 -> refreshPesananAktif();
                case 1 -> refreshUpdateStatus();
                case 2 -> refreshRiwayat();
                case 3 -> refreshRatingTenant();
                case 4 -> refreshMenuTenant();
                case 5 -> refreshStokTenant();
            }
        });

        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private void refreshSemuaTabTenant() {
        if (tenantLogin == null) return;
        if (lblTenantName != null)
            lblTenantName.setText("Dashboard — " + tenantLogin.getNamaTenant());
        refreshPesananAktif();
    }

    // ── Tab T1: Pesanan Aktif ─────────────────────────────────────────────
    private JPanel buildTabPesananAktif() {
        JPanel panel = tabWrapper("Pesanan Aktif");
        modelTblAktif = newTableModel("ID", "Meja", "Status", "Kupon", "Rupiah", "Items");
        panel.add(new JScrollPane(makeTable(modelTblAktif)), BorderLayout.CENTER);
        panel.add(refreshBar(e -> refreshPesananAktif()), BorderLayout.SOUTH);
        return panel;
    }

    private void refreshPesananAktif() {
        if (modelTblAktif == null || tenantLogin == null) return;
        modelTblAktif.setRowCount(0);
        for (Pesanan p : tenantLogin.getDaftarPesanan()) {
            if (!"Selesai".equals(p.getStatusPesanan())) {
                modelTblAktif.addRow(new Object[]{
                    p.getIdPesanan(), p.getNoMeja(), p.getStatusPesanan(),
                    p.hitungTotalKupon(), "Rp" + p.hitungTotalRupiah(),
                    p.getDaftarItem().size() + " item"
                });
            }
        }
    }

    // ── Tab T2: Update Status ─────────────────────────────────────────────
    private JPanel buildTabUpdateStatus() {
        JPanel panel = tabWrapper("Update Status Pesanan");
        modelTblUpdateStatus = newTableModel("ID", "Meja", "Status Saat Ini", "Total");
        tblUpdateStatus = makeTable(modelTblUpdateStatus);
        panel.add(new JScrollPane(tblUpdateStatus), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        south.setBackground(COLOR_CARD);
        
        JButton btnRefresh = makeSecondaryBtn("🔄 Refresh", 100, 32);
        btnRefresh.addActionListener(e -> refreshUpdateStatus());
        south.add(btnRefresh);
        
        JButton btnDiproses = makeSecondaryBtn("🍳 → Diproses", 140, 36);
        btnDiproses.addActionListener(e -> ubahStatusTenant("Diproses"));
        south.add(btnDiproses);
        
        JButton btnSiap = makePrimaryBtn("✅ → Siap Diambil", 160, 36);
        btnSiap.addActionListener(e -> ubahStatusTenant("Siap Diambil"));
        south.add(btnSiap);
        
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    // Workaround: JPanel.add() tidak mengembalikan JButton, jadi kita ganti
    // buildTabUpdateStatus() agar tombol-tombol ditambah manual.
    //   (lihat implementasi langsung di bawah)

    private void refreshUpdateStatus() {
        if (modelTblUpdateStatus == null || tenantLogin == null) return;
        modelTblUpdateStatus.setRowCount(0);
        for (Pesanan p : tenantLogin.getDaftarPesanan()) {
            if (!"Selesai".equals(p.getStatusPesanan())) {
                modelTblUpdateStatus.addRow(new Object[]{
                    p.getIdPesanan(), p.getNoMeja(),
                    p.getStatusPesanan(), "Rp" + p.hitungTotalRupiah()
                });
            }
        }
    }

    private void ubahStatusTenant(String statusBaru) {
        int row = tblUpdateStatus.getSelectedRow();
        if (row < 0) { showErr("Pilih pesanan yang akan diubah statusnya."); return; }

        int idPesanan = (int) modelTblUpdateStatus.getValueAt(row, 0);
        boolean ok    = tenantLogin.updateStatusPesananKeDB(idPesanan, statusBaru);

        if (ok) {
            for (Pesanan p : tenantLogin.getDaftarPesanan())
                if (p.getIdPesanan() == idPesanan) { p.ubahStatus(statusBaru); break; }

            refreshUpdateStatus();
            refreshPesananAktif();
            showInfo("Status diubah menjadi: " + statusBaru);
        } else {
            showErr("Gagal mengubah status.");
        }
    }

    // ── Tab T3: Riwayat ───────────────────────────────────────────────────
    private JPanel buildTabRiwayat() {
        JPanel panel = tabWrapper("Riwayat Pesanan Selesai");
        modelTblRiwayat = newTableModel("ID", "Meja", "Status", "Kupon", "Rupiah");
        panel.add(new JScrollPane(makeTable(modelTblRiwayat)), BorderLayout.CENTER);
        panel.add(refreshBar(e -> refreshRiwayat()), BorderLayout.SOUTH);
        return panel;
    }

    private void refreshRiwayat() {
        if (modelTblRiwayat == null || tenantLogin == null) return;
        modelTblRiwayat.setRowCount(0);
        for (Pesanan p : tenantLogin.getDaftarPesanan()) {
            if ("Selesai".equals(p.getStatusPesanan())) {
                modelTblRiwayat.addRow(new Object[]{
                    p.getIdPesanan(), p.getNoMeja(), p.getStatusPesanan(),
                    p.hitungTotalKupon(), "Rp" + p.hitungTotalRupiah()
                });
            }
        }
    }

    // ── Tab T4: Rating Tenant ─────────────────────────────────────────────
    private JPanel buildTabRatingTenant() {
        JPanel panel = tabWrapper("Rating dari Pelanggan");
        modelTblRatingTenant = newTableModel("No", "Meja", "Rating", "Ulasan");
        panel.add(new JScrollPane(makeTable(modelTblRatingTenant)), BorderLayout.CENTER);
        panel.add(refreshBar(e -> refreshRatingTenant()), BorderLayout.SOUTH);
        return panel;
    }

    private void refreshRatingTenant() {
        if (modelTblRatingTenant == null || tenantLogin == null) return;
        modelTblRatingTenant.setRowCount(0);
        int no = 1;
        for (Pesanan p : tenantLogin.getDaftarPesanan()) {
            Rating r = p.getRating();
            if (r != null) {
                modelTblRatingTenant.addRow(new Object[]{
                    no++, p.getNoMeja(), r.getNilai(), r.getUlasan()
                });
            }
        }
    }

    // ── Tab T5: Tambah Menu ───────────────────────────────────────────────
    private JPanel buildTabTambahMenu() {
        JPanel panel = new JPanel(new BorderLayout(14, 0));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Form kiri
        JPanel formWrap = new JPanel(new BorderLayout(0, 12));
        formWrap.setBackground(COLOR_CARD);
        formWrap.setPreferredSize(new Dimension(300, 0));
        formWrap.add(makeLabel("Tambah Menu Baru", FONT_SUBTITLE, COLOR_TEXT), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 4, 8));
        form.setBackground(COLOR_CARD);

        form.add(makeLabel("Jenis:", FONT_BODY, COLOR_TEXT));
        JComboBox<String> cbJenis = new JComboBox<>(new String[]{"Makanan", "Minuman"});
        cbJenis.setFont(FONT_BODY);
        form.add(cbJenis);

        form.add(makeLabel("ID Menu (unik):", FONT_BODY, COLOR_TEXT));
        JTextField tfId = new JTextField(); tfId.setFont(FONT_BODY);
        form.add(tfId);

        form.add(makeLabel("Nama Menu:", FONT_BODY, COLOR_TEXT));
        JTextField tfNama = new JTextField(); tfNama.setFont(FONT_BODY);
        form.add(tfNama);

        form.add(makeLabel("Harga (Kupon):", FONT_BODY, COLOR_TEXT));
        JSpinner spHarga = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spHarga.setFont(FONT_BODY);
        form.add(spHarga);

        form.add(makeLabel("Stok Awal:", FONT_BODY, COLOR_TEXT));
        JSpinner spStok = new JSpinner(new SpinnerNumberModel(10, 0, 9999, 1));
        spStok.setFont(FONT_BODY);
        form.add(spStok);

        formWrap.add(form, BorderLayout.CENTER);

        JButton btnTambah = makePrimaryBtn("➕ Tambah Menu", 280, 44);
        btnTambah.addActionListener(e -> {
            String id   = tfId.getText().trim();
            String nama = tfNama.getText().trim();
            if (id.isEmpty() || nama.isEmpty()) {
                showErr("ID dan nama menu tidak boleh kosong."); return;
            }

            String jenis = (String) cbJenis.getSelectedItem();
            int harga = (int) spHarga.getValue();
            int stok  = (int) spStok.getValue();

            // Polymorphism: Menu bisa Makanan atau Minuman
            Menu menuBaru = "Makanan".equals(jenis)
                ? new Makanan(id, nama, harga)
                : new Minuman(id, nama, harga);

            boolean ok = manajemenMenu.tambahMenuKeDB(tenantLogin.getIdPengguna(), menuBaru, stok);
            if (ok) {
                tfId.setText(""); tfNama.setText("");
                spHarga.setValue(1); spStok.setValue(10);
                refreshMenuTenant();
                showInfo("Menu " + nama + " berhasil ditambahkan!");
            } else {
                showErr("Gagal menambah menu. ID mungkin sudah dipakai.");
            }
        });
        formWrap.add(btnTambah, BorderLayout.SOUTH);
        panel.add(formWrap, BorderLayout.WEST);

        // Tabel kanan: daftar menu saat ini
        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(COLOR_CARD);
        rightPanel.add(makeLabel("Menu Saat Ini", FONT_SUBTITLE, COLOR_TEXT), BorderLayout.NORTH);

        modelTblMenuTenant = newTableModel("ID", "Nama Menu", "Jenis", "Kupon");
        rightPanel.add(new JScrollPane(makeTable(modelTblMenuTenant)), BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.CENTER);

        return panel;
    }

    private void refreshMenuTenant() {
        if (modelTblMenuTenant == null || tenantLogin == null) return;
        modelTblMenuTenant.setRowCount(0);
        for (Menu m : tenantLogin.getDaftarMenu()) {
            modelTblMenuTenant.addRow(new Object[]{
                m.getIdProduk(), m.getNamaMenu(), m.getJenis(), m.getHargaKupon()
            });
        }
    }

    // ── Tab T6: Kelola Stok ───────────────────────────────────────────────
    private JPanel buildTabKelolastok() {
        JPanel panel = tabWrapper("Kelola Stok Menu");
        modelTblStok = newTableModel("No", "ID", "Nama Menu", "Jenis", "Kupon", "Stok");
        tblStok = makeTable(modelTblStok);
        panel.add(new JScrollPane(tblStok), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        south.setBackground(COLOR_CARD);
        
        JButton btnRefresh = makeSecondaryBtn("🔄 Refresh", 100, 32);
        btnRefresh.addActionListener(e -> refreshStokTenant());
        south.add(btnRefresh);

        south.add(makeLabel("Stok Baru:", FONT_BODY, COLOR_TEXT));
        JSpinner spStokBaru = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        spStokBaru.setFont(FONT_BODY);
        spStokBaru.setPreferredSize(new Dimension(80, 32));
        south.add(spStokBaru);

        JButton btnUpdate = makePrimaryBtn("💾 Update Stok", 140, 36);
        btnUpdate.addActionListener(e -> {
            int row = tblStok.getSelectedRow();
            if (row < 0) { showErr("Pilih menu yang ingin diubah stoknya."); return; }

            ArrayList<StokMenu> stoks = tenantLogin.getDaftarStokMenu();
            if (row >= stoks.size()) return;

            StokMenu sk     = stoks.get(row);
            int     stokBaru = (int) spStokBaru.getValue();
            boolean ok = manajemenMenu.updateStokKeDB(sk.getProduk().getIdProduk(), stokBaru);

            if (ok) {
                sk.setJumlahStok(stokBaru);
                refreshStokTenant();
                showInfo("Stok berhasil diubah menjadi " + stokBaru + ".");
            } else {
                showErr("Gagal update stok di database.");
            }
        });
        south.add(btnUpdate);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshStokTenant() {
        if (modelTblStok == null || tenantLogin == null) return;
        modelTblStok.setRowCount(0);
        ArrayList<StokMenu> stoks = tenantLogin.getDaftarStokMenu();
        for (int i = 0; i < stoks.size(); i++) {
            StokMenu s = stoks.get(i);
            Menu     m = s.getProduk();
            modelTblStok.addRow(new Object[]{
                i + 1, m.getIdProduk(), m.getNamaMenu(),
                m.getJenis(), m.getHargaKupon(), s.getJumlahStok()
            });
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  HELPER — KOMPONEN UI
    //  (Enkapsulasi: logika styling tersembunyi di method-method private ini)
    // ═══════════════════════════════════════════════════════════════════════
    private JLabel makeLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    /** Tombol utama (warna oranye). */
    private JButton makePrimaryBtn(String text, int w, int h) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setBackground(COLOR_PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(w, h));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(COLOR_PRIMARY_DARK); }
            public void mouseExited (MouseEvent e) { btn.setBackground(COLOR_PRIMARY); }
        });
        return btn;
    }

    /** Tombol sekunder (abu-abu terang). */
    private JButton makeSecondaryBtn(String text, int w, int h) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setBackground(new Color(225, 225, 225));
        btn.setForeground(COLOR_TEXT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(w, h));
        return btn;
    }

    /** Card putih dengan border tipis dan shadow ringan. */
    private JPanel createCard(int w, int h) {
        JPanel card = new JPanel();
        card.setBackground(COLOR_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(210, 210, 210), 1, true),
            new EmptyBorder(0, 0, 0, 0)
        ));
        card.setPreferredSize(new Dimension(w, h));
        return card;
    }

    /** JTable dengan styling seragam. */
    private JTable makeTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(COLOR_ROW_SEL);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(FONT_TH);
        table.getTableHeader().setBackground(new Color(242, 242, 242));
        return table;
    }

    /** DefaultTableModel dengan kolom varargs dan sel tidak bisa di-edit. */
    private DefaultTableModel newTableModel(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    /** JPanel dengan BorderLayout dan padding standar. */
    private JPanel wrapWithPadding(int v, int h) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(v, h, v, h));
        return panel;
    }

    /** Header panel (judul + subtitle + tombol kembali). */
    private JPanel buildHeader(String title, String subtitle, ActionListener backAction) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BG);
        header.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel left = new JPanel();
        left.setBackground(COLOR_BG);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(makeLabel(title, FONT_TITLE, COLOR_TEXT));
        if (!subtitle.isEmpty())
            left.add(makeLabel(subtitle, FONT_SMALL, COLOR_MUTED));
        header.add(left, BorderLayout.CENTER);

        if (backAction != null) {
            JButton btnBack = makeSecondaryBtn("← Kembali", 110, 32);
            btnBack.addActionListener(backAction);
            header.add(btnBack, BorderLayout.EAST);
        }
        return header;
    }

    /** Panel tab dengan header label + BorderLayout + background putih. */
    private JPanel tabWrapper(String judulTab) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.add(makeLabel(judulTab, FONT_SUBTITLE, COLOR_TEXT), BorderLayout.NORTH);
        return panel;
    }

    /** Bar bawah berisi tombol Refresh. */
    private JPanel refreshBar(ActionListener onRefresh) {
        JButton btnRefresh = makeSecondaryBtn("🔄 Refresh", 120, 32);
        btnRefresh.addActionListener(onRefresh);
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.setBackground(COLOR_CARD);
        bar.add(btnRefresh);
        return bar;
    }

    private void setColWidths(JTable tbl, int... widths) {
        for (int i = 0; i < widths.length; i++)
            tbl.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    private void showErr (String msg) {
        JOptionPane.showMessageDialog(this, msg, "Perhatian", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  MAIN
    // ═══════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        // Terapkan Look & Feel sistem agar tampilan lebih native
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        // Selalu jalankan GUI di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}
