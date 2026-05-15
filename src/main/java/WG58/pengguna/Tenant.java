package WG58.pengguna;

import WG58.pesanan.Pesanan;

public class Tenant extends Pengguna {
    private String username;
    private String password;

    public Tenant(String idPengguna, String username, String password) {
        super(idPengguna, "Tenant");
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean aksesSistem() {
        boolean berhasil = username != null && !username.isBlank()
                && password != null && !password.isBlank();

        if (berhasil) {
            System.out.println("Tenant " + username + " berhasil login.");
        } else {
            System.out.println("Login tenant gagal.");
        }

        return berhasil;
    }

    public void terimaNotifikasi() {
        System.out.println("Tenant menerima notifikasi pesanan baru.");
    }

    public void terimaNotifikasi(Pesanan pesanan) {
        System.out.println("Tenant menerima pesanan: " + pesanan.getIdPesanan());
        pesanan.cetakStrukDigital();
    }

    public void kelolaMenu() {
        System.out.println("Tenant membuka fitur kelola menu.");
    }

    public void lihatRiwayatPesanan() {
        System.out.println("Tenant melihat riwayat pesanan.");
    }

    public void ubahStatusPesanan(Pesanan pesanan, String statusBaru) {
        pesanan.ubahStatus(statusBaru);
        System.out.println("Status pesanan diubah menjadi: " + statusBaru);
    }

    public String getUsername() {
        return username;
    }
}
