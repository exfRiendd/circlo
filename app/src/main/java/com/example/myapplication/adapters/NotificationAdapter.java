package com.example.circlo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.circlo.R;
import com.example.circlo.models.NotifItem;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<NotifItem> notifList;

    public NotificationAdapter(List<NotifItem> notifList) {
        this.notifList = notifList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotifItem item = notifList.get(position);
        holder.tvTitle.setText(item.getJudul());
        holder.tvContent.setText(item.getKonten());
        holder.tvWaktu.setText(item.getWaktu());

        // Latar belakang beda jika belum dibaca
        holder.itemView.setAlpha(item.isSudahDibaca() ? 0.6f : 1f);

        holder.ivDelete.setOnClickListener(v -> {
            notifList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, notifList.size());
        });
    }

    @Override
    public int getItemCount() {
        return notifList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvWaktu;
        ImageView ivDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvWaktu = itemView.findViewById(R.id.tv_waktu);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
}
