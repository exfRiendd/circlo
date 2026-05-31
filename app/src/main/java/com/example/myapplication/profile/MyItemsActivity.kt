package com.example.myapplication.profile

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.BarangAdapter
import com.example.myapplication.models.Barang
import com.example.myapplication.models.BarangItem
import com.example.myapplication.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class MyItemsActivity : AppCompatActivity() {

    private lateinit var rvMyItems: RecyclerView
    private lateinit var tabAktif: TextView
    private lateinit var tabSelesai: TextView

    private var aktifList = mutableListOf<Barang>()
    private var selesaiList = mutableListOf<Barang>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_items)

        findViewById<ImageView>(R.id.iv_back).setOnClickListener { finish() }

        tabAktif = findViewById(R.id.tab_aktif)
        tabSelesai = findViewById(R.id.tab_selesai)
        rvMyItems = findViewById(R.id.rv_my_items)
        rvMyItems.layoutManager = LinearLayoutManager(this)

        loadMyBarang()

        tabAktif.setOnClickListener { showTab("aktif") }
        tabSelesai.setOnClickListener { showTab("selesai") }
    }

    private fun loadMyBarang() {
        val userId = SupabaseClientProvider.client
            .auth.currentSessionOrNull()?.user?.id ?: return

        lifecycleScope.launch {
            try {
                val result = SupabaseClientProvider.client
                    .postgrest["barang"]
                    .select { filter { eq("user_id", userId) } }
                    .decodeList<BarangItem>()

                aktifList = result
                    .filter { it.status == "aktif" }
                    .map { Barang(it.nama, it.kategori, it.lokasi, it.createdAt).also { b ->
                        b.setId(it.id); b.setUserId(it.userId)
                    }}.toMutableList()

                selesaiList = result
                    .filter { it.status == "diambil" }
                    .map { Barang(it.nama, it.kategori, it.lokasi, it.createdAt).also { b ->
                        b.setId(it.id); b.setUserId(it.userId)
                    }}.toMutableList()

                showTab("aktif")
            } catch (e: Exception) {
                Toast.makeText(this@MyItemsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showTab(tab: String) {
        if (tab == "aktif") {
            tabAktif.setBackgroundResource(R.drawable.bg_tab_active)
            tabAktif.setTextColor(getColor(R.color.circlo_green))
            tabSelesai.background = null
            tabSelesai.setTextColor(getColor(R.color.circlo_gray))
            rvMyItems.adapter = BarangAdapter(aktifList)
            tabAktif.text = "Aktif (${aktifList.size})"
        } else {
            tabSelesai.setBackgroundResource(R.drawable.bg_tab_active)
            tabSelesai.setTextColor(getColor(R.color.circlo_green))
            tabAktif.background = null
            tabAktif.setTextColor(getColor(R.color.circlo_gray))
            rvMyItems.adapter = BarangAdapter(selesaiList)
            tabSelesai.text = "Selesai (${selesaiList.size})"
        }
    }
}