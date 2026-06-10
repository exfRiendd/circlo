package com.example.myapplication.admin

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.BarangItem
import com.example.myapplication.models.Profile
import com.example.myapplication.network.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val tvTotalBarang: TextView = findViewById(R.id.tv_total_barang)
        val tvTotalUser: TextView = findViewById(R.id.tv_total_user)
        val tvTotalDonasi: TextView = findViewById(R.id.tv_total_donasi)
        val rvBarang: RecyclerView = findViewById(R.id.rv_admin_barang)
        rvBarang.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            try {
                val supabase = SupabaseClientProvider.client

                val allBarang = supabase.postgrest["barang"]
                    .select()
                    .decodeList<BarangItem>()

                val totalUsers = supabase.postgrest["profiles"]
                    .select()
                    .decodeList<Profile>()

                val totalDonasi = allBarang.count { it.status == "diambil" }

                tvTotalBarang.text = allBarang.size.toString()
                tvTotalUser.text = totalUsers.size.toString()
                tvTotalDonasi.text = totalDonasi.toString()

                rvBarang.adapter = AdminBarangAdapter(
                    allBarang.toMutableList()
                ) { barangId ->
                    val barangYangDihapus = allBarang.find { it.id == barangId }
                    val urlFoto = barangYangDihapus?.fotoUrl ?: ""

                    deleteBarang(barangId, urlFoto)
                }

            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity,
                    "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deleteBarang(barangId: String, fotoUrl: String) {
        lifecycleScope.launch {
            try {
                // 1. Hapus fotonya dulu dari Storage (jika ada)
                if (fotoUrl.isNotEmpty()) {
                    val bucket = "barang-foto"
                    // Ekstrak nama file dari URL (contoh: "userId/uuid.jpg")
                    val fileName = fotoUrl.substringAfterLast("$bucket/")
                    SupabaseClientProvider.client.storage[bucket].delete(fileName)
                }

                // 2. Hapus baris datanya dari Database
                SupabaseClientProvider.client.postgrest["barang"]
                    .delete {
                        filter { eq("id", barangId) }
                    }

                Toast.makeText(this@AdminDashboardActivity, "Barang dan foto dihapus", Toast.LENGTH_SHORT).show()
                recreate()
            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}