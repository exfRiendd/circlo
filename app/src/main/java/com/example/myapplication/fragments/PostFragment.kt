package com.example.myapplication.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.models.BarangItem
import com.example.myapplication.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PostFragment : Fragment() {

    private var kategoriDipilih = ""

    // ── URI foto yang dipilih/diambil ──────────────────────────────────────
    private var selectedImageUri: Uri? = null
    private var cameraImageUri: Uri? = null   // URI sementara untuk kamera

    // ── ImageView preview (akan kita tambah di layout) ─────────────────────
    private lateinit var ivPreview: ImageView

    // ── Activity Result: Galeri ────────────────────────────────────────────
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult
            selectedImageUri = uri
            showPreview(uri)
        }
    }

    // ── Activity Result: Kamera ────────────────────────────────────────────
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            selectedImageUri = cameraImageUri
            showPreview(cameraImageUri!!)
        }
    }

    // ── Activity Result: Minta izin ────────────────────────────────────────
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) openGallery()
        else Toast.makeText(context, "Izin diperlukan untuk memilih foto", Toast.LENGTH_SHORT).show()
    }

    // ──────────────────────────────────────────────────────────────────────

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_post, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etNama: EditText     = view.findViewById(R.id.et_nama_barang)
        val etDeskripsi: EditText = view.findViewById(R.id.et_deskripsi)
        val btnPosting: Button   = view.findViewById(R.id.btn_posting)
        val llFoto: LinearLayout = view.findViewById(R.id.ll_tambah_foto)
        ivPreview                = view.findViewById(R.id.iv_foto_preview)

        // ── Klik area tambah foto → pilih galeri atau kamera ──────────────
        llFoto.setOnClickListener { showImageSourceDialog() }

        // ── Kategori chips ────────────────────────────────────────────────
        val kategoriIds  = intArrayOf(
            R.id.btn_furniture, R.id.btn_elektronik, R.id.btn_pakaian,
            R.id.btn_mainan, R.id.btn_buku, R.id.btn_lainnya
        )
        val kategoriNama = arrayOf("Furniture", "Elektronik", "Pakaian", "Mainan", "Buku", "Lainnya")

        kategoriIds.forEachIndexed { index, id ->
            val btn = view.findViewById<Button>(id)
            btn.setOnClickListener {
                kategoriDipilih = kategoriNama[index]
                kategoriIds.forEach { btnId ->
                    view.findViewById<Button>(btnId).apply {
                        setBackgroundResource(R.drawable.bg_chip_unselected)
                        setTextColor(resources.getColor(R.color.circlo_black, null))
                    }
                }
                btn.setBackgroundResource(R.drawable.bg_chip_selected)
                btn.setTextColor(resources.getColor(R.color.circlo_white, null))
            }
        }

        // ── Tombol posting ────────────────────────────────────────────────
        btnPosting.setOnClickListener {
            val nama      = etNama.text.toString().trim()
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

            btnPosting.isEnabled = false
            btnPosting.text = "Memposting..."

            lifecycleScope.launch {
                try {
                    // 1. Upload foto ke Supabase Storage (jika ada)
                    val fotoUrl = selectedImageUri?.let { uri ->
                        uploadFotoToSupabase(uri, userId)
                    } ?: ""

                    // 2. Insert barang ke database
                    val barang = BarangItem(
                        userId    = userId,
                        nama      = nama,
                        deskripsi = deskripsi,
                        kategori  = kategoriDipilih,
                        lokasi    = "Jakarta",
                        fotoUrl   = fotoUrl
                    )

                    SupabaseClientProvider.client
                        .postgrest["barang"]
                        .insert(barang)

                    Toast.makeText(context, "Barang berhasil diposting!", Toast.LENGTH_SHORT).show()
                    resetForm(etNama, etDeskripsi, kategoriIds, view)

                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    btnPosting.isEnabled = true
                    btnPosting.text = "Posting Barang"
                }
            }
        }
    }

    // ── Dialog pilih sumber foto ───────────────────────────────────────────
    private fun showImageSourceDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Foto")
            .setItems(arrayOf("📷  Ambil dari Kamera", "🖼️  Pilih dari Galeri")) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> checkGalleryPermissionAndOpen()
                }
            }
            .show()
    }

    // ── Galeri ─────────────────────────────────────────────────────────────
    private fun checkGalleryPermissionAndOpen() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            android.Manifest.permission.READ_MEDIA_IMAGES
        else
            android.Manifest.permission.READ_EXTERNAL_STORAGE

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission)
                    == PackageManager.PERMISSION_GRANTED -> openGallery()
            else -> requestPermissionLauncher.launch(permission)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    // ── Kamera ─────────────────────────────────────────────────────────────
    private fun openCamera() {
        val imageFile = createTempImageFile()
        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            imageFile
        )
        takePictureLauncher.launch(cameraImageUri)
    }

    private fun createTempImageFile(): File {
        val timestamp  = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timestamp}_", ".jpg", storageDir)
    }

    // ── Preview gambar ─────────────────────────────────────────────────────
    private fun showPreview(uri: Uri) {
        ivPreview.visibility = View.VISIBLE
        Glide.with(this).load(uri).centerCrop().into(ivPreview)
    }

    // ── Upload ke Supabase Storage ─────────────────────────────────────────
    private suspend fun uploadFotoToSupabase(uri: Uri, userId: String): String {
        val ctx         = requireContext()
        val inputStream = ctx.contentResolver.openInputStream(uri)
            ?: throw Exception("Gagal membaca file gambar")

        val bytes    = inputStream.readBytes()
        inputStream.close()

        // Nama file unik: userId/uuid.jpg
        val fileName = "$userId/${UUID.randomUUID()}.jpg"
        val bucket   = "barang-foto"

        SupabaseClientProvider.client
            .storage[bucket]
            .upload(fileName, bytes) {
                upsert = false
            }

        // Ambil public URL
        return SupabaseClientProvider.client
            .storage[bucket]
            .publicUrl(fileName)
    }

    // ── Reset form setelah posting ─────────────────────────────────────────
    private fun resetForm(
        etNama: EditText,
        etDeskripsi: EditText,
        kategoriIds: IntArray,
        view: View
    ) {
        etNama.setText("")
        etDeskripsi.setText("")
        kategoriDipilih  = ""
        selectedImageUri = null
        ivPreview.visibility = View.GONE

        kategoriIds.forEach { btnId ->
            view.findViewById<Button>(btnId).apply {
                setBackgroundResource(R.drawable.bg_chip_unselected)
                setTextColor(resources.getColor(R.color.circlo_black, null))
            }
        }
    }
}