package WG58.pengguna;

import WG58.pesanan.Pesanan;

public class Tenant extends Pengguna{
    private String username, pw;

    public Tenant(String idPengguna, String peran,String username, String pw) {
        super(idPengguna, peran);
        this.username = username;
        this.pw = pw;
    }
    
    @Override
    public boolean aksesSistem() {
        return true; // dia sebagai pelanggan
    }

    public void terimaPesanan(){
        
    }
    
    public void kelolaMenu(){
        
    }
    
    public void lihatRiwayatPesanan(){
        
    }
}
