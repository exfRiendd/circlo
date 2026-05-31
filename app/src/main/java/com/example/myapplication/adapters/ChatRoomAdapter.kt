package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.ChatRoom

class ChatRoomAdapter(
    private val rooms: List<ChatRoom>,
    private val onClick: (ChatRoom) -> Unit
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
        val room = rooms[position]
        holder.tvNama.text = room.donorId.take(8)   // sementara tampil ID
        holder.tvItemName.text = room.barangId.take(8)
        holder.tvLastMessage.text = room.lastMessage
        holder.tvWaktu.text = if (room.lastMessageAt.length >= 10)
            room.lastMessageAt.take(10) else room.lastMessageAt
        holder.itemView.setOnClickListener { onClick(room) }
    }

    override fun getItemCount() = rooms.size
}