package com.example.myapplication.models;

public class NotifItem {
    private String judul;
    private String konten;
    private String waktu;
    private boolean sudahDibaca;

    public NotifItem(String judul, String konten, String waktu, boolean sudahDibaca) {
        this.judul = judul;
        this.konten = konten;
        this.waktu = waktu;
        this.sudahDibaca = sudahDibaca;
    }

    public String getJudul() { return judul; }
    public String getKonten() { return konten; }
    public String getWaktu() { return waktu; }
    public boolean isSudahDibaca() { return sudahDibaca; }
    public void setSudahDibaca(boolean sudahDibaca) { this.sudahDibaca = sudahDibaca; }
}
