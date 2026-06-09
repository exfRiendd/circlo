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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var rvBarang: RecyclerView
    private var allBarangList = mutableListOf<Barang>()
    private var activeCategory = "Semua"

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

        rvBarang = view.findViewById(R.id.rv_barang)
        rvBarang.layoutManager = LinearLayoutManager(context)

        val etSearch: EditText = view.findViewById(R.id.et_search)

        // Logika saat pengguna menekan Enter / Ikon Cari di Keyboard
        etSearch.setOnEditorActionListener { _, actionId, event ->
            val queryText = etSearch.text.toString().trim()

            val isEnterPressed = event?.keyCode == android.view.KeyEvent.KEYCODE_ENTER
                    && event.action == android.view.KeyEvent.ACTION_DOWN
            val isSearchAction = actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
                    || actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE

            if ((isSearchAction || isEnterPressed) && queryText.isNotEmpty()) {

                // 1. Pindahkan tab bawah ke Search
                activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    R.id.bottom_nav
                )?.selectedItemId = R.id.nav_search

                // 2. Timpa fragment kosong dengan SearchFragment yang membawa query
                val searchFragment = SearchFragment().apply {
                    arguments = Bundle().also { it.putString("query", queryText) }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, searchFragment)
                    .commit()

                true
            } else {
                false
            }
        }

        // Logika saat search bar sekadar diklik
        etSearch.setOnClickListener {
            activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                R.id.bottom_nav
            )?.selectedItemId = R.id.nav_search
        }

        // Setup filter kategori
        setupCategoryChips(view)

        // Load data pertama kali
        loadBarang()
    }

    override fun onResume() {
        super.onResume()
        if (::rvBarang.isInitialized) {
            loadBarang()
        }
    }

    private fun loadBarang() {
        lifecycleScope.launch {
            try {
                val result = SupabaseClientProvider.client
                    .postgrest["barang"]
                    .select()
                    .decodeList<BarangItem>()

                val filtered = result.filter {
                    it.status == "aktif" || it.status == "pending_pickup"
                }

                val barangList = filtered.map { item ->
                    Barang(item.nama, item.kategori, item.lokasi, DateHelper.toRelative(item.createdAt)).also { b ->
                        b.setId(item.id)
                        b.setUserId(item.userId)
                        b.setFotoUrl(item.fotoUrl)
                    }
                }

                // Simpan ke list global dan panggil fungsi filter
                allBarangList.clear()
                allBarangList.addAll(barangList)
                filterData()

            } catch (e: CancellationException) {
                // Abaikan error coroutine saat berpindah tab
                throw e
            } catch (e: Exception) {
                // Cegah crash dengan memastikan context tidak null
                context?.let { safeContext ->
                    Toast.makeText(safeContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupCategoryChips(view: View) {
        val chips = mapOf(
            "Semua" to view.findViewById<TextView>(R.id.chip_semua),
            "Furniture" to view.findViewById<TextView>(R.id.chip_furniture),
            "Elektronik" to view.findViewById<TextView>(R.id.chip_elektronik),
            "Pakaian" to view.findViewById<TextView>(R.id.chip_pakaian),
            "Mainan" to view.findViewById<TextView>(R.id.chip_mainan),
            "Buku" to view.findViewById<TextView>(R.id.chip_buku),
            "Lainnya" to view.findViewById<TextView>(R.id.chip_lainnya)
        )

        chips.forEach { (kategori, textView) ->
            textView?.setOnClickListener {
                // 1. Reset warna semua chip jadi unselected
                chips.values.forEach { chip ->
                    chip?.setBackgroundResource(R.drawable.bg_chip_unselected)
                    chip?.setTextColor(resources.getColor(R.color.circlo_black, null))
                }

                // 2. Ubah warna chip yang sedang diklik jadi hijau
                textView.setBackgroundResource(R.drawable.bg_chip_selected)
                textView.setTextColor(resources.getColor(R.color.circlo_white, null))

                // 3. Set kategori aktif dan saring datanya
                activeCategory = kategori
                filterData()
            }
        }
    }

    private fun filterData() {
        // Saring data berdasarkan kategori yang dipilih
        val filteredList = if (activeCategory == "Semua") {
            allBarangList
        } else {
            allBarangList.filter { it.kategori.equals(activeCategory, ignoreCase = true) }
        }

        // Update teks jumlah barang
        view?.findViewById<TextView>(R.id.tv_jumlah_barang)?.text = "${filteredList.size} barang"

        // Set adapter dengan list yang sudah difilter
        rvBarang.adapter = BarangAdapter(
            filteredList,
            BarangAdapter.OnItemClickListener { barang ->
                // Klik card -> ke halaman detail
                val intent = Intent(requireContext(), com.example.myapplication.chat.BarangDetailActivity::class.java)
                intent.putExtra("barang_id", barang.getId())
                startActivity(intent)
            },
            BarangAdapter.OnAmbilClickListener { barang ->
                // Klik tombol Ambil -> buat chat room
                buatChatRoom(barang)
            }
        )
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

            } catch (e: CancellationException) {
                // Abaikan pembatalan
                throw e
            } catch (e: Exception) {
                context?.let { safeContext ->
                    Toast.makeText(safeContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}