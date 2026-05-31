package com.example.myapplication.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRoom(
    val id: String = "",
    @SerialName("barang_id") val barangId: String = "",
    @SerialName("donor_id") val donorId: String = "",
    @SerialName("requester_id") val requesterId: String = "",
    @SerialName("last_message") val lastMessage: String = "",
    @SerialName("last_message_at") val lastMessageAt: String = ""
)