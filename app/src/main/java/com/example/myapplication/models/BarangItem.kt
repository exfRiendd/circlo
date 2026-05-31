package com.example.myapplication.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BarangItem(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    val nama: String = "",
    val deskripsi: String = "",
    val kategori: String = "",
    val lokasi: String = "",
    @SerialName("foto_url") val fotoUrl: String = "",
    val status: String = "aktif",
    @SerialName("created_at") val createdAt: String = ""
)