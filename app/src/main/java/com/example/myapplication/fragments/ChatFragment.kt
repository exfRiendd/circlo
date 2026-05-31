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
import com.example.myapplication.models.BarangItem
import com.example.myapplication.models.ChatRoom
import com.example.myapplication.models.ChatRoomDisplay
import com.example.myapplication.models.Profile
import com.example.myapplication.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
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
                android.util.Log.d("ChatFragment", "Start loading, userId: $userId")

                val rooms = SupabaseClientProvider.client
                    .postgrest["chat_rooms"]
                    .select()
                    .decodeList<ChatRoom>()
                    .filter { it.donorId == userId || it.requesterId == userId }
                    .sortedByDescending { it.lastMessageAt }

                android.util.Log.d("ChatFragment", "Rooms: ${rooms.size}")

                val profiles = SupabaseClientProvider.client
                    .postgrest["profiles"]
                    .select()
                    .decodeList<Profile>()
                    .associateBy { it.id }

                android.util.Log.d("ChatFragment", "Profiles: ${profiles.size}")

                val allBarang = SupabaseClientProvider.client
                    .postgrest["barang"]
                    .select()
                    .decodeList<BarangItem>()
                    .associateBy { it.id }

                android.util.Log.d("ChatFragment", "Barang: ${allBarang.size}")

                val roomsDisplay = rooms.map { room ->
                    val otherUserId = if (room.donorId == userId) room.requesterId else room.donorId
                    val otherUsername = profiles[otherUserId]?.username ?: "Pengguna"
                    val namaBarang = allBarang[room.barangId]?.nama ?: ""
                    ChatRoomDisplay(room, otherUsername, namaBarang)
                }

                android.util.Log.d("ChatFragment", "RoomsDisplay: ${roomsDisplay.size}")

                rv.adapter = ChatRoomAdapter(roomsDisplay) { item ->
                    val intent = Intent(requireContext(), ChatRoomActivity::class.java)
                    intent.putExtra("room_id", item.room.id)
                    intent.putExtra("other_username", item.otherUsername)
                    startActivity(intent)
                }

            } catch (e: Exception) {
                android.util.Log.e("ChatFragment", "Error: ${e.message}", e)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}