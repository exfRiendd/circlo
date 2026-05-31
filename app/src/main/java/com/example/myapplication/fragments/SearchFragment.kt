package com.example.myapplication.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
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
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var adapter: BarangAdapter
    private val allBarang = mutableListOf<Barang>()
    private lateinit var tvJumlahHasil: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvJumlahHasil = view.findViewById(R.id.tv_jumlah_hasil)
        val rvHasil: RecyclerView = view.findViewById(R.id.rv_hasil)
        rvHasil.layoutManager = LinearLayoutManager(context)
        adapter = BarangAdapter(mutableListOf())
        rvHasil.adapter = adapter

        loadBarang()

        view.findViewById<EditText>(R.id.et_search).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBarang(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadBarang() {
        lifecycleScope.launch {
            try {
                val result = SupabaseClientProvider.client
                    .postgrest["barang"]
                    .select()
                    .decodeList<BarangItem>()
                    .filter { it.status == "aktif" }

                allBarang.clear()
                allBarang.addAll(result.map {
                    val b = Barang(it.nama, it.kategori, it.lokasi, it.createdAt)
                    b.setId(it.id)
                    b.setUserId(it.userId)
                    b
                })

                adapter.updateData(allBarang)
                tvJumlahHasil.text = "${allBarang.size} barang"
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filterBarang(query: String) {
        val filtered = if (query.isEmpty()) {
            allBarang
        } else {
            allBarang.filter {
                it.nama.contains(query, ignoreCase = true) ||
                        it.kategori.contains(query, ignoreCase = true)
            }
        }
        adapter.updateData(filtered)
        tvJumlahHasil.text = "${filtered.size} barang"
    }
}