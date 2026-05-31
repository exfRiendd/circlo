package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.ChatRoomDisplay

class ChatRoomAdapter(
    private val rooms: List<ChatRoomDisplay>,
    private val onClick: (ChatRoomDisplay) -> Unit
) : RecyclerView.Adapter<ChatRoomAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tv_nama)
        val tvItemName: TextView = view.findViewById(R.id.tv_item_name)
        val tvLastMessage: TextView = view.findViewById(R.id.tv_last_message)
        val tvWaktu: TextView = view.findViewById(R.id.tv_waktu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_row, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = rooms[position]
        holder.tvNama.text = item.otherUsername
        holder.tvItemName.text = item.namaBarang
        holder.tvLastMessage.text = item.room.lastMessage
        holder.tvWaktu.text = if (item.room.lastMessageAt.length >= 16)
            item.room.lastMessageAt.substring(11, 16) else item.room.lastMessageAt
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = rooms.size
}