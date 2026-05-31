package com.example.myapplication.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.models.BarangItem
import com.example.myapplication.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class PostFragment : Fragment() {

    private var kategoriDipilih = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_post, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etNama: EditText = view.findViewById(R.id.et_nama_barang)
        val etDeskripsi: EditText = view.findViewById(R.id.et_deskripsi)
        val btnPosting: Button = view.findViewById(R.id.btn_posting)
        val llFoto: LinearLayout = view.findViewById(R.id.ll_tambah_foto)

        llFoto.setOnClickListener {
            Toast.makeText(context, "Fitur kamera/galeri", Toast.LENGTH_SHORT).show()
        }

        // Kategori chips
        val kategoriIds = intArrayOf(
            R.id.btn_furniture, R.id.btn_elektronik, R.id.btn_pakaian,
            R.id.btn_mainan, R.id.btn_buku, R.id.btn_lainnya
        )
        val kategoriNama = arrayOf("Furniture", "Elektronik", "Pakaian", "Mainan", "Buku", "Lainnya")

        kategoriIds.forEachIndexed { index, id ->
            val btn = view.findViewById<Button>(id)
            btn.setOnClickListener {
                kategoriDipilih = kategoriNama[index]
                // Reset semua
                kategoriIds.forEach { btnId ->
                    view.findViewById<Button>(btnId).apply {
                        setBackgroundResource(R.drawable.bg_chip_unselected)
                        setTextColor(resources.getColor(R.color.circlo_black, null))
                    }
                }
                // Tandai dipilih
                btn.setBackgroundResource(R.drawable.bg_chip_selected)
                btn.setTextColor(resources.getColor(R.color.circlo_white, null))
            }
        }

        btnPosting.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val deskripsi = etDeskripsi.text.toString().trim()

            if (nama.isEmpty() || deskripsi.isEmpty() || kategoriDipilih.isEmpty()) {
                Toast.makeText(context, "Lengkapi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = SupabaseClientProvider.client
                .auth.currentSessionOrNull()?.user?.id ?: run {
                Toast.makeText(context, "Silakan login ulang", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val barang = BarangItem(
                        userId = userId,
                        nama = nama,
                        deskripsi = deskripsi,
                        kategori = kategoriDipilih,
                        lokasi = "Jakarta"
                    )

                    SupabaseClientProvider.client
                        .postgrest["barang"]
                        .insert(barang)

                    Toast.makeText(context, "Barang berhasil diposting!", Toast.LENGTH_SHORT).show()
                    etNama.setText("")
                    etDeskripsi.setText("")
                    kategoriDipilih = ""
                    // Reset semua chip
                    kategoriIds.forEach { btnId ->
                        view.findViewById<Button>(btnId).apply {
                            setBackgroundResource(R.drawable.bg_chip_unselected)
                            setTextColor(resources.getColor(R.color.circlo_black, null))
                        }
                    }

                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}