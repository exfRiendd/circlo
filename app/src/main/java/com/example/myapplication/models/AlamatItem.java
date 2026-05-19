package com.example.circlo.models;

public class AlamatItem {
    private String label;
    private String alamat;
    private String kota;
    private String catatan;
    private boolean utama;

    public AlamatItem(String label, String alamat, String kota, String catatan, boolean utama) {
        this.label = label;
        this.alamat = alamat;
        this.kota = kota;
        this.catatan = catatan;
        this.utama = utama;
    }

    public String getLabel() { return label; }
    public String getAlamat() { return alamat; }
    public String getKota() { return kota; }
    public String getCatatan() { return catatan; }
    public boolean isUtama() { return utama; }
    public void setUtama(boolean utama) { this.utama = utama; }
}
