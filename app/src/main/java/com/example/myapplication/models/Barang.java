package com.example.myapplication.models;

public class Barang {
    private String id;
    private String nama;
    private String kategori;
    private String lokasi;
    private String deskripsi;
    private String fotoUrl;
    private String status;
    private String createdAt;
    private String username;

    // Constructor untuk dummy data
    public Barang(String nama, String kategori, String lokasi, String waktu) {
        this.nama = nama;
        this.kategori = kategori;
        this.lokasi = lokasi;
        this.createdAt = waktu;
    }

    // Getters
    public String getId()          { return id; }
    public String getNama()        { return nama; }
    public String getKategori()    { return kategori; }
    public String getLokasi()      { return lokasi; }
    public String getDeskripsi()   { return deskripsi; }
    public String getFotoUrl()     { return fotoUrl; }
    public String getStatus()      { return status; }
    public String getWaktu()       { return createdAt; }
    public String getUsername()    { return username; }
}