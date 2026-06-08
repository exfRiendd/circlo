package com.example.myapplication.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    val label: String = "Rumah",
    val alamat: String = "",
    val kota: String = "",
    val catatan: String = "",
    @SerialName("is_utama") val isUtama: Boolean = false,
    @SerialName("created_at") val createdAt: String = ""
)