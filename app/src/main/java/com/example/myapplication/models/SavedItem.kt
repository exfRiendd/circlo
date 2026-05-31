package com.example.myapplication.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SavedItem(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("barang_id") val barangId: String = "",
    @SerialName("created_at") val createdAt: String = ""
)