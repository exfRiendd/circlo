package com.example.circlo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.circlo.R;
import com.example.circlo.models.Barang;
import java.util.List;

public class SavedItemAdapter extends RecyclerView.Adapter<SavedItemAdapter.ViewHolder> {

    private final List<Barang> barangList;

    public SavedItemAdapter(List<Barang> barangList) {
        this.barangList = barangList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Barang barang = barangList.get(position);
        holder.tvNama.setText(barang.getNama());
        holder.tvKategori.setText(barang.getKategori());
        holder.tvLokasi.setText(barang.getLokasi());

        holder.btnAmbil.setOnClickListener(v ->
                Toast.makeText(v.getContext(),
                        "Menghubungi pemilik " + barang.getNama(),
                        Toast.LENGTH_SHORT).show());

        holder.ivUnsave.setOnClickListener(v -> {
            barangList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, barangList.size());
            Toast.makeText(v.getContext(), "Dihapus dari simpanan", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return barangList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvKategori, tvLokasi;
        Button btnAmbil;
        ImageView ivUnsave;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvKategori = itemView.findViewById(R.id.tv_kategori);
            tvLokasi = itemView.findViewById(R.id.tv_lokasi);
            btnAmbil = itemView.findViewById(R.id.btn_ambil);
            ivUnsave = itemView.findViewById(R.id.iv_unsave);
        }
    }
}
