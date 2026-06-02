package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.BarangAdapter
import com.example.myapplication.chat.ChatRoomActivity
import com.example.myapplication.models.Barang
import com.example.myapplication.models.BarangItem
import com.example.myapplication.models.ChatRoom
import com.example.myapplication.network.SupabaseClientProvider
import com.example.myapplication.profile.NotificationActivity
import com.example.myapplication.utils.DateHelper
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivNotif: ImageView = view.findViewById(R.id.iv_notif)
        ivNotif.setOnClickListener {
            startActivity(Intent(activity, NotificationActivity::class.java))
        }

        val btnMulaiPosting: Button = view.findViewById(R.id.btn_mulai_posting)
        btnMulaiPosting.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PostFragment())
                .commit()
        }

        val rvBarang: RecyclerView = view.findViewById(R.id.rv_barang)
        rvBarang.layoutManager = LinearLayoutManager(context)

        loadBarang(rvBarang)

        val etSearch: EditText = view.findViewById(R.id.et_search)

        etSearch.setOnEditorActionListener { _, actionId, event ->
            val queryText = etSearch.text.toString().trim()

            val isEnterPressed = event?.keyCode == android.view.KeyEvent.KEYCODE_ENTER
                    && event.action == android.view.KeyEvent.ACTION_DOWN
            val isSearchAction = actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
                    || actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE

            if ((isSearchAction || isEnterPressed) && queryText.isNotEmpty()) {
                val searchFragment = SearchFragment().apply {
                    arguments = Bundle().also { it.putString("query", queryText) }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, searchFragment)
                    .commit()

                activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    R.id.bottom_nav
                )?.selectedItemId = R.id.nav_search

                true
            } else {
                false
            }
        }
    }

    private fun loadBarang(rv: RecyclerView) {
        lifecycleScope.launch {
            try {
                val result = SupabaseClientProvider.client
                    .postgrest["barang"]
                    .select()
                    .decodeList<BarangItem>()
                    .filter { it.status == "aktif" || it.status == "pending_pickup" }

                val barangList = result.map { item ->
                    Barang(item.nama, item.kategori, item.lokasi, DateHelper.toRelative(item.createdAt)).also { b ->
                        b.setId(item.id)
                        b.setUserId(item.userId)
                        b.setFotoUrl(item.fotoUrl)
                    }
                }

                // ← UPDATE jumlah barang
                view?.findViewById<TextView>(R.id.tv_jumlah_barang)
                    ?.text = "${barangList.size} barang"

                rv.adapter = BarangAdapter(barangList) { barang ->
                    buatChatRoom(barang)
                }

            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buatChatRoom(barang: Barang) {
        val myUserId = SupabaseClientProvider.client
            .auth.currentSessionOrNull()?.user?.id ?: return

        if (myUserId == barang.getUserId()) {
            Toast.makeText(context, "Ini barang kamu sendiri", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // Cek apakah room sudah ada
                val existing = SupabaseClientProvider.client
                    .postgrest["chat_rooms"]
                    .select()
                    .decodeList<ChatRoom>()
                    .firstOrNull {
                        it.barangId == barang.getId() && it.requesterId == myUserId
                    }

                val roomId = if (existing != null) {
                    existing.id
                } else {
                    // Buat room baru
                    val newRoom = ChatRoom(
                        barangId = barang.getId() ?: "",
                        donorId = barang.getUserId() ?: "",
                        requesterId = myUserId
                    )
                    val created = SupabaseClientProvider.client
                        .postgrest["chat_rooms"]
                        .insert(newRoom)
                        .decodeSingle<ChatRoom>()
                    created.id
                }

                val intent = Intent(requireContext(), ChatRoomActivity::class.java)
                intent.putExtra("room_id", roomId)
                intent.putExtra("other_username", barang.getNama())
                startActivity(intent)

            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}