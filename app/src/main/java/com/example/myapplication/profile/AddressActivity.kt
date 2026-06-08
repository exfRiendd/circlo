package com.example.myapplication.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.AddressAdapter
import com.example.myapplication.models.Address
import com.example.myapplication.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AddressActivity : AppCompatActivity() {

    private lateinit var adapter: AddressAdapter
    private val addressList = mutableListOf<Address>()
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        userId = SupabaseClientProvider.client
            .auth.currentSessionOrNull()?.user?.id ?: run { finish(); return }

        findViewById<ImageView>(R.id.iv_back).setOnClickListener { finish() }

        val rvAddresses: RecyclerView = findViewById(R.id.rv_addresses)
        rvAddresses.layoutManager = LinearLayoutManager(this)

        adapter = AddressAdapter(
            addressList,
            onJadikanUtama = { address -> jadikanUtama(address) },
            onEdit = { address -> showAddressDialog(address) },
            onHapus = { address -> hapusAddress(address) }
        )
        rvAddresses.adapter = adapter

        findViewById<Button>(R.id.btn_tambah).setOnClickListener {
            showAddressDialog(null)
        }

        loadAddresses()
    }

    // ── Load semua alamat user ────────────────────────────────────────────
    private fun loadAddresses() {
        lifecycleScope.launch {
            try {
                val result = SupabaseClientProvider.client
                    .postgrest["addresses"]
                    .select { filter { eq("user_id", userId) } }
                    .decodeList<Address>()
                    .sortedByDescending { it.isUtama }

                adapter.updateData(result)
            } catch (e: Exception) {
                Toast.makeText(this@AddressActivity,
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ── Dialog tambah / edit alamat ───────────────────────────────────────
    private fun showAddressDialog(existing: Address?) {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_address_form, null)

        val etLabel: EditText = dialogView.findViewById(R.id.et_label)
        val etAlamat: EditText = dialogView.findViewById(R.id.et_alamat)
        val etKota: EditText = dialogView.findViewById(R.id.et_kota)
        val etCatatan: EditText = dialogView.findViewById(R.id.et_catatan)

        // Isi data jika mode edit
        existing?.let {
            etLabel.setText(it.label)
            etAlamat.setText(it.alamat)
            etKota.setText(it.kota)
            etCatatan.setText(it.catatan)
        }

        AlertDialog.Builder(this)
            .setTitle(if (existing == null) "Tambah Alamat" else "Edit Alamat")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val label = etLabel.text.toString().trim()
                val alamat = etAlamat.text.toString().trim()
                val kota = etKota.text.toString().trim()
                val catatan = etCatatan.text.toString().trim()

                if (label.isEmpty() || alamat.isEmpty() || kota.isEmpty()) {
                    Toast.makeText(this, "Label, alamat, dan kota wajib diisi",
                        Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (existing == null) {
                    insertAddress(label, alamat, kota, catatan)
                } else {
                    updateAddress(existing.id, label, alamat, kota, catatan)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // ── Insert alamat baru ────────────────────────────────────────────────
    private fun insertAddress(
        label: String, alamat: String, kota: String, catatan: String
    ) {
        lifecycleScope.launch {
            try {
                val data = buildJsonObject {
                    put("user_id", userId)
                    put("label", label)
                    put("alamat", alamat)
                    put("kota", kota)
                    put("catatan", catatan)
                    put("is_utama", addressList.isEmpty()) // utama jika pertama
                }
                SupabaseClientProvider.client
                    .postgrest["addresses"]
                    .insert(data)

                Toast.makeText(this@AddressActivity,
                    "Alamat ditambahkan", Toast.LENGTH_SHORT).show()
                loadAddresses()
            } catch (e: Exception) {
                Toast.makeText(this@AddressActivity,
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ── Update alamat ─────────────────────────────────────────────────────
    private fun updateAddress(
        id: String, label: String, alamat: String, kota: String, catatan: String
    ) {
        lifecycleScope.launch {
            try {
                SupabaseClientProvider.client
                    .postgrest["addresses"]
                    .update({
                        set("label", label)
                        set("alamat", alamat)
                        set("kota", kota)
                        set("catatan", catatan)
                    }) { filter { eq("id", id) } }

                Toast.makeText(this@AddressActivity,
                    "Alamat diperbarui", Toast.LENGTH_SHORT).show()
                loadAddresses()
            } catch (e: Exception) {
                Toast.makeText(this@AddressActivity,
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ── Jadikan alamat utama ──────────────────────────────────────────────
    private fun jadikanUtama(address: Address) {
        lifecycleScope.launch {
            try {
                // Reset semua is_utama jadi false dulu
                SupabaseClientProvider.client
                    .postgrest["addresses"]
                    .update({ set("is_utama", false) }) {
                        filter { eq("user_id", userId) }
                    }

                // Set yang dipilih jadi true
                SupabaseClientProvider.client
                    .postgrest["addresses"]
                    .update({ set("is_utama", true) }) {
                        filter { eq("id", address.id) }
                    }

                Toast.makeText(this@AddressActivity,
                    "Alamat utama diperbarui", Toast.LENGTH_SHORT).show()
                loadAddresses()
            } catch (e: Exception) {
                Toast.makeText(this@AddressActivity,
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ── Hapus alamat ──────────────────────────────────────────────────────
    private fun hapusAddress(address: Address) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Alamat")
            .setMessage("Yakin ingin menghapus alamat ini?")
            .setPositiveButton("Hapus") { _, _ ->
                lifecycleScope.launch {
                    try {
                        SupabaseClientProvider.client
                            .postgrest["addresses"]
                            .delete { filter { eq("id", address.id) } }

                        Toast.makeText(this@AddressActivity,
                            "Alamat dihapus", Toast.LENGTH_SHORT).show()
                        loadAddresses()
                    } catch (e: Exception) {
                        Toast.makeText(this@AddressActivity,
                            "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}