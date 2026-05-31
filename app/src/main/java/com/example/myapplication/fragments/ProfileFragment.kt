package com.example.myapplication.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.auth.LoginOrSignUpActivity
import com.example.myapplication.models.Profile
import com.example.myapplication.network.SupabaseClientProvider
import com.example.myapplication.profile.AddressActivity
import com.example.myapplication.profile.MyItemsActivity
import com.example.myapplication.profile.NotificationActivity
import com.example.myapplication.profile.SavedItemsActivity
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    // Target donasi untuk mencapai level berikutnya
    private val ECO_TARGET = 10

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNama: TextView = view.findViewById(R.id.tv_nama)
        val tvLokasi: TextView = view.findViewById(R.id.tv_lokasi)
        val tvJoined: TextView = view.findViewById(R.id.tv_joined)
        val tvDiposting: TextView = view.findViewById(R.id.tv_diposting)
        val tvDiterima: TextView = view.findViewById(R.id.tv_diterima)
        val tvDibagikan: TextView = view.findViewById(R.id.tv_dibagikan)
        val progressEco: ProgressBar = view.findViewById(R.id.progress_eco)
        val tvDampakDesc: TextView = view.findViewById(R.id.tv_dampak_desc)
        val tvEcoTarget: TextView = view.findViewById(R.id.tv_eco_target)

        val userId = SupabaseClientProvider.client
            .auth.currentSessionOrNull()?.user?.id ?: return

        // Load profil dari Supabase
        lifecycleScope.launch {
            try {
                val profile = SupabaseClientProvider.client
                    .postgrest["profiles"]
                    .select { filter { eq("id", userId) } }
                    .decodeSingle<Profile>()

                tvNama.text = profile.username
                tvLokasi.text = profile.lokasi.ifEmpty { "Tap untuk tambah lokasi" }
                tvJoined.text = "Bergabung ${profile.createdAt.take(7)}"

                // Hitung jumlah barang yang diposting user
                val barangList = SupabaseClientProvider.client
                    .postgrest["barang"]
                    .select { filter { eq("user_id", userId) } }
                    .decodeList<com.example.myapplication.models.BarangItem>()

                val totalPosted = barangList.size
                val totalDonated = barangList.count { it.status == "diambil" }
                val totalReceived = profile.totalReceived

                tvDiposting.text = totalPosted.toString()
                tvDibagikan.text = totalDonated.toString()
                tvDiterima.text = totalReceived.toString()

                // ── ECO PROGRESS BAR ──────────────────────────────────────
                // Progress berdasarkan total barang yang berhasil didonasikan
                val progress = ((totalDonated % ECO_TARGET) * 100) / ECO_TARGET
                val level = totalDonated / ECO_TARGET + 1
                progressEco.progress = progress

                tvDampakDesc.text = when {
                    totalDonated == 0 -> "Mulai bagikan barangmu dan bantu kurangi sampah!"
                    totalDonated < 5  -> "Keren! $totalDonated barang sudah mendapat kehidupan baru 🌱"
                    totalDonated < 10 -> "Luar biasa! Kamu sudah menyelamatkan $totalDonated barang ♻️"
                    else              -> "Pahlawan lingkungan! $totalDonated barang sudah dibagikan 🌍"
                }

                val sisaKeDonasi = ECO_TARGET - (totalDonated % ECO_TARGET)
                tvEcoTarget.text = if (totalDonated == 0)
                    "Donasikan $ECO_TARGET barang untuk naik level!"
                else
                    "Level $level • $sisaKeDonasi donasi lagi untuk level ${level + 1}!"

            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Klik lokasi → edit
        view.findViewById<TextView>(R.id.tv_lokasi).setOnClickListener {
            showEditLokasiDialog(userId)
        }

        // Menu navigasi
        view.findViewById<View>(R.id.menu_barang_saya)
            .setOnClickListener { startActivity(Intent(activity, MyItemsActivity::class.java)) }
        view.findViewById<View>(R.id.menu_barang_tersimpan)
            .setOnClickListener { startActivity(Intent(activity, SavedItemsActivity::class.java)) }
        view.findViewById<View>(R.id.menu_alamat)
            .setOnClickListener { startActivity(Intent(activity, AddressActivity::class.java)) }
        view.findViewById<View>(R.id.menu_notifikasi)
            .setOnClickListener { startActivity(Intent(activity, NotificationActivity::class.java)) }

        // Logout
        view.findViewById<View>(R.id.menu_logout).setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Logout")
                .setMessage("Yakin ingin keluar?")
                .setPositiveButton("Logout") { _, _ ->
                    lifecycleScope.launch {
                        SupabaseClientProvider.client.auth.signOut()
                        startActivity(Intent(activity, LoginOrSignUpActivity::class.java))
                        activity?.finishAffinity()
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun showEditLokasiDialog(userId: String) {
        val etLokasi = EditText(context).apply {
            hint = "Contoh: Jakarta, Indonesia"
            setPadding(40, 20, 40, 20)
        }
        AlertDialog.Builder(context)
            .setTitle("Edit Lokasi")
            .setView(etLokasi)
            .setPositiveButton("Simpan") { _, _ ->
                val lokasi = etLokasi.text.toString().trim()
                if (lokasi.isNotEmpty()) {
                    lifecycleScope.launch {
                        try {
                            SupabaseClientProvider.client.postgrest["profiles"]
                                .update({ set("lokasi", lokasi) }) {
                                    filter { eq("id", userId) }
                                }
                            view?.findViewById<TextView>(R.id.tv_lokasi)?.text = lokasi
                            Toast.makeText(context, "Lokasi diperbarui", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}