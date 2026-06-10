package com.example.myapplication.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.BarangItem

class AdminBarangAdapter(
    private val list: MutableList<BarangItem>,
    private val onDelete: (String) -> Unit,
    private val onItemClick: (String) -> Unit // ← TAMBAH PARAMETER INI
) : RecyclerView.Adapter<AdminBarangAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tv_nama)
        val tvKategori: TextView = view.findViewById(R.id.tv_kategori)
        val tvStatus: TextView = view.findViewById(R.id.tv_status)
        val btnHapus: Button = view.findViewById(R.id.btn_hapus_admin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_admin_barang, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.tvNama.text = item.nama
        holder.tvKategori.text = item.kategori
        holder.tvStatus.text = item.status

        // Aksi ketika tombol Hapus diklik
        holder.btnHapus.setOnClickListener { onDelete(item.id) }

        // ← TAMBAH INI: Aksi ketika area kartu diklik
        holder.itemView.setOnClickListener { onItemClick(item.id) }
    }

    override fun getItemCount() = list.size
}