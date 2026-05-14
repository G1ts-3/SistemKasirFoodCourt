package tubes.kelompok4.model;

/**
 * [ABSTRACTION]: Menggunakan abstract class karena 'Menu' secara umum 
 * tidak boleh diinstansiasi secara langsung, melainkan harus spesifik (Makanan/Minuman).
 */
public abstract class Menu {
    
    // [ENCAPSULATION]: Menyembunyikan atribut menggunakan access modifier private.
    // Hanya bisa diakses/diubah melalui getter dan setter.
    private int id;
    private String nama;
    private double harga;

    // [OVERLOADING] Constructor 1: Constructor kosong
    public Menu() {}

    // [OVERLOADING] Constructor 2: Constructor dengan parameter
    public Menu(int id, String nama, double harga) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
    }

    // Getter dan Setter untuk Encapsulation
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public double getHarga() { return harga; }
    public void setHarga(double harga) { this.harga = harga; }

    // [ABSTRACTION]: Abstract method yang wajib di-override oleh class turunannya.
    public abstract void tampilkanInfo();
}