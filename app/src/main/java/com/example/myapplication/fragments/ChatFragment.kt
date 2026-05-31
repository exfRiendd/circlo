package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.ChatRoomAdapter
import com.example.myapplication.chat.ChatRoomActivity
import com.example.myapplication.models.ChatRoom
import com.example.myapplication.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_chat, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv: RecyclerView = view.findViewById(R.id.rv_chats)
        rv.layoutManager = LinearLayoutManager(context)

        val userId = SupabaseClientProvider.client
            .auth.currentSessionOrNull()?.user?.id ?: return

        lifecycleScope.launch {
            try {
                val rooms = SupabaseClientProvider.client
                    .postgrest["chat_rooms"]
                    .select()
                    .decodeList<ChatRoom>()
                    .filter { it.donorId == userId || it.requesterId == userId }
                    .sortedByDescending { it.lastMessageAt }

                rv.adapter = ChatRoomAdapter(rooms) { room ->
                    val intent = Intent(requireContext(), ChatRoomActivity::class.java)
                    intent.putExtra("room_id", room.id)
                    intent.putExtra("other_username", room.donorId)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}