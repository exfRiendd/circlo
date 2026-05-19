package com.example.circlo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.circlo.R;
import com.example.circlo.models.AlamatItem;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private final List<AlamatItem> alamatList;

    public AddressAdapter(List<AlamatItem> alamatList) {
        this.alamatList = alamatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlamatItem item = alamatList.get(position);
        holder.tvLabel.setText(item.getLabel());
        holder.tvAlamat.setText(item.getAlamat());
        holder.tvKota.setText(item.getKota());
        holder.tvCatatan.setText(item.getCatatan());
        holder.tvBadgeUtama.setVisibility(item.isUtama() ? View.VISIBLE : View.GONE);

        holder.btnJadikanUtama.setVisibility(item.isUtama() ? View.GONE : View.VISIBLE);
        holder.btnJadikanUtama.setOnClickListener(v -> {
            for (AlamatItem a : alamatList) a.setUtama(false);
            item.setUtama(true);
            notifyDataSetChanged();
            Toast.makeText(v.getContext(), "Alamat utama diperbarui", Toast.LENGTH_SHORT).show();
        });

        holder.btnEdit.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Edit alamat: " + item.getLabel(), Toast.LENGTH_SHORT).show());

        holder.btnHapus.setOnClickListener(v -> {
            alamatList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, alamatList.size());
        });
    }

    @Override
    public int getItemCount() {
        return alamatList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvAlamat, tvKota, tvCatatan, tvBadgeUtama;
        TextView btnJadikanUtama, btnEdit, btnHapus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tv_label);
            tvAlamat = itemView.findViewById(R.id.tv_alamat);
            tvKota = itemView.findViewById(R.id.tv_kota);
            tvCatatan = itemView.findViewById(R.id.tv_catatan);
            tvBadgeUtama = itemView.findViewById(R.id.tv_badge_utama);
            btnJadikanUtama = itemView.findViewById(R.id.btn_jadikan_utama);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnHapus = itemView.findViewById(R.id.btn_hapus);
        }
    }
}
