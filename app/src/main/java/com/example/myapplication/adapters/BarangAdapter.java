package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.models.Barang;
import java.util.List;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.ViewHolder> {

    public interface OnAmbilClickListener {
        void onAmbilClick(Barang barang);
    }

    public interface OnItemClickListener {
        void onItemClick(Barang barang);
    }

    private List<Barang> barangList;
    private OnAmbilClickListener listener;
    private OnItemClickListener itemClickListener;

    // 1. Konstruktor bawaan (Hanya List)
    public BarangAdapter(List<Barang> barangList) {
        this.barangList = barangList;
    }

    // 2. Konstruktor untuk SearchFragment & SavedItemsActivity (List + Klik Card)
    public BarangAdapter(List<Barang> barangList, OnItemClickListener itemClickListener) {
        this.barangList = barangList;
        this.itemClickListener = itemClickListener;
    }

    // 3. Konstruktor untuk HomeFragment (List + Klik Card + Klik Tombol Ambil)
    public BarangAdapter(List<Barang> barangList,
                         OnItemClickListener itemClickListener,
                         OnAmbilClickListener ambilClickListener) {
        this.barangList = barangList;
        this.itemClickListener = itemClickListener;
        this.listener = ambilClickListener;
    }

    public void updateData(List<Barang> newList) {
        this.barangList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_barang_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Barang barang = barangList.get(position);
        holder.tvNama.setText(barang.getNama());
        holder.tvKategori.setText(barang.getKategori());
        holder.tvLokasi.setText(barang.getLokasi());
        holder.tvWaktu.setText(barang.getWaktu());

        // Load foto dari URL
        if (barang.getFotoUrl() != null && !barang.getFotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(barang.getFotoUrl())
                    .placeholder(R.drawable.placeholder_item)
                    .error(R.drawable.placeholder_item)
                    .centerCrop()
                    .into(holder.ivFoto);
        } else {
            holder.ivFoto.setImageResource(R.drawable.placeholder_item);
        }

        // Action klik seluruh card (buka detail)
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(barang);
            }
        });

        // Action klik tombol ambil
        holder.btnAmbil.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAmbilClick(barang);
            } else {
                Toast.makeText(v.getContext(),
                        "Menghubungi pemilik " + barang.getNama(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {

            return barangList != null ? barangList.size() : 0;

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvKategori, tvLokasi, tvWaktu;
        Button btnAmbil;
        ImageView ivFoto;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvKategori = itemView.findViewById(R.id.tv_kategori);
            tvLokasi = itemView.findViewById(R.id.tv_lokasi);
            tvWaktu = itemView.findViewById(R.id.tv_waktu);
            btnAmbil = itemView.findViewById(R.id.btn_ambil);
            ivFoto = itemView.findViewById(R.id.iv_foto);
        }
    }
}