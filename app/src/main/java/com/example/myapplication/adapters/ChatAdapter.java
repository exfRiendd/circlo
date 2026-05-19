package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.models.ChatItem;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final List<ChatItem> chatList;

    public ChatAdapter(List<ChatItem> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatItem item = chatList.get(position);
        holder.tvNama.setText(item.getNama());
        holder.tvItemName.setText(item.getNamaBarang());
        holder.tvLastMessage.setText(item.getPesanTerakhir());
        holder.tvWaktu.setText(item.getWaktu());

        if (item.getUnreadCount() > 0) {
            holder.tvUnread.setVisibility(View.VISIBLE);
            holder.tvUnread.setText(String.valueOf(item.getUnreadCount()));
        } else {
            holder.tvUnread.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvItemName, tvLastMessage, tvWaktu, tvUnread;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvWaktu = itemView.findViewById(R.id.tv_waktu);
            tvUnread = itemView.findViewById(R.id.tv_unread);
        }
    }
}
