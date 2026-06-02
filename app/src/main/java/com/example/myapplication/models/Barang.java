package com.example.myapplication.models;

public class Barang {
    private String id;
    private String userId;
    private String nama;
    private String kategori;
    private String lokasi;
    private String deskripsi;
    private String fotoUrl;
    private String status;
    private String createdAt;

    public Barang(String nama, String kategori, String lokasi, String waktu) {
        this.nama = nama;
        this.kategori = kategori;
        this.lokasi = lokasi;
        this.createdAt = waktu;
    }

    // Getters & Setters
    public String getId()             { return id; }
    public void setId(String id)      { this.id = id; }
    public String getUserId()         { return userId; }
    public void setUserId(String uid) { this.userId = uid; }
    public String getNama()           { return nama; }
    public String getKategori()       { return kategori; }
    public String getLokasi()         { return lokasi; }
    public String getDeskripsi()      { return deskripsi; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public String getFotoUrl()        { return fotoUrl; }
    public String getStatus()         { return status; }
    public String getWaktu()          { return createdAt; }
    public String getUsername()       { return null; }
}