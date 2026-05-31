package com.example.myapplication.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDb(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    val judul: String = "",
    val konten: String = "",
    @SerialName("sudah_dibaca") val sudahDibaca: Boolean = false,
    @SerialName("created_at") val createdAt: String = ""
)