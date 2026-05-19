package com.example.circlo.models;

public class ChatItem {
    private String nama;
    private String namaBarang;
    private String pesanTerakhir;
    private String waktu;
    private int unreadCount;

    public ChatItem(String nama, String namaBarang, String pesanTerakhir, String waktu, int unreadCount) {
        this.nama = nama;
        this.namaBarang = namaBarang;
        this.pesanTerakhir = pesanTerakhir;
        this.waktu = waktu;
        this.unreadCount = unreadCount;
    }

    public String getNama() { return nama; }
    public String getNamaBarang() { return namaBarang; }
    public String getPesanTerakhir() { return pesanTerakhir; }
    public String getWaktu() { return waktu; }
    public int getUnreadCount() { return unreadCount; }
}
