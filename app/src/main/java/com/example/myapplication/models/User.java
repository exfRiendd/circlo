package com.example.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("lokasi")
    private String lokasi;

    @SerializedName("foto_profil")
    private String fotoProfil;

    public int getId()           { return id; }
    public String getUsername()  { return username; }
    public String getEmail()     { return email; }
    public String getLokasi()    { return lokasi; }
    public String getFotoProfil(){ return fotoProfil; }
}
