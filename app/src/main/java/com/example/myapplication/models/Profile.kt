package com.example.myapplication.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val lokasi: String = "",
    @SerialName("foto_profil") val fotoProfil: String = "",
    val role: String = "user",
    @SerialName("total_donated") val totalDonated: Int = 0,
    @SerialName("total_received") val totalReceived: Int = 0,
    @SerialName("created_at") val createdAt: String = ""
)