package com.example.myapplication.chat

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.MessageAdapter
import com.example.myapplication.models.BarangItem
import com.example.myapplication.models.ChatRoom
import com.example.myapplication.models.Message
import com.example.myapplication.models.Profile
import com.example.myapplication.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var adapter: MessageAdapter
    private val messages = mutableListOf<Message>()
    private lateinit var roomId: String
    private lateinit var userId: String
    private var barang: BarangItem? = null
    private var room: ChatRoom? = null

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

        loadRoomAndBarang(rvMessages)
        subscribeRealtime(rvMessages)

        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener
            sendMessage(text, etMessage)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LOAD DATA
    // ─────────────────────────────────────────────────────────────────────────

    private fun loadRoomAndBarang(rv: RecyclerView) {
        lifecycleScope.launch {
            try {
                // Load chat room
                room = SupabaseClientProvider.client
                    .postgrest["chat_rooms"]
                    .select { filter { eq("id", roomId) } }
                    .decodeSingle<ChatRoom>()

                // Load barang
                barang = SupabaseClientProvider.client
                    .postgrest["barang"]
                    .select { filter { eq("id", room!!.barangId) } }
                    .decodeSingle<BarangItem>()

                loadMessages(rv)
                updateActionBar()

            } catch (e: Exception) {
                Toast.makeText(this@ChatRoomActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadMessages(rv: RecyclerView) {
        lifecycleScope.launch {
            try {
                val loaded = SupabaseClientProvider.client
                    .postgrest["messages"]
                    .select { filter { eq("room_id", roomId) } }
                    .decodeList<Message>()
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

    // ─────────────────────────────────────────────────────────────────────────
    // ACTION BAR — tombol berubah sesuai status & role
    // ─────────────────────────────────────────────────────────────────────────

    private fun updateActionBar() {
        val b = barang ?: run {
            android.util.Log.d("DEBUG", "barang null"); return
        }
        val r = room ?: run {
            android.util.Log.d("DEBUG", "room null"); return
        }

        android.util.Log.d("DEBUG", "userId: $userId")
        android.util.Log.d("DEBUG", "donorId: ${r.donorId}")
        android.util.Log.d("DEBUG", "requesterId: ${r.requesterId}")
        android.util.Log.d("DEBUG", "status: ${b.status}")
        android.util.Log.d("DEBUG", "barang.requesterId: ${b.requesterId}")

        val isDonor = userId == r.donorId
        val isRequester = userId == r.requesterId

        val llActionBar: LinearLayout = findViewById(R.id.ll_action_bar)
        val btnMinta: Button = findViewById(R.id.btn_minta_barang)
        val btnPilih: Button = findViewById(R.id.btn_pilih_penerima)
        val btnKonfirmasi: Button = findViewById(R.id.btn_konfirmasi_terima)
        val tvStatusDonasi: TextView = findViewById(R.id.tv_status_donasi)

        // Reset semua
        llActionBar.visibility = View.VISIBLE
        btnMinta.visibility = View.GONE
        btnPilih.visibility = View.GONE
        btnKonfirmasi.visibility = View.GONE
        tvStatusDonasi.visibility = View.GONE

        when (b.status) {
            "aktif" -> {
                if (isRequester) {
                    // Requester bisa minta barang ini
                    btnMinta.visibility = View.VISIBLE
                    btnMinta.setOnClickListener { mintaBarang() }
                }
                // Donor hanya lihat chat, belum ada aksi
            }
            "pending_pickup" -> {
                if (isDonor && b.requesterId == null) {
                    // Donor belum pilih penerima → tampilkan tombol pilih
                    btnPilih.visibility = View.VISIBLE
                    btnPilih.setOnClickListener { pilihPenerima() }
                } else if (isDonor && b.requesterId != null) {
                    // Donor sudah pilih, menunggu konfirmasi requester
                    tvStatusDonasi.visibility = View.VISIBLE
                    tvStatusDonasi.text = "⏳ Menunggu konfirmasi penerima..."
                } else if (isRequester && b.requesterId == userId) {
                    // Requester terpilih → tampilkan tombol konfirmasi
                    btnKonfirmasi.visibility = View.VISIBLE
                    btnKonfirmasi.setOnClickListener { konfirmasiTerima() }
                } else if (isRequester && b.requesterId != null && b.requesterId != userId) {
                    // Requester ini tidak terpilih
                    tvStatusDonasi.visibility = View.VISIBLE
                    tvStatusDonasi.text = "❌ Barang ini sudah diberikan ke orang lain"
                }
            }
            "diambil" -> {
                llActionBar.visibility = View.VISIBLE
                tvStatusDonasi.visibility = View.VISIBLE
                tvStatusDonasi.text = "✅ Barang ini sudah berhasil didonasikan"
            }
            else -> llActionBar.visibility = View.GONE
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AKSI 1 — Requester: Minta Barang
    // ─────────────────────────────────────────────────────────────────────────

    private fun mintaBarang() {
        val b = barang ?: return
        val r = room ?: return

        // Disable tombol langsung biar tidak dobel
        val btnMinta: Button = findViewById(R.id.btn_minta_barang)
        btnMinta.isEnabled = false

        // Cek dulu apakah sudah pernah minta (status sudah pending)
        if (b.status != "aktif") {
            Toast.makeText(this, "Permintaan sudah terkirim", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                SupabaseClientProvider.client.postgrest["barang"]
                    .update({ set("status", "pending_pickup") }) {
                        filter {
                            eq("id", b.id)
                            eq("status", "aktif") // hanya update jika masih aktif
                        }
                    }

                sendSystemMessage("📦 ${getUsernameById(userId)} meminta barang ini")

                insertNotifikasi(
                    targetUserId = r.donorId,
                    judul = "Ada yang minta barangmu!",
                    konten = "Seseorang meminta '${b.nama}'. Buka chat untuk memilih penerima.",
                    type = "request"
                )

                barang = b.copy(status = "pending_pickup")
                updateActionBar()
                Toast.makeText(this@ChatRoomActivity, "Permintaan terkirim!", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                btnMinta.isEnabled = true // re-enable kalau error
                Toast.makeText(this@ChatRoomActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AKSI 2 — Donor: Pilih Penerima (approve requester di room ini)
    // ─────────────────────────────────────────────────────────────────────────

    private fun pilihPenerima() {
        val b = barang ?: return
        val r = room ?: return

        lifecycleScope.launch {
            try {
                // Set requester_id di barang = requester di room ini
                SupabaseClientProvider.client.postgrest["barang"]
                    .update({
                        set("requester_id", r.requesterId)
                    }) {
                        filter { eq("id", b.id) }
                    }

                // Kirim pesan sistem ke chat
                sendSystemMessage("✅ Donor memilih kamu sebagai penerima! Silakan konfirmasi setelah barang diambil.")

                // Kirim notifikasi ke requester
                insertNotifikasi(
                    targetUserId = r.requesterId,
                    judul = "Kamu dipilih sebagai penerima!",
                    konten = "Donor menyetujui permintaanmu untuk '${b.nama}'. Konfirmasi setelah barang diambil.",
                    type = "approved"
                )

                barang = b.copy(requesterId = r.requesterId)
                updateActionBar()
                Toast.makeText(this@ChatRoomActivity, "Penerima dipilih!", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Toast.makeText(this@ChatRoomActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AKSI 3 — Requester: Konfirmasi Sudah Terima Barang
    // ─────────────────────────────────────────────────────────────────────────

    private fun konfirmasiTerima() {
        val b = barang ?: return
        val r = room ?: return

        lifecycleScope.launch {
            try {
                // 1. Update status barang jadi "diambil"
                SupabaseClientProvider.client.postgrest["barang"]
                    .update({ set("status", "diambil") }) {
                        filter { eq("id", b.id) }
                    }

                // 2. Tambah total_donated donor +1
                val donorProfile = SupabaseClientProvider.client
                    .postgrest["profiles"]
                    .select { filter { eq("id", r.donorId) } }
                    .decodeSingle<Profile>()

                SupabaseClientProvider.client.postgrest["profiles"]
                    .update({ set("total_donated", donorProfile.totalDonated + 1) }) {
                        filter { eq("id", r.donorId) }
                    }

                // 3. Tambah total_received requester +1
                val requesterProfile = SupabaseClientProvider.client
                    .postgrest["profiles"]
                    .select { filter { eq("id", userId) } }
                    .decodeSingle<Profile>()

                SupabaseClientProvider.client.postgrest["profiles"]
                    .update({ set("total_received", requesterProfile.totalReceived + 1) }) {
                        filter { eq("id", userId) }
                    }

                // 4. Kirim pesan sistem
                sendSystemMessage("🎉 Barang berhasil diterima! Terima kasih sudah berkontribusi.")

                // 5. Notifikasi ke donor
                insertNotifikasi(
                    targetUserId = r.donorId,
                    judul = "Donasi berhasil! 🌱",
                    konten = "'${b.nama}' sudah diterima. Kontribusimu bertambah!",
                    type = "completed"
                )

                barang = b.copy(status = "diambil")
                updateActionBar()

                Toast.makeText(
                    this@ChatRoomActivity,
                    "Terima kasih! Barang berhasil didonasikan 🎉",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {
                Toast.makeText(this@ChatRoomActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPER
    // ─────────────────────────────────────────────────────────────────────────

    private suspend fun sendSystemMessage(text: String) {
        val msg = Message(
            roomId = roomId,
            senderId = "system",
            content = text
        )
        SupabaseClientProvider.client.postgrest["messages"].insert(msg)
    }

    private suspend fun insertNotifikasi(
        targetUserId: String,
        judul: String,
        konten: String,
        type: String
    ) {
        val data = buildJsonObject {
            put("user_id", targetUserId)
            put("judul", judul)
            put("konten", konten)
            put("type", type)
        }
        SupabaseClientProvider.client.postgrest["notifications"].insert(data)
    }

    private suspend fun getUsernameById(uid: String): String {
        return try {
            SupabaseClientProvider.client
                .postgrest["profiles"]
                .select { filter { eq("id", uid) } }
                .decodeSingle<com.example.myapplication.models.Profile>()
                .username
        } catch (_: Exception) { "Seseorang" }
    }

    private fun sendMessage(text: String, et: EditText) {
        lifecycleScope.launch {
            try {
                val msg = Message(roomId = roomId, senderId = userId, content = text)
                SupabaseClientProvider.client.postgrest["messages"].insert(msg)
                et.setText("")
            } catch (e: Exception) {
                Toast.makeText(this@ChatRoomActivity, "Gagal kirim: ${e.message}", Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            try { SupabaseClientProvider.client.realtime.removeAllChannels() }
            catch (_: Exception) {}
        }
    }
}