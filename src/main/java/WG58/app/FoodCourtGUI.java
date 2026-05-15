package WG58.app;

import WG58.database.KonektorMySQL;
import WG58.pembayaran.PembayaranQRIS;
import WG58.pengguna.Meja;
import WG58.pengguna.Pelanggan;
import WG58.pengguna.Tenant;
import WG58.pesanan.ItemPesanan;
import WG58.pesanan.KuponDigital;
import WG58.pesanan.Rating;
import WG58.produk.Makanan;
import WG58.produk.Minuman;
import WG58.produk.Produk;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class FoodCourtGUI extends JFrame {
    private static final String ROLE = "ROLE";
    private static final String CUSTOMER_SCAN = "CUSTOMER_SCAN";
    private static final String CUSTOMER_MENU = "CUSTOMER_MENU";
    private static final String CUSTOMER_CHECKOUT = "CUSTOMER_CHECKOUT";
    private static final String CUSTOMER_PAYMENT = "CUSTOMER_PAYMENT";
    private static final String CUSTOMER_RECEIPT = "CUSTOMER_RECEIPT";
    private static final String CUSTOMER_RATING = "CUSTOMER_RATING";
    private static final String TENANT_LOGIN = "TENANT_LOGIN";
    private static final String TENANT_DASHBOARD = "TENANT_DASHBOARD";
    private static final String TENANT_MENU = "TENANT_MENU";
    private static final int NILAI_KURS = 5000;

    private static final Color BG = new Color(252, 248, 243);
    private static final Color CARD = Color.WHITE;
    private static final Color DARK = new Color(67, 40, 24);
    private static final Color MUTED = new Color(139, 115, 85);
    private static final Color LINE = new Color(230, 220, 210);
    private static final Color ACCENT = new Color(212, 110, 39);
    private static final Color ACCENT_DARK = new Color(165, 85, 30);
    private static final Color GREEN = new Color(95, 122, 63);
    private static final Color BLUE = new Color(101, 124, 137);

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final KonektorMySQL database = new KonektorMySQL("localhost", 3306, "wg58_foodcourt");
    private final List<Produk> daftarMenu = new ArrayList<>();
    private final DefaultListModel<String> keranjangModel = new DefaultListModel<>();
    private final DefaultListModel<String> tenantOrderModel = new DefaultListModel<>();
    private final List<KonektorMySQL.PesananTersimpan> tenantPesananAktif = new ArrayList<>();
    private final NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private final DateTimeFormatter waktuFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private JPanel menuGrid;
    private JLabel cartTotalLabel;
    private JLabel customerStatusLabel;
    private JLabel tenantStatusLabel;
    private JTextArea checkoutArea;
    private JTextArea paymentArea;
    private JTextArea receiptArea;
    private JTextArea tenantOrderArea;
    private JTextArea menuTenantArea;
    private JTextArea scanHistoryArea;
    private JComboBox<String> mejaCombo;
    private JList<String> tenantOrderList;
    private JComboBox<KonektorMySQL.ProdukStok> stokProdukCombo;
    private JSpinner stokSpinner;

    private Pelanggan pelanggan;
    private Tenant tenant;
    private Meja mejaAktif;
    private KuponDigital pesananAktif;
    private PembayaranQRIS pembayaranAktif;
    private String idPembayaranAktif;
    private String kodeQrisAktif;
    private boolean pembayaranBerhasil;
    private boolean pesananSiap;

    public FoodCourtGUI() {
        setTitle("WG58 Food Court");
        setSize(1024, 680);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(BG);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        boolean dbSiap = database.hubungkan();
        isiMenuAwal();

        mainPanel.setBackground(BG);
        mainPanel.add(panelRole(), ROLE);
        mainPanel.add(panelCustomerScan(), CUSTOMER_SCAN);
        mainPanel.add(panelCustomerMenu(), CUSTOMER_MENU);
        mainPanel.add(panelCustomerCheckout(), CUSTOMER_CHECKOUT);
        mainPanel.add(panelCustomerPayment(), CUSTOMER_PAYMENT);
        mainPanel.add(panelCustomerReceipt(), CUSTOMER_RECEIPT);
        mainPanel.add(panelCustomerRating(), CUSTOMER_RATING);
        mainPanel.add(panelTenantLogin(), TENANT_LOGIN);
        mainPanel.add(panelTenantDashboard(), TENANT_DASHBOARD);
        mainPanel.add(panelTenantMenu(), TENANT_MENU);

        add(mainPanel);
        tampil(ROLE);

        if (!dbSiap) {
            SwingUtilities.invokeLater(() -> info("Database belum terkoneksi.\n"
                    + database.getPesanKesalahanTerakhir()
                    + "\n\nPastikan MySQL aktif dan JDBC connector masuk ke classpath."));
        }
    }

    private void isiMenuAwal() {
        daftarMenu.clear();
        List<Produk> menuDatabase = database.ambilDaftarMenu();
        if (!menuDatabase.isEmpty()) {
            daftarMenu.addAll(menuDatabase);
            return;
        }

        daftarMenu.add(new Makanan("MKN-001", "Ayam Geprek", 3, "Level pedas bisa request"));
        daftarMenu.add(new Makanan("MKN-002", "Nasi Goreng", 4, "Telur mata sapi"));
        daftarMenu.add(new Makanan("MKN-003", "Mie Ayam", 3, "Pangsit kering"));
        daftarMenu.add(new Makanan("MKN-004", "Soto Ayam", 4, "Kuah hangat"));
        daftarMenu.add(new Minuman("MNM-001", "Es Teh", 1, true));
        daftarMenu.add(new Minuman("MNM-002", "Kopi Susu", 2, true));
        daftarMenu.add(new Minuman("MNM-003", "Air Mineral", 1, false));
    }

    private JPanel panelRole() {
        JPanel root = screen();
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        RoundedPanel hero = card(24);
        hero.setLayout(new BorderLayout(14, 14));
        hero.setBorder(new EmptyBorder(24, 24, 24, 24));
        hero.setPreferredSize(new Dimension(680, 360));

        JPanel text = transparentBox(BoxLayout.Y_AXIS);
        text.add(title("WG58 Food Court", 28));
        text.add(Box.createVerticalStrut(4));
        text.add(label("Sistem kasir cerdas dengan pembayaran QRIS dan manajemen tenant.", 14, MUTED));

        JPanel roleCards = new JPanel(new GridLayout(1, 2, 16, 0));
        roleCards.setOpaque(false);
        roleCards.add(roleCard("Pelanggan", "Pesan menu & scan meja", "P", () -> {
            buatSesiPelangganBaru();
            tampil(CUSTOMER_SCAN);
        }));
        roleCards.add(roleCard("Tenant", "Kelola pesanan & menu", "T", () -> tampil(TENANT_LOGIN)));

        hero.add(text, BorderLayout.NORTH);
        hero.add(roleCards, BorderLayout.CENTER);

        center.add(hero);
        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private JPanel panelCustomerScan() {
        JPanel root = screen();
        root.add(topBar("Pelanggan", "Scan meja", true, true), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        RoundedPanel card = card(20);
        card.setLayout(new BorderLayout(14, 14));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(680, 420));

        JPanel scrollContent = transparentBox(BoxLayout.Y_AXIS);

        JLabel qr = new JLabel("QR", SwingConstants.CENTER);
        qr.setFont(new Font("SansSerif", Font.BOLD, 42));
        qr.setForeground(DARK);
        RoundedPanel qrBox = new RoundedPanel(20, new Color(252, 235, 219));
        qrBox.setLayout(new BorderLayout());
        qrBox.setPreferredSize(new Dimension(130, 130));
        qrBox.add(qr, BorderLayout.CENTER);

        mejaCombo = new JComboBox<>(ambilPilihanMeja());
        mejaCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        mejaCombo.setPreferredSize(new Dimension(220, 38));
        mejaCombo.addActionListener(e -> updateScanHistoryPreview());

        JPanel form = transparentBox(BoxLayout.Y_AXIS);
        form.add(label("Nomor Meja", 13, MUTED));
        form.add(Box.createVerticalStrut(4));
        form.add(mejaCombo);
        form.add(Box.createVerticalStrut(14));
        form.add(primaryButton("Buka Sesi Meja", this::bukaSesiMeja));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        topRow.setOpaque(false);
        topRow.add(qrBox);
        topRow.add(form);

        scanHistoryArea = readArea(5, 36);
        scanHistoryArea.setText("Pilih meja untuk melihat history pesanan.");

        scrollContent.add(label("Pilih meja", 22, DARK));
        scrollContent.add(Box.createVerticalStrut(12));
        scrollContent.add(topRow);
        scrollContent.add(Box.createVerticalStrut(16));
        scrollContent.add(cleanScroll(scanHistoryArea));

        JScrollPane mainScroll = cleanScroll(scrollContent);
        mainScroll.setBorder(null);
        card.add(mainScroll, BorderLayout.CENTER);

        center.add(card);
        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private JPanel panelCustomerMenu() {
        JPanel root = screen();
        root.add(topBar("Pelanggan", "Menu", true, true), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 0));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 16, 16, 16));

        RoundedPanel left = card(18);
        left.setLayout(new BorderLayout(10, 10));
        left.setBorder(new EmptyBorder(12, 12, 12, 12));
        left.add(sectionHeader("Daftar Menu", "Pilih menu favorit"), BorderLayout.NORTH);
        menuGrid = new JPanel();
        menuGrid.setOpaque(false);
        menuGrid.setLayout(new BoxLayout(menuGrid, BoxLayout.Y_AXIS));
        left.add(cleanScroll(menuGrid), BorderLayout.CENTER);

        RoundedPanel right = card(18);
        right.setLayout(new BorderLayout(10, 10));
        right.setPreferredSize(new Dimension(260, 1));
        right.setBorder(new EmptyBorder(12, 12, 12, 12));
        right.add(sectionHeader("Keranjang", ""), BorderLayout.NORTH);

        JList<String> keranjangList = new JList<>(keranjangModel);
        keranjangList.setFont(new Font("SansSerif", Font.PLAIN, 12));
        keranjangList.setFixedCellHeight(28);
        right.add(cleanScroll(keranjangList), BorderLayout.CENTER);

        JPanel cartBottom = transparentBox(BoxLayout.Y_AXIS);
        cartTotalLabel = label("Total: 0 kupon", 14, DARK);
        cartBottom.add(cartTotalLabel);
        cartBottom.add(Box.createVerticalStrut(8));
        cartBottom.add(primaryButton("Checkout", () -> {
            if (!punyaItem()) {
                info("Keranjang masih kosong.");
                return;
            }
            updateCheckout();
            tampil(CUSTOMER_CHECKOUT);
        }));
        cartBottom.add(Box.createVerticalStrut(4));
        cartBottom.add(ghostButton("Kosongkan", () -> {
            resetPesananAktif();
            updateCart();
            updateMenuGrid();
        }));
        right.add(cartBottom, BorderLayout.SOUTH);

        content.add(left, BorderLayout.CENTER);
        content.add(right, BorderLayout.EAST);
        root.add(content, BorderLayout.CENTER);
        return root;
    }

    private JPanel panelCustomerCheckout() {
        JPanel root = screen();
        root.add(topBar("Pelanggan", "Checkout", true, true), BorderLayout.NORTH);

        RoundedPanel card = centerCard(root, 640, 420);
        card.setLayout(new BorderLayout(12, 12));
        card.add(sectionHeader("Ringkasan Pesanan", ""), BorderLayout.NORTH);
        checkoutArea = readArea(10, 40);
        card.add(cleanScroll(checkoutArea), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);
        actions.add(ghostButton("Menu", () -> tampil(CUSTOMER_MENU)));
        actions.add(primaryButton("Bayar QRIS", () -> {
            if (pesananAktif == null) {
                info("Pesanan belum ada.");
                return;
            }
            pembayaranAktif = new PembayaranQRIS(pesananAktif);
            updatePayment();
            tampil(CUSTOMER_PAYMENT);
        }));
        card.add(actions, BorderLayout.SOUTH);
        return root;
    }

    private JPanel panelCustomerPayment() {
        JPanel root = screen();
        root.add(topBar("Pelanggan", "Pembayaran", true, true), BorderLayout.NORTH);

        RoundedPanel card = centerCard(root, 660, 440);
        card.setLayout(new BorderLayout(10, 10));
        card.add(sectionHeader("QRIS", ""), BorderLayout.NORTH);

        JPanel scrollContent = transparentBox(BoxLayout.Y_AXIS);

        JPanel body = new JPanel(new GridLayout(1, 2, 12, 0));
        body.setOpaque(false);

        RoundedPanel qrBox = new RoundedPanel(18, DARK);
        qrBox.setLayout(new BorderLayout());
        qrBox.setPreferredSize(new Dimension(220, 220));
        JLabel qrisLabel = new JLabel("QRIS", SwingConstants.CENTER);
        qrisLabel.setForeground(Color.WHITE);
        qrisLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        qrBox.add(qrisLabel, BorderLayout.CENTER);

        JPanel form = transparentBox(BoxLayout.Y_AXIS);
        paymentArea = readArea(6, 24);
        JTextField nominalField = inputField();
        form.add(cleanScroll(paymentArea));
        form.add(Box.createVerticalStrut(6));
        form.add(label("Nominal Bayar", 12, MUTED));
        form.add(Box.createVerticalStrut(4));
        form.add(nominalField);
        form.add(Box.createVerticalStrut(8));
        form.add(primaryButton("Bayar", () -> prosesBayar(nominalField)));
        form.add(Box.createVerticalStrut(4));
        form.add(ghostButton("Kembali", () -> tampil(CUSTOMER_CHECKOUT)));

        body.add(qrBox);
        body.add(form);
        
        scrollContent.add(body);
        
        JScrollPane mainScroll = cleanScroll(scrollContent);
        mainScroll.setBorder(null);
        card.add(mainScroll, BorderLayout.CENTER);
        
        return root;
    }

    private JPanel panelCustomerReceipt() {
        JPanel root = screen();
        root.add(topBar("Pelanggan", "Struk", true, true), BorderLayout.NORTH);

        RoundedPanel card = centerCard(root, 680, 460);
        card.setLayout(new BorderLayout(12, 12));
        card.add(sectionHeader("Struk Digital", ""), BorderLayout.NORTH);
        receiptArea = readArea(10, 40);
        card.add(cleanScroll(receiptArea), BorderLayout.CENTER);

        customerStatusLabel = label("", 13, MUTED);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);
        actions.add(customerStatusLabel);
        actions.add(ghostButton("Refresh", this::refreshReceipt));
        actions.add(primaryButton("Rating", () -> {
            if (!pesananSiap) {
                info("Pesanan belum siap diambil.");
                return;
            }
            tampil(CUSTOMER_RATING);
        }));
        card.add(actions, BorderLayout.SOUTH);
        return root;
    }

    private JPanel panelCustomerRating() {
        JPanel root = screen();
        root.add(topBar("Pelanggan", "Rating", true, true), BorderLayout.NORTH);

        RoundedPanel card = centerCard(root, 540, 380);
        card.setLayout(new BorderLayout(12, 12));
        card.add(sectionHeader("Beri Rating", ""), BorderLayout.NORTH);

        JPanel form = transparentBox(BoxLayout.Y_AXIS);
        JSpinner nilai = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        nilai.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JTextArea ulasan = new JTextArea(4, 32);
        ulasan.setLineWrap(true);
        ulasan.setWrapStyleWord(true);
        ulasan.setFont(new Font("SansSerif", Font.PLAIN, 13));
        ulasan.setBorder(new EmptyBorder(8, 8, 8, 8));

        form.add(label("Nilai (1-5)", 12, MUTED));
        form.add(Box.createVerticalStrut(4));
        form.add(nilai);
        form.add(Box.createVerticalStrut(10));
        form.add(label("Ulasan", 12, MUTED));
        form.add(Box.createVerticalStrut(4));
        form.add(cleanScroll(ulasan));
        card.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);
        actions.add(primaryButton("Kirim", () -> {
            if (pesananAktif == null) {
                info("Tidak ada pesanan untuk diberi rating.");
                return;
            }

            String isiUlasan = ulasan.getText().trim().isEmpty() ? "Tidak ada ulasan" : ulasan.getText().trim();
            Rating rating = new Rating(pesananAktif, (Integer) nilai.getValue(), isiUlasan);
            boolean tersimpan = database.simpanRating(pesananAktif.getIdPesanan(), rating.getNilai(), rating.getUlasan());
            if (!tersimpan) {
                info("Rating gagal disimpan ke database.");
                return;
            }

            database.simpanKeDB(rating.getInfo());
            info("Rating tersimpan. Terima kasih.");
            buatSesiPelangganBaru();
            tampil(ROLE);
        }));
        card.add(actions, BorderLayout.SOUTH);
        return root;
    }

    private JPanel panelTenantLogin() {
        JPanel root = screen();
        root.add(topBar("Pemilik Tenant", "Login", true, true), BorderLayout.NORTH);

        RoundedPanel card = centerCard(root, 440, 340);
        card.setLayout(new BorderLayout(12, 12));
        card.add(sectionHeader("Masuk Tenant", ""), BorderLayout.NORTH);

        JPanel form = transparentBox(BoxLayout.Y_AXIS);
        JTextField username = inputField();
        JPasswordField password = new JPasswordField();
        password.setFont(new Font("SansSerif", Font.PLAIN, 13));
        password.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE), new EmptyBorder(8, 10, 8, 10)));
        password.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        form.add(label("Username", 12, MUTED));
        form.add(Box.createVerticalStrut(4));
        form.add(username);
        form.add(Box.createVerticalStrut(10));
        form.add(label("Password", 12, MUTED));
        form.add(Box.createVerticalStrut(4));
        form.add(password);
        card.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);
        actions.add(primaryButton("Masuk", () -> {
            String user = username.getText().trim();
            String pass = String.valueOf(password.getPassword()).trim();
            if (user.isEmpty() || pass.isEmpty()) {
                info("Username dan password wajib diisi.");
                return;
            }
            tenant = new Tenant("TNT-" + randomPendek(), user, pass);
            if (!tenant.aksesSistem()) {
                info("Login tenant gagal.");
                return;
            }
            updateTenantDashboard();
            tampil(TENANT_DASHBOARD);
        }));
        card.add(actions, BorderLayout.SOUTH);
        return root;
    }

    private JPanel panelTenantDashboard() {
        JPanel root = screen();
        root.add(tenantTopBar("Dashboard Tenant"), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(14, 0));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 16, 16, 16));

        RoundedPanel left = card(18);
        left.setLayout(new BorderLayout(10, 10));
        left.setBorder(new EmptyBorder(12, 12, 12, 12));
        left.add(sectionHeader("Pesanan Masuk", ""), BorderLayout.NORTH);

        tenantOrderList = new JList<>(tenantOrderModel);
        tenantOrderList.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tenantOrderList.setFixedCellHeight(26);
        tenantOrderList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                tampilkanPesananTenantTerpilih();
            }
        });

        tenantOrderArea = readArea(12, 40);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cleanScroll(tenantOrderList), cleanScroll(tenantOrderArea));
        split.setResizeWeight(0.32);
        split.setBorder(null);
        left.add(split, BorderLayout.CENTER);

        JPanel rightWrapper = transparentBox(BoxLayout.Y_AXIS);
        JPanel actions = new JPanel(new GridLayout(5, 1, 0, 6));
        actions.setOpaque(false);
        actions.add(primaryButton("Masak", () -> ubahStatusTenant("Diproses")));
        actions.add(primaryButton("Siap", () -> ubahStatusTenant("Siap Diambil")));
        actions.add(ghostButton("Riwayat", this::tampilkanRiwayatTenant));
        actions.add(ghostButton("Refresh", this::updateTenantDashboard));
        actions.add(ghostButton("Menu", () -> {
            updateTenantMenuText();
            tampil(TENANT_MENU);
        }));

        RoundedPanel right = card(18);
        right.setLayout(new BorderLayout(10, 10));
        right.setPreferredSize(new Dimension(220, 1));
        right.setBorder(new EmptyBorder(12, 12, 12, 12));
        right.add(sectionHeader("Aksi", ""), BorderLayout.NORTH);
        tenantStatusLabel = label("", 12, MUTED);
        
        rightWrapper.add(tenantStatusLabel);
        rightWrapper.add(Box.createVerticalStrut(8));
        rightWrapper.add(actions);
        
        JScrollPane scrollRight = cleanScroll(rightWrapper);
        scrollRight.setBorder(null);
        right.add(scrollRight, BorderLayout.CENTER);

        content.add(left, BorderLayout.CENTER);
        content.add(right, BorderLayout.EAST);
        root.add(content, BorderLayout.CENTER);
        return root;
    }

    private JPanel panelTenantMenu() {
        JPanel root = screen();
        root.add(tenantTopBar("Kelola Menu"), BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 12, 0));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 16, 16, 16));

        RoundedPanel listCard = card(18);
        listCard.setLayout(new BorderLayout(10, 10));
        listCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        listCard.add(sectionHeader("Menu Aktif", ""), BorderLayout.NORTH);
        menuTenantArea = readArea(14, 30);
        listCard.add(cleanScroll(menuTenantArea), BorderLayout.CENTER);

        RoundedPanel formCard = card(18);
        formCard.setLayout(new BorderLayout(10, 10));
        formCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        formCard.add(sectionHeader("Tambah & Stok", ""), BorderLayout.NORTH);

        JPanel formScrollContent = transparentBox(BoxLayout.Y_AXIS);
        JPanel form = transparentBox(BoxLayout.Y_AXIS);
        JTextField nama = inputField();
        JSpinner harga = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        harga.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JSpinner stokAwal = new JSpinner(new SpinnerNumberModel(30, 0, 999, 1));
        stokAwal.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JComboBox<String> tipe = new JComboBox<>(new String[]{"Makanan", "Minuman"});
        tipe.setFont(new Font("SansSerif", Font.PLAIN, 13));
        stokProdukCombo = new JComboBox<>();
        stokProdukCombo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        stokSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        stokSpinner.setFont(new Font("SansSerif", Font.PLAIN, 13));
        stokProdukCombo.addActionListener(e -> {
            KonektorMySQL.ProdukStok produkStok = (KonektorMySQL.ProdukStok) stokProdukCombo.getSelectedItem();
            if (produkStok != null) {
                stokSpinner.setValue(produkStok.getStok());
            }
        });

        form.add(label("Nama", 11, MUTED));
        form.add(Box.createVerticalStrut(3));
        form.add(nama);
        form.add(Box.createVerticalStrut(8));
        form.add(label("Harga", 11, MUTED));
        form.add(Box.createVerticalStrut(3));
        form.add(harga);
        form.add(Box.createVerticalStrut(8));
        form.add(label("Tipe", 11, MUTED));
        form.add(Box.createVerticalStrut(3));
        form.add(tipe);
        form.add(Box.createVerticalStrut(8));
        form.add(label("Stok", 11, MUTED));
        form.add(Box.createVerticalStrut(3));
        form.add(stokAwal);
        form.add(Box.createVerticalStrut(12));
        form.add(label("Atur Stok", 11, MUTED));
        form.add(Box.createVerticalStrut(3));
        form.add(stokProdukCombo);
        form.add(Box.createVerticalStrut(3));
        form.add(stokSpinner);

        JPanel actions = new JPanel(new GridLayout(3, 1, 0, 6));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(8, 0, 0, 0));
        actions.add(primaryButton("Tambah", () -> {
            String namaMenu = nama.getText().trim();
            if (namaMenu.isEmpty()) {
                info("Nama menu wajib diisi.");
                return;
            }

            int hargaKupon = (Integer) harga.getValue();
            Produk produk;
            if ("Makanan".equals(tipe.getSelectedItem())) {
                produk = new Makanan("MKN-" + randomPendek(), namaMenu, hargaKupon, "Menu tenant");
            } else {
                produk = new Minuman("MNM-" + randomPendek(), namaMenu, hargaKupon, true);
            }

            if (!database.tambahMenu(produk, (Integer) stokAwal.getValue())) {
                info("Menu gagal disimpan ke database.\n" + database.getPesanKesalahanTerakhir());
                return;
            }

            isiMenuAwal();
            nama.setText("");
            updateTenantMenuText();
            updateMenuGrid();
            info("Menu ditambahkan.");
        }));
        actions.add(primaryButton("Update Stok", () -> {
            KonektorMySQL.ProdukStok produkStok = (KonektorMySQL.ProdukStok) stokProdukCombo.getSelectedItem();
            if (produkStok == null) {
                info("Pilih produk dulu.");
                return;
            }
            if (!database.updateStokProduk(produkStok.getIdProduk(), (Integer) stokSpinner.getValue())) {
                info("Stok gagal diupdate.\n" + database.getPesanKesalahanTerakhir());
                return;
            }
            updateTenantMenuText();
            updateMenuGrid();
            info("Stok produk diupdate.");
        }));
        actions.add(ghostButton("Dashboard", () -> {
            updateTenantDashboard();
            tampil(TENANT_DASHBOARD);
        }));

        formScrollContent.add(form);
        formScrollContent.add(actions);
        
        JScrollPane scrollForm = cleanScroll(formScrollContent);
        scrollForm.setBorder(null);
        formCard.add(scrollForm, BorderLayout.CENTER);

        content.add(listCard);
        content.add(formCard);
        root.add(content, BorderLayout.CENTER);
        return root;
    }

    private JPanel topBar(String title, String subtitle, boolean roleButton, boolean exitButton) {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(24, 28, 20, 28));

        JPanel text = transparentBox(BoxLayout.Y_AXIS);
        text.add(label(title, 30, DARK));
        text.add(Box.createVerticalStrut(4));
        text.add(label(subtitle, 15, MUTED));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        if (roleButton) {
            actions.add(ghostButton("Pilihan Peran", () -> tampil(ROLE)));
        }
        if (exitButton) {
            actions.add(ghostButton("Keluar", this::keluarAplikasi));
        }

        top.add(text, BorderLayout.WEST);
        top.add(actions, BorderLayout.EAST);
        return top;
    }

    private JPanel tenantTopBar(String title) {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(24, 28, 20, 28));

        JPanel text = transparentBox(BoxLayout.Y_AXIS);
        text.add(label(title, 30, DARK));
        text.add(Box.createVerticalStrut(4));
        String namaTenant = tenant == null ? "Tenant" : tenant.getUsername();
        text.add(label(namaTenant, 15, MUTED));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(ghostButton("Logout", () -> {
            tenant = null;
            tampil(ROLE);
        }));
        actions.add(ghostButton("Keluar", this::keluarAplikasi));

        top.add(text, BorderLayout.WEST);
        top.add(actions, BorderLayout.EAST);
        return top;
    }

    private RoundedPanel centerCard(JPanel root, int width, int height) {
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        RoundedPanel card = card(24);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setPreferredSize(new Dimension(width, height));
        center.add(card);
        root.add(center, BorderLayout.CENTER);
        return card;
    }

    private JPanel roleCard(String title, String subtitle, String icon, Runnable action) {
        RoundedPanel panel = new RoundedPanel(26, new Color(254, 251, 247));
        panel.setBorder(new EmptyBorder(22, 22, 22, 22));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 42));
        iconLabel.setForeground(ACCENT);

        JPanel text = transparentBox(BoxLayout.Y_AXIS);
        text.add(iconLabel);
        text.add(Box.createVerticalStrut(14));
        text.add(label(title, 22, DARK));
        text.add(Box.createVerticalStrut(4));
        text.add(label(subtitle, 14, MUTED));

        panel.add(text, BorderLayout.CENTER);
        panel.add(primaryButton("Pilih", action), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel menuCard(Produk produk) {
        RoundedPanel panel = new RoundedPanel(22, Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setLayout(new BorderLayout(14, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        RoundedPanel badge = new RoundedPanel(18, new Color(252, 235, 219));
        badge.setPreferredSize(new Dimension(68, 68));
        badge.setLayout(new BorderLayout());
        String icon = produk instanceof Minuman ? "MNM" : "MKN";
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        iconLabel.setForeground(DARK);
        badge.add(iconLabel, BorderLayout.CENTER);

        JPanel info = transparentBox(BoxLayout.Y_AXIS);
        info.add(label(produk.getNamaMenu(), 18, DARK));
        info.add(Box.createVerticalStrut(4));
        info.add(label(produk.ambilDetail(), 13, MUTED));
        info.add(Box.createVerticalStrut(6));
        info.add(label(produk.getHargaKupon() + " kupon", 14, ACCENT_DARK));

        JPanel action = transparentBox(BoxLayout.Y_AXIS);
        JSpinner qty = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        qty.setMaximumSize(new Dimension(80, 32));
        action.add(qty);
        action.add(Box.createVerticalStrut(8));
        action.add(primaryButton("Tambah", () -> {
            if (pesananAktif == null) {
                resetPesananAktif();
            }
            int jumlah = (Integer) qty.getValue();
            pesananAktif.tambahMenu(produk, jumlah);
            sinkronkanKeranjangDariPesanan();
            updateCart();
        }));

        panel.add(badge, BorderLayout.WEST);
        panel.add(info, BorderLayout.CENTER);
        panel.add(action, BorderLayout.EAST);
        return panel;
    }

    private JPanel sectionHeader(String title, String subtitle) {
        JPanel panel = new BorderLayoutPanel();
        JPanel text = transparentBox(BoxLayout.Y_AXIS);
        text.add(label(title, 20, DARK));
        if (subtitle != null && !subtitle.isBlank()) {
            text.add(Box.createVerticalStrut(2));
            text.add(label(subtitle, 13, MUTED));
        }
        panel.add(text, BorderLayout.WEST);
        return panel;
    }

    private void buatSesiPelangganBaru() {
        pelanggan = new Pelanggan("PLG-" + randomPendek(), "-", 0);
        pelanggan.aksesSistem();
        mejaAktif = null;
        pembayaranAktif = null;
        idPembayaranAktif = null;
        kodeQrisAktif = null;
        pembayaranBerhasil = false;
        pesananSiap = false;
        keranjangModel.clear();
        pesananAktif = null;
        updateCart();
    }

    private void resetPesananAktif() {
        pesananAktif = new KuponDigital("PSN-" + randomPendek(), "Draft", NILAI_KURS);
        pembayaranAktif = null;
        idPembayaranAktif = null;
        kodeQrisAktif = null;
        pembayaranBerhasil = false;
        pesananSiap = false;
        keranjangModel.clear();
    }

    private void bukaSesiMeja() {
        if (pelanggan == null) {
            buatSesiPelangganBaru();
        }

        String nomor = String.valueOf(mejaCombo.getSelectedItem());
        mejaAktif = new Meja(nomor);
        pelanggan.pindaiQRMeja(mejaAktif);
        database.simpanMeja(nomor);

        KonektorMySQL.PesananTersimpan pesananTersimpan = database.ambilPesananAktifByMeja(nomor);
        if (pesananTersimpan != null) {
            pakaiPesananTersimpan(pesananTersimpan);
            if (pembayaranBerhasil) {
                info("Pesanan aktif untuk meja ini ditemukan. Status terbaru diambil dari database.");
                tampil(CUSTOMER_RECEIPT);
                return;
            }
        } else {
            resetPesananAktif();
        }

        updateMenuGrid();
        updateCart();
        tampil(CUSTOMER_MENU);
    }

    private void pakaiPesananTersimpan(KonektorMySQL.PesananTersimpan data) {
        if (data == null) {
            return;
        }

        pesananAktif = data.getPesanan();
        pembayaranBerhasil = data.isStatusBayar();
        pesananSiap = isStatusSiap(pesananAktif.getStatusPesanan());
        idPembayaranAktif = data.getIdPembayaran();
        kodeQrisAktif = data.getKodeQris();
        sinkronkanKeranjangDariPesanan();
        pembayaranAktif = null;
    }

    private void sinkronkanKeranjangDariPesanan() {
        keranjangModel.clear();
        if (pesananAktif == null) {
            return;
        }
        for (ItemPesanan item : pesananAktif.getDaftarItem()) {
            keranjangModel.addElement(item.getInfo());
        }
    }

    private void updateMenuGrid() {
        if (menuGrid == null) {
            return;
        }
        isiMenuAwal();
        menuGrid.removeAll();
        for (Produk produk : daftarMenu) {
            menuGrid.add(menuCard(produk));
            menuGrid.add(Box.createVerticalStrut(12));
        }
        menuGrid.revalidate();
        menuGrid.repaint();
    }

    private void updateCart() {
        if (cartTotalLabel == null) {
            return;
        }
        int kupon = pesananAktif == null ? 0 : pesananAktif.getTotalKuponDigital();
        cartTotalLabel.setText("Total: " + kupon + " kupon | " + formatRupiah(kupon * NILAI_KURS));
    }

    private boolean punyaItem() {
        return pesananAktif != null && !pesananAktif.getDaftarItem().isEmpty();
    }

    private void updateCheckout() {
        if (checkoutArea != null) {
            checkoutArea.setText(ringkasanPesanan());
        }
    }

    private void updatePayment() {
        if (paymentArea == null || pesananAktif == null || pembayaranAktif == null) {
            return;
        }
        paymentArea.setText("ID Pesanan\n" + pesananAktif.getIdPesanan()
                + "\n\nKode QRIS\n" + pembayaranAktif.getKodeQR()
                + "\n\nTotal\n" + formatRupiah((int) pesananAktif.konversiKeRupiah()));
    }

    private void prosesBayar(JTextField nominalField) {
        if (pesananAktif == null || pembayaranAktif == null || mejaAktif == null || pelanggan == null) {
            info("Sesi pembayaran belum siap.");
            return;
        }

        String angka = nominalField.getText().replaceAll("[^0-9]", "");
        if (angka.isEmpty()) {
            info("Nominal wajib diisi.");
            return;
        }

        int nominal = Integer.parseInt(angka);
        int total = (int) pesananAktif.konversiKeRupiah();
        if (nominal != total) {
            info("Nominal belum sesuai.");
            return;
        }

        if (!database.stokCukup(pesananAktif)) {
            info("Pesanan belum bisa dibayar.\n" + database.getPesanKesalahanTerakhir());
            return;
        }

        pembayaranAktif.prosesPembayaran();
        pembayaranBerhasil = true;
        pesananSiap = false;
        idPembayaranAktif = pembayaranAktif.getIdPembayaran();
        kodeQrisAktif = pembayaranAktif.getKodeQR();

        boolean tersimpan = database.simpanPesanan(
                pelanggan.getIdPengguna(),
                mejaAktif.getNoMeja(),
                pesananAktif,
                idPembayaranAktif,
                kodeQrisAktif,
                true
        );
        if (!tersimpan) {
            info("Pesanan gagal disimpan ke database.\n" + database.getPesanKesalahanTerakhir());
            return;
        }

        database.simpanKeDB(ringkasanPesanan());
        refreshReceipt();
        tampil(CUSTOMER_RECEIPT);
    }

    private void updateReceipt() {
        if (receiptArea == null || pesananAktif == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(ringkasanPesanan())
                .append("\nPembayaran : ").append(pembayaranBerhasil ? "Berhasil" : "Belum")
                .append("\nStatus     : ").append(pesananAktif.getStatusPesanan())
                .append("\nKode QRIS  : ").append(kodeQrisAktif == null ? "-" : kodeQrisAktif)
                .append("\n\nHistory Meja:\n")
                .append(ringkasanRiwayatMeja());

        receiptArea.setText(sb.toString());
        if (customerStatusLabel != null) {
            customerStatusLabel.setText(pesananSiap ? "Siap diambil" : pesananAktif.getStatusPesanan());
            customerStatusLabel.setForeground(pesananSiap ? GREEN : MUTED);
        }
    }

    private void refreshReceipt() {
        if (pesananAktif == null) {
            return;
        }

        KonektorMySQL.PesananTersimpan data = database.ambilPesananById(pesananAktif.getIdPesanan());
        if (data != null) {
            pakaiPesananTersimpan(data);
            if (mejaAktif == null && data.getNoMeja() != null) {
                mejaAktif = new Meja(data.getNoMeja());
            }
        }

        updateReceipt();
        if (pesananSiap) {
            info("Pesanan siap diambil.");
        }
    }

    private void updateTenantDashboard() {
        if (tenantOrderArea == null) {
            return;
        }

        tenantPesananAktif.clear();
        tenantPesananAktif.addAll(database.ambilSemuaPesananAktif());
        tenantOrderModel.clear();

        for (KonektorMySQL.PesananTersimpan pesanan : tenantPesananAktif) {
            tenantOrderModel.addElement(labelPesananTenant(pesanan));
        }

        if (tenantPesananAktif.isEmpty()) {
            tenantOrderArea.setText("Belum ada pesanan aktif di database.");
            if (tenantStatusLabel != null) {
                tenantStatusLabel.setText("0 pesanan aktif");
                tenantStatusLabel.setForeground(MUTED);
            }
            return;
        }

        if (tenantOrderList != null && tenantOrderList.getSelectedIndex() < 0) {
            tenantOrderList.setSelectedIndex(0);
        } else {
            tampilkanPesananTenantTerpilih();
        }
        if (tenantStatusLabel != null) {
            tenantStatusLabel.setText(tenantPesananAktif.size() + " pesanan aktif");
            tenantStatusLabel.setForeground(BLUE);
        }
    }

    private void ubahStatusTenant(String status) {
        if (tenant == null) {
            info("Tenant belum login.");
            return;
        }

        KonektorMySQL.PesananTersimpan aktif = pesananTenantTerpilih();
        if (aktif == null) {
            info("Pilih pesanan masuk dulu.");
            return;
        }

        mejaAktif = new Meja(aktif.getNoMeja());
        pakaiPesananTersimpan(aktif);
        tenant.ubahStatusPesanan(pesananAktif, status);
        pesananSiap = isStatusSiap(status);

        boolean berhasil = database.updateStatusPesanan(pesananAktif.getIdPesanan(), status);
        if (!berhasil) {
            info("Status pesanan gagal diupdate ke database.\n" + database.getPesanKesalahanTerakhir());
            return;
        }

        database.simpanKeDB("Status " + pesananAktif.getIdPesanan() + " = " + status);
        updateTenantDashboard();
    }

    private void updateTenantMenuText() {
        if (menuTenantArea == null) {
            return;
        }
        isiMenuAwal();
        StringBuilder sb = new StringBuilder();
        List<KonektorMySQL.ProdukStok> stokProduk = database.ambilSemuaMenuDenganStok();
        for (KonektorMySQL.ProdukStok produk : stokProduk) {
            sb.append(produk.getIdProduk()).append(" | ").append(produk.getNamaMenu()).append("\n");
            sb.append(produk.getTipe()).append(" | ")
                    .append(produk.getHargaKupon()).append(" kupon | ")
                    .append(formatRupiah(produk.getHargaKupon() * NILAI_KURS)).append("\n");
            sb.append("Stok: ").append(produk.getStok())
                    .append(" | Status: ").append(produk.isAktif() ? "Aktif" : "Nonaktif")
                    .append("\n\n");
        }
        menuTenantArea.setText(sb.toString());
        refreshStokCombo(stokProduk);
    }

    private void tampilkanPesananTenantTerpilih() {
        if (tenantOrderArea == null) {
            return;
        }

        KonektorMySQL.PesananTersimpan aktif = pesananTenantTerpilih();
        if (aktif == null) {
            tenantOrderArea.setText("Pilih pesanan untuk melihat detail.");
            return;
        }

        mejaAktif = new Meja(aktif.getNoMeja());
        pakaiPesananTersimpan(aktif);
        tenantOrderArea.setText(ringkasanPesanan()
                + "\nWaktu Masuk : " + aktif.getDibuatPada().format(waktuFormatter)
                + "\nUpdate Terakhir : " + aktif.getDiubahPada().format(waktuFormatter)
                + "\n\nHistory Meja:\n" + ringkasanRiwayatMeja());
    }

    private KonektorMySQL.PesananTersimpan pesananTenantTerpilih() {
        int index = tenantOrderList == null ? -1 : tenantOrderList.getSelectedIndex();
        if (index < 0 || index >= tenantPesananAktif.size()) {
            return null;
        }
        return tenantPesananAktif.get(index);
    }

    private String labelPesananTenant(KonektorMySQL.PesananTersimpan data) {
        return data.getPesanan().getIdPesanan()
                + " | Meja " + data.getNoMeja()
                + " | " + data.getPesanan().getStatusPesanan();
    }

    private void tampilkanRiwayatTenant() {
        if (tenantOrderArea == null) {
            return;
        }

        List<KonektorMySQL.PesananTersimpan> riwayat = database.ambilRiwayatPesanan(30);
        if (riwayat.isEmpty()) {
            tenantOrderArea.setText("Belum ada riwayat pesanan.");
            return;
        }

        StringBuilder sb = new StringBuilder("Riwayat Pesanan:\n\n");
        for (KonektorMySQL.PesananTersimpan data : riwayat) {
            sb.append(data.getPesanan().getIdPesanan())
                    .append(" | Meja ").append(data.getNoMeja())
                    .append(" | ").append(data.getPesanan().getStatusPesanan())
                    .append(" | ").append(formatRupiah((int) data.getPesanan().konversiKeRupiah()))
                    .append(" | ").append(data.getDibuatPada().format(waktuFormatter))
                    .append("\n");
        }
        tenantOrderArea.setText(sb.toString());
    }

    private void refreshStokCombo(List<KonektorMySQL.ProdukStok> produkStok) {
        if (stokProdukCombo == null) {
            return;
        }

        KonektorMySQL.ProdukStok sebelumnya = (KonektorMySQL.ProdukStok) stokProdukCombo.getSelectedItem();
        String idSebelumnya = sebelumnya == null ? null : sebelumnya.getIdProduk();
        stokProdukCombo.removeAllItems();

        int selectedIndex = -1;
        for (int i = 0; i < produkStok.size(); i++) {
            KonektorMySQL.ProdukStok item = produkStok.get(i);
            stokProdukCombo.addItem(item);
            if (item.getIdProduk().equals(idSebelumnya)) {
                selectedIndex = i;
            }
        }

        if (stokProdukCombo.getItemCount() > 0) {
            stokProdukCombo.setSelectedIndex(selectedIndex >= 0 ? selectedIndex : 0);
        }
    }

    private void updateScanHistoryPreview() {
        if (scanHistoryArea == null || mejaCombo == null) {
            return;
        }

        String noMeja = String.valueOf(mejaCombo.getSelectedItem());
        List<String> riwayat = database.ambilRiwayatMeja(noMeja, 5);
        if (riwayat.isEmpty()) {
            scanHistoryArea.setText("Belum ada history untuk meja " + noMeja + ".");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("History meja ").append(noMeja).append(":\n");
        for (String item : riwayat) {
            sb.append("- ").append(item).append("\n");
        }
        scanHistoryArea.setText(sb.toString());
    }

    private String ringkasanRiwayatMeja() {
        if (mejaAktif == null) {
            return "Belum ada meja aktif.";
        }

        List<String> riwayat = database.ambilRiwayatMeja(mejaAktif.getNoMeja(), 5);
        if (riwayat.isEmpty()) {
            return "Belum ada history.";
        }

        StringBuilder sb = new StringBuilder();
        for (String item : riwayat) {
            sb.append("- ").append(item).append("\n");
        }
        return sb.toString();
    }

    private String ringkasanPesanan() {
        if (pesananAktif == null) {
            return "Belum ada pesanan.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ID Pesanan : ").append(pesananAktif.getIdPesanan()).append("\n");
        sb.append("No Meja    : ").append(mejaAktif == null ? "-" : mejaAktif.getNoMeja()).append("\n");
        sb.append("Status     : ").append(pesananAktif.getStatusPesanan()).append("\n\n");
        sb.append("Item:\n");
        for (ItemPesanan item : pesananAktif.getDaftarItem()) {
            sb.append("- ").append(item.getInfo()).append("\n");
        }
        sb.append("\nTotal Kupon: ").append(pesananAktif.getTotalKuponDigital()).append(" kupon\n");
        sb.append("Total Bayar: ").append(formatRupiah((int) pesananAktif.konversiKeRupiah())).append("\n");
        return sb.toString();
    }

    private String[] ambilPilihanMeja() {
        List<String> meja = database.ambilSemuaNomorMeja();
        if (meja.isEmpty()) {
            return new String[]{"WG-M-01", "WG-M-02", "WG-M-03", "WG-M-04", "WG-M-05"};
        }
        return meja.toArray(new String[0]);
    }

    private boolean isStatusSiap(String status) {
        return "Siap Diambil".equalsIgnoreCase(status);
    }

    private JPanel screen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        return panel;
    }

    private RoundedPanel card(int radius) {
        RoundedPanel panel = new RoundedPanel(radius, CARD);
        panel.setOpaque(false);
        return panel;
    }

    private JPanel transparentBox(int axis) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, axis));
        panel.setOpaque(false);
        return panel;
    }

    private JLabel title(String text, int size) {
        return label(text, size > 20 ? size - 4 : size, DARK);
    }

    private JLabel label(String text, int size, Color color) {
        JLabel label = new JLabel(text);
        int finalSize = size > 14 ? size - 2 : size;
        label.setFont(new Font("SansSerif", finalSize >= 18 ? Font.BOLD : Font.PLAIN, finalSize));
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField inputField() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE), new EmptyBorder(6, 8, 6, 8)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        return field;
    }

    private JTextArea readArea(int rows, int columns) {
        JTextArea area = new JTextArea(rows, columns);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(new Color(254, 251, 247));
        area.setBorder(new EmptyBorder(8, 8, 8, 8));
        area.setForeground(DARK);
        return area;
    }

    private JScrollPane cleanScroll(Component component) {
        JScrollPane scroll = new JScrollPane(component);
        scroll.setBorder(BorderFactory.createLineBorder(LINE));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private JButton primaryButton(String text, Runnable action) {
        return new PillButton(text, ACCENT, Color.WHITE, action);
    }

    private JButton ghostButton(String text, Runnable action) {
        return new PillButton(text, new Color(245, 235, 225), DARK, action);
    }

    private void tampil(String card) {
        if (CUSTOMER_SCAN.equals(card)) {
            updateScanHistoryPreview();
        } else if (CUSTOMER_MENU.equals(card)) {
            updateMenuGrid();
            updateCart();
        } else if (CUSTOMER_CHECKOUT.equals(card)) {
            updateCheckout();
        } else if (CUSTOMER_RECEIPT.equals(card)) {
            updateReceipt();
        } else if (TENANT_DASHBOARD.equals(card)) {
            updateTenantDashboard();
        } else if (TENANT_MENU.equals(card)) {
            updateTenantMenuText();
        }
        cardLayout.show(mainPanel, card);
    }

    private void info(String message) {
        JOptionPane.showMessageDialog(this, message, "WG58 Food Court", JOptionPane.INFORMATION_MESSAGE);
    }

    private String formatRupiah(int nominal) {
        String hasil = rupiah.format(nominal);
        return hasil.replace(",00", "");
    }

    private String randomPendek() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private void keluarAplikasi() {
        database.putuskan();
        dispose();
    }

    private static class BorderLayoutPanel extends JPanel {
        BorderLayoutPanel() {
            super(new BorderLayout());
            setOpaque(false);
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill;

        RoundedPanel(int radius, Color fill) {
            this.radius = radius;
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(LINE);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class PillButton extends JButton {
        private final Color bg;

        PillButton(String text, Color bg, Color fg, Runnable action) {
            super(text);
            this.bg = bg;
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setForeground(fg);
            setBorder(new EmptyBorder(6, 12, 6, 12));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addActionListener(e -> action.run());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color color = getModel().isPressed() ? bg.darker() : bg;
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
