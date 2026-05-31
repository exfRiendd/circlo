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
import com.example.myapplication.models.SavedItem
import com.example.myapplication.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class SavedItemsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_items)

        findViewById<ImageView>(R.id.iv_back).setOnClickListener { finish() }

        val tvJumlah: TextView = findViewById(R.id.tv_jumlah)
        val rvSaved: RecyclerView = findViewById(R.id.rv_saved)
        rvSaved.layoutManager = LinearLayoutManager(this)

        val userId = SupabaseClientProvider.client
            .auth.currentSessionOrNull()?.user?.id ?: return

        lifecycleScope.launch {
            try {
                // Ambil semua saved_items milik user
                val savedItems = SupabaseClientProvider.client
                    .postgrest["saved_items"]
                    .select { filter { eq("user_id", userId) } }
                    .decodeList<SavedItem>()

                // Ambil detail barang untuk setiap saved item
                val barangIds = savedItems.map { it.barangId }
                val barangList = mutableListOf<Barang>()

                for (barangId in barangIds) {
                    try {
                        val item = SupabaseClientProvider.client
                            .postgrest["barang"]
                            .select { filter { eq("id", barangId) } }
                            .decodeSingle<BarangItem>()
                        val b = Barang(item.nama, item.kategori, item.lokasi, item.createdAt)
                        b.setId(item.id)
                        b.setUserId(item.userId)
                        barangList.add(b)
                    } catch (_: Exception) {}
                }

                tvJumlah.text = "${barangList.size} barang yang kamu simpan"
                rvSaved.adapter = BarangAdapter(barangList)
            } catch (e: Exception) {
                Toast.makeText(this@SavedItemsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}