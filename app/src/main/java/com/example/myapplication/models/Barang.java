package com.example.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class Barang {
    @SerializedName("id")
    private int id;

    @SerializedName("nama")
    private String nama;

    @SerializedName("kategori")
    private String kategori;

    @SerializedName("lokasi")
    private String lokasi;

    @SerializedName("deskripsi")
    private String deskripsi;

    @SerializedName("foto_url")
    private String fotoUrl;

    @SerializedName("status")
    private String status;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("username")
    private String username;

    // Constructor untuk data dummy (tetap kompatibel)
    public Barang(String nama, String kategori, String lokasi, String waktu) {
        this.nama = nama;
        this.kategori = kategori;
        this.lokasi = lokasi;
        this.createdAt = waktu;
    }

    // Getter
    public int getId()          { return id; }
    public String getNama()     { return nama; }
    public String getKategori() { return kategori; }
    public String getLokasi()   { return lokasi; }
    public String getDeskripsi(){ return deskripsi; }
    public String getFotoUrl()  { return fotoUrl; }
    public String getStatus()   { return status; }
    public String getWaktu()    { return createdAt; }
    public String getUsername() { return username; }
}
