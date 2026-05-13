package WG58.pengguna;

import WG58.pesanan.Pesanan;

public class Pelanggan extends Pengguna{

    private String noMeja;
    private int uangPembayaran;

    public Pelanggan( String idPengguna, String peran,String noMeja,int uangPembayaran) {
        super(idPengguna, "Pelanggan");
        this.uangPembayaran = uangPembayaran;
    }

    @Override
    public boolean aksesSistem() {
        System.out.println("Pelanggan dengan ID " + idPengguna + " berhasil mengakses sistem.");
        return false; // dia sebagai pelanggan
    }
    
    
    public void pindaiQRMeja(Meja mejaYangDiScan) {
        System.out.println("Mulai memindai QR Meja...");
        
        mejaYangDiScan.ambilNoMeja();
        this.noMeja = mejaYangDiScan.getNoMeja();
        
        System.out.println("Pelanggan " + this.idPengguna + " sukses terhubung dengan meja " + this.noMeja);
    }
    
    public void lihatMenu() {
        System.out.println("Menampilkan daftar menu untuk meja " + noMeja + "...");
    }

    public void konfirmasiPesanan() {
        
    }

    public void berikanRating(Pesanan p, int r, String u) {
        System.out.println("Mengirim ulasan ke database...");
        System.out.println("Rating: ⭐ " + r + "/5");
        System.out.println("Komentar: " + u);
        // Logika query INSERT ke tabel Rating bisa ditaruh di sini
    }

    

}
