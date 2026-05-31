package com.example.myapplication.chat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.MessageAdapter
import com.example.myapplication.models.Message
import com.example.myapplication.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var adapter: MessageAdapter
    private val messages = mutableListOf<Message>()
    private lateinit var roomId: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        roomId = intent.getStringExtra("room_id") ?: run { finish(); return }
        val otherUsername = intent.getStringExtra("other_username") ?: "Chat"

        userId = SupabaseClientProvider.client
            .auth.currentSessionOrNull()?.user?.id ?: run { finish(); return }

        val tvTitle: TextView = findViewById(R.id.tv_chat_title)
        val rvMessages: RecyclerView = findViewById(R.id.rv_messages)
        val etMessage: EditText = findViewById(R.id.et_message)
        val btnSend: ImageButton = findViewById(R.id.btn_send)
        val ivBack: ImageView = findViewById(R.id.iv_back)

        tvTitle.text = otherUsername
        ivBack.setOnClickListener { finish() }

        adapter = MessageAdapter(messages, userId)
        rvMessages.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        rvMessages.adapter = adapter

        loadMessages(rvMessages)
        subscribeRealtime(rvMessages)

        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener
            sendMessage(text, etMessage)
        }
    }

    private fun loadMessages(rv: RecyclerView) {
        lifecycleScope.launch {
            try {
                val loaded = SupabaseClientProvider.client
                    .postgrest["messages"]
                    .select()
                    .decodeList<Message>()
                    .filter { it.roomId == roomId }
                    .sortedBy { it.createdAt }

                messages.clear()
                messages.addAll(loaded)
                adapter.notifyDataSetChanged()
                if (messages.isNotEmpty()) rv.scrollToPosition(messages.size - 1)
            } catch (e: Exception) {
                Toast.makeText(this@ChatRoomActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun subscribeRealtime(rv: RecyclerView) {
        val channel = SupabaseClientProvider.client.realtime.channel("room-$roomId")

        channel.postgresChangeFlow<PostgresAction.Insert>("public") {
            table = "messages"
        }.onEach { change ->
            try {
                val msg = Json { ignoreUnknownKeys = true }
                    .decodeFromJsonElement<Message>(change.record)
                if (msg.roomId == roomId) {
                    messages.add(msg)
                    adapter.notifyItemInserted(messages.size - 1)
                    rv.scrollToPosition(messages.size - 1)
                }
            } catch (_: Exception) {}
        }.launchIn(lifecycleScope)

        lifecycleScope.launch {
            try { channel.subscribe() } catch (_: Exception) {}
        }
    }

    private fun sendMessage(text: String, et: EditText) {
        lifecycleScope.launch {
            try {
                val msg = Message(
                    roomId = roomId,
                    senderId = userId,
                    content = text
                )
                SupabaseClientProvider.client.postgrest["messages"].insert(msg)
                et.setText("")
            } catch (e: Exception) {
                Toast.makeText(this@ChatRoomActivity, "Gagal kirim: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            try {
                SupabaseClientProvider.client.realtime.removeAllChannels()
            } catch (_: Exception) {}
        }
    }
}