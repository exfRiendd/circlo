package com.example.myapplication.models;

public class Barang {
    private String nama;
    private String kategori;
    private String lokasi;
    private String waktu;

    public Barang(String nama, String kategori, String lokasi, String waktu) {
        this.nama = nama;
        this.kategori = kategori;
        this.lokasi = lokasi;
        this.waktu = waktu;
    }

    public String getNama() { return nama; }
    public String getKategori() { return kategori; }
    public String getLokasi() { return lokasi; }
    public String getWaktu() { return waktu; }
}
