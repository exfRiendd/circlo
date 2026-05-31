package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.BarangAdapter
import com.example.myapplication.models.Barang
import com.example.myapplication.models.BarangItem
import com.example.myapplication.network.SupabaseClientProvider
import com.example.myapplication.profile.NotificationActivity
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
    }

    private fun loadBarang(rv: RecyclerView) {
        lifecycleScope.launch {
            try {
                val result = SupabaseClientProvider.client
                    .postgrest["barang"]
                    .select()
                    .decodeList<BarangItem>()

                // Konversi BarangItem ke Barang agar kompatibel dengan BarangAdapter
                val barangList = result.map {
                    Barang(it.nama, it.kategori, it.lokasi, it.createdAt)
                }

                rv.adapter = BarangAdapter(barangList)

            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}