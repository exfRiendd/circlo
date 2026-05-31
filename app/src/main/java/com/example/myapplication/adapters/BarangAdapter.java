package com.example.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.models.Barang;
import java.util.List;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.ViewHolder> {

    public interface OnAmbilClickListener {
        void onAmbilClick(Barang barang);
    }

    private List<Barang> barangList;
    private OnAmbilClickListener listener;

    public BarangAdapter(List<Barang> barangList) {
        this.barangList = barangList;
    }

    public BarangAdapter(List<Barang> barangList, OnAmbilClickListener listener) {
        this.barangList = barangList;
        this.listener = listener;
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
        return barangList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvKategori, tvLokasi, tvWaktu;
        Button btnAmbil;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvKategori = itemView.findViewById(R.id.tv_kategori);
            tvLokasi = itemView.findViewById(R.id.tv_lokasi);
            tvWaktu = itemView.findViewById(R.id.tv_waktu);
            btnAmbil = itemView.findViewById(R.id.btn_ambil);
        }
    }
}