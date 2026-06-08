package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.Address

class AddressAdapter(
    private val addresses: MutableList<Address>,
    private val onJadikanUtama: (Address) -> Unit,
    private val onEdit: (Address) -> Unit,
    private val onHapus: (Address) -> Unit
) : RecyclerView.Adapter<AddressAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvLabel: TextView = view.findViewById(R.id.tv_label)
        val tvBadgeUtama: TextView = view.findViewById(R.id.tv_badge_utama)
        val tvAlamat: TextView = view.findViewById(R.id.tv_alamat)
        val tvKota: TextView = view.findViewById(R.id.tv_kota)
        val tvCatatan: TextView = view.findViewById(R.id.tv_catatan)
        val btnJadikanUtama: TextView = view.findViewById(R.id.btn_jadikan_utama)
        val btnEdit: TextView = view.findViewById(R.id.btn_edit)
        val btnHapus: TextView = view.findViewById(R.id.btn_hapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_address_card, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val address = addresses[position]

        holder.tvLabel.text = address.label
        holder.tvAlamat.text = address.alamat
        holder.tvKota.text = address.kota
        holder.tvCatatan.text = if (address.catatan.isNotEmpty())
            "Patokan: ${address.catatan}" else ""
        holder.tvCatatan.visibility = if (address.catatan.isNotEmpty())
            View.VISIBLE else View.GONE

        if (address.isUtama) {
            holder.tvBadgeUtama.visibility = View.VISIBLE
            holder.btnJadikanUtama.visibility = View.GONE
        } else {
            holder.tvBadgeUtama.visibility = View.GONE
            holder.btnJadikanUtama.visibility = View.VISIBLE
        }

        holder.btnJadikanUtama.setOnClickListener { onJadikanUtama(address) }
        holder.btnEdit.setOnClickListener { onEdit(address) }
        holder.btnHapus.setOnClickListener { onHapus(address) }
    }

    override fun getItemCount() = addresses.size

    fun updateData(newList: List<Address>) {
        addresses.clear()
        addresses.addAll(newList)
        notifyDataSetChanged()
    }
}