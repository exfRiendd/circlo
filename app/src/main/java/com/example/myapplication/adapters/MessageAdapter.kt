package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.Message

class MessageAdapter(
    private val messages: List<Message>,
    private val myUserId: String
) : RecyclerView.Adapter<MessageAdapter.VH>() {

    companion object {
        private const val VIEW_SENT = 1
        private const val VIEW_RECEIVED = 2
        private const val VIEW_SYSTEM = 3
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvContent: TextView = view.findViewById(R.id.tv_message_content)
        val tvTime: TextView = view.findViewById(R.id.tv_message_time)
    }

    override fun getItemViewType(position: Int) = when {
        messages[position].senderId == "system" -> VIEW_SYSTEM
        messages[position].senderId == myUserId -> VIEW_SENT
        else -> VIEW_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val layout = if (viewType == VIEW_SENT)
            R.layout.item_message_sent else R.layout.item_message_received
        return VH(LayoutInflater.from(parent.context).inflate(layout, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val msg = messages[position]
        holder.tvContent.text = msg.content
        holder.tvTime.text = msg.createdAt.substring(11, 16) // HH:mm
    }

    override fun getItemCount() = messages.size
}