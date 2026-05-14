package tubes.kelompok4;

import tubes.kelompok4.config.DatabaseConfig;
import tubes.kelompok4.model.Makanan;
import tubes.kelompok4.model.Menu;
import tubes.kelompok4.model.Minuman;
import tubes.kelompok4.repo.RestoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // [1] Buat database secara otomatis jika belum ada di MySQL
        DatabaseConfig.buatDatabaseJikaBelumAda();
        
        Scanner scanner = new Scanner(System.in);
        
        // [2] Instansiasi Repository. 
        // Constructor class ini akan otomatis mengeksekusi pembuatan Tabel & Data menu.
        RestoRepository repo = new RestoRepository();
        
        List<Menu> daftarMenu = repo.getAllMenu();
        List<Menu> keranjang = new ArrayList<>();
        boolean isRunning = true;

        System.out.println("=== Selamat Datang di Restoran CLI ===");

        // (Sisa kode ke bawah SAMA PERSIS seperti sebelumnya)
        while (isRunning) {
            System.out.println("\n--- DAFTAR MENU ---");
            if (daftarMenu.isEmpty()) {
                System.out.println("Menu kosong. Pastikan database sudah terisi.");
                break;
            }

            for (Menu menu : daftarMenu) {
                menu.tampilkanInfo();
                
                if (menu instanceof Makanan) {
                    Makanan m = (Makanan) menu;
                    if (m.isPedas()) {
                        System.out.println("   -> Peringatan: Menu ini lumayan pedas!");
                    }
                } else if (menu instanceof Minuman) {
                    Minuman m = (Minuman) menu;
                    if (m.isDingin()) {
                        System.out.println("   -> Info: Disajikan menggunakan es batu.");
                    }
                }
            }

            System.out.println("\nPilih ID Menu untuk dipesan (0 untuk Selesai & Bayar): ");
            int pilihan = scanner.nextInt();

            if (pilihan == 0) {
                isRunning = false;
            } else {
                Menu menuDipilih = null;
                for (Menu m : daftarMenu) {
                    if (m.getId() == pilihan) {
                        menuDipilih = m;
                        break;
                    }
                }

                if (menuDipilih != null) {
                    keranjang.add(menuDipilih);
                    System.out.println(menuDipilih.getNama() + " berhasil ditambahkan ke keranjang!");
                } else {
                    System.out.println("ID Menu tidak valid.");
                }
            }
        }

        if (!keranjang.isEmpty()) {
            System.out.println("\n--- STRUK PESANAN ---");
            double total = 0;
            for (Menu pesanan : keranjang) {
                System.out.printf("- %s : Rp%,.2f\n", pesanan.getNama(), pesanan.getHarga());
                total += pesanan.getHarga();
            }
            
            System.out.printf("\nTotal Tagihan: Rp%,.2f\n", total);
            
            double uang = 0;
            while (uang < total) {
                System.out.print("Masukkan jumlah uang pembayaran: Rp");
                uang = scanner.nextDouble();
                if (uang < total) {
                    System.out.println("Uang tidak cukup. Silakan masukkan nominal yang benar.");
                }
            }

            double kembalian = uang - total;
            System.out.printf("Kembalian Anda: Rp%,.2f\n", kembalian);
            
            repo.simpanTransaksi(total, uang, kembalian);
            System.out.println("Transaksi berhasil disimpan ke database. Terima kasih!");
        } else {
            System.out.println("Anda tidak memesan apa pun. Sampai jumpa!");
        }
        
        scanner.close();
    }
}