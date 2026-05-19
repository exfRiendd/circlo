package com.example.circlo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.circlo.R;
import com.example.circlo.models.Barang;
import java.util.List;

public class MyItemAdapter extends RecyclerView.Adapter<MyItemAdapter.ViewHolder> {

    private List<Barang> barangList;

    public MyItemAdapter(List<Barang> barangList) {
        this.barangList = barangList;
    }

    public void updateData(List<Barang> newList) {
        this.barangList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Barang barang = barangList.get(position);
        holder.tvNama.setText(barang.getNama());
        holder.tvKategori.setText(barang.getKategori());
        holder.tvWaktu.setText("Diposting " + barang.getWaktu());

        holder.ivMore.setOnClickListener(v ->
                Toast.makeText(v.getContext(),
                        "Opsi untuk " + barang.getNama(),
                        Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return barangList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvKategori, tvStatus, tvWaktu;
        ImageView ivFoto, ivMore;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvKategori = itemView.findViewById(R.id.tv_kategori);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvWaktu = itemView.findViewById(R.id.tv_waktu);
            ivFoto = itemView.findViewById(R.id.iv_foto);
            ivMore = itemView.findViewById(R.id.iv_more);
        }
    }
}
