package com.app.omiyago.kurir.model;

/**
 * Created by Andre Tampubolon (andre.tampubolon@idstar.co.id) on 3/10/2019.
 */
public class ScannedItem {

    /*
    Contoh format:
    ID:1303-Nama:Ikka Prabowo-No Ref:YRLRIPFGB-Jumlah:2-Total:333750.00-Alamat:Belum ada alamat
     */

    private int id, jumlah;
    private double total;
    String nama, noRef, alamat;
    String status_pickup, ttd;
    String nama_kurir, ttd_kurir;

    public ScannedItem(int id, String nama, String noRef, int jumlah, double total, String alamat, String ttd,
                       String nama_kurir, String ttd_kurir, String status_pickup){
        this.id = id;
        this.nama = nama;
        this.noRef = noRef;
        this.jumlah = jumlah;
        this.total = total;
        this.alamat = alamat;
        this.ttd = ttd;
        this.nama_kurir = nama_kurir;
        this.ttd_kurir = ttd_kurir;
        this.status_pickup = status_pickup;
    }

    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getNoRef() { return noRef; }
    public int getJumlah() { return jumlah; }
    public double getTotal() { return total; }
    public String getAlamat() { return alamat; }
    public String getTtd() { return ttd; }
    public String getNamaKurir() { return nama_kurir;}
    public String getTtdKurir() { return ttd_kurir; }
    public String getStatusPickup() { return status_pickup; }
}
