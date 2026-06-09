package com.example.myapplication.chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.models.BarangItem
import com.example.myapplication.models.ChatRoom
import com.example.myapplication.models.Profile
import com.example.myapplication.models.SavedItem
import com.example.myapplication.network.SupabaseClientProvider
import com.example.myapplication.utils.DateHelper
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class BarangDetailActivity : AppCompatActivity() {

    private lateinit var barangId: String
    private lateinit var userId: String
    private var isSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barang_detail)

        barangId = intent.getStringExtra("barang_id") ?: run { finish(); return }
        userId = SupabaseClientProvider.client
            .auth.currentSessionOrNull()?.user?.id ?: run { finish(); return }

        findViewById<ImageView>(R.id.iv_back).setOnClickListener { finish() }

        loadDetail()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LOAD DATA
    // ─────────────────────────────────────────────────────────────────────────

    private fun loadDetail() {
        lifecycleScope.launch {
            try {
                // 1. Load data barang
                val barang = SupabaseClientProvider.client
                    .postgrest["barang"]
                    .select { filter { eq("id", barangId) } }
                    .decodeSingle<BarangItem>()

                // 2. Load profil donor
                val donor = try {
                    SupabaseClientProvider.client
                        .postgrest["profiles"]
                        .select { filter { eq("id", barang.userId) } }
                        .decodeSingle<Profile>()
                } catch (_: Exception) { null }

                // 3. Cek apakah sudah disimpan
                val saved = SupabaseClientProvider.client
                    .postgrest["saved_items"]
                    .select {
                        filter {
                            eq("user_id", userId)
                            eq("barang_id", barangId)
                        }
                    }
                    .decodeList<SavedItem>()
                isSaved = saved.isNotEmpty()

                bindUI(barang, donor)

            } catch (e: Exception) {
                Toast.makeText(this@BarangDetailActivity,
                    "Gagal memuat: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BIND UI
    // ─────────────────────────────────────────────────────────────────────────

    private fun bindUI(barang: BarangItem, donor: Profile?) {
        // Foto
        val ivFoto: ImageView = findViewById(R.id.iv_foto_detail)
        if (barang.fotoUrl.isNotEmpty()) {
            Glide.with(this)
                .load(barang.fotoUrl)
                .placeholder(R.drawable.placeholder_item)
                .centerCrop()
                .into(ivFoto)
        }

        // Info dasar
        findViewById<TextView>(R.id.tv_nama_detail).text = barang.nama
        findViewById<TextView>(R.id.tv_kategori_detail).text = barang.kategori
        findViewById<TextView>(R.id.tv_lokasi_detail).text = barang.lokasi
        findViewById<TextView>(R.id.tv_waktu_detail).text = DateHelper.toRelative(barang.createdAt)
        findViewById<TextView>(R.id.tv_deskripsi_detail).text =
            barang.deskripsi.ifEmpty { "Tidak ada deskripsi." }

        // Info donor
        donor?.let {
            findViewById<TextView>(R.id.tv_donor_nama).text = it.username
            findViewById<TextView>(R.id.tv_donor_donasi).text =
                "${it.totalDonated} donasi berhasil"
        }

        // Tombol Simpan Bawah & Icon Simpan Atas
        val btnSimpan: Button = findViewById(R.id.btn_simpan)
        val ivSimpanTop: ImageView = findViewById(R.id.iv_simpan)

        val simpanClickListener = View.OnClickListener {
            if (isSaved) unsaveBarang() else saveBarang()
        }

        btnSimpan.setOnClickListener(simpanClickListener)
        ivSimpanTop.setOnClickListener(simpanClickListener) // ← Icon atas juga bisa diklik

        // Tombol Chat — sembunyikan jika barang milik sendiri
        val btnChat: Button = findViewById(R.id.btn_chat_donor)
        if (userId == barang.userId) {
            btnChat.visibility = View.GONE
            btnSimpan.visibility = View.GONE
            ivSimpanTop.visibility = View.GONE // ← Sembunyikan icon atas juga
        } else {
            btnChat.setOnClickListener { buatChatRoom(barang) }
        }

        // Update status UI tombol setelah semua diinisialisasi
        updateSimpanButton()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SIMPAN / UNSAVE
    // ─────────────────────────────────────────────────────────────────────────

    private fun saveBarang() {
        lifecycleScope.launch {
            try {
                val data = buildJsonObject {
                    put("user_id", userId)
                    put("barang_id", barangId)
                }
                SupabaseClientProvider.client
                    .postgrest["saved_items"]
                    .insert(data)

                isSaved = true
                updateSimpanButton()
                Toast.makeText(this@BarangDetailActivity,
                    "Barang disimpan ✓", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Toast.makeText(this@BarangDetailActivity,
                    "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun unsaveBarang() {
        lifecycleScope.launch {
            try {
                SupabaseClientProvider.client
                    .postgrest["saved_items"]
                    .delete {
                        filter {
                            eq("user_id", userId)
                            eq("barang_id", barangId)
                        }
                    }

                isSaved = false
                updateSimpanButton()
                Toast.makeText(this@BarangDetailActivity,
                    "Barang dihapus dari simpanan", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Toast.makeText(this@BarangDetailActivity,
                    "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSimpanButton() {
        val btnSimpan: Button = findViewById(R.id.btn_simpan)
        val ivSimpanTop: ImageView = findViewById(R.id.iv_simpan)

        if (isSaved) {
            // Tombol Bawah
            btnSimpan.text = "♥  Tersimpan"
            btnSimpan.setTextColor(getColor(R.color.circlo_white))
            btnSimpan.setBackgroundResource(R.drawable.bg_button_green)

            // Icon Atas (Berubah merah)
            ivSimpanTop.setColorFilter(getColor(R.color.circlo_red))
        } else {
            // Tombol Bawah
            btnSimpan.text = "♡  Simpan"
            btnSimpan.setTextColor(getColor(R.color.circlo_green))
            btnSimpan.setBackgroundResource(R.drawable.bg_chip_unselected)

            // Icon Atas (Berubah kembali jadi hitam)
            ivSimpanTop.setColorFilter(getColor(R.color.circlo_black))
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CHAT
    // ─────────────────────────────────────────────────────────────────────────

    private fun buatChatRoom(barang: BarangItem) {
        lifecycleScope.launch {
            try {
                // Cek apakah chat room sudah ada
                val existing = SupabaseClientProvider.client
                    .postgrest["chat_rooms"]
                    .select()
                    .decodeList<ChatRoom>()
                    .firstOrNull {
                        it.barangId == barang.id && it.requesterId == userId
                    }

                val roomId = if (existing != null) {
                    existing.id
                } else {
                    val newRoom = ChatRoom(
                        barangId = barang.id,
                        donorId = barang.userId,
                        requesterId = userId
                    )
                    SupabaseClientProvider.client
                        .postgrest["chat_rooms"]
                        .insert(newRoom)
                        .decodeSingle<ChatRoom>()
                        .id
                }

                val intent = Intent(this@BarangDetailActivity, ChatRoomActivity::class.java)
                intent.putExtra("room_id", roomId)
                intent.putExtra("other_username", barang.nama)
                startActivity(intent)

            } catch (e: Exception) {
                Toast.makeText(this@BarangDetailActivity,
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}