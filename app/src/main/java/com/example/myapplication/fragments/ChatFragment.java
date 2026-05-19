package com.example.circlo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.circlo.R;
import com.example.circlo.adapters.ChatAdapter;
import com.example.circlo.models.ChatItem;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvChats = view.findViewById(R.id.rv_chats);
        rvChats.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChats.setAdapter(new ChatAdapter(getDummyChats()));
    }

    private List<ChatItem> getDummyChats() {
        List<ChatItem> list = new ArrayList<>();
        list.add(new ChatItem("Reja Touring", "Rak Buku Minimalis", "Apakah barangnya masih tersedia?", "09.15", 3));
        list.add(new ChatItem("Sarah Amelia", "Kursi Kayu Vintage", "Oke, saya ambil besok ya", "08.30", 0));
        list.add(new ChatItem("Budi Santoso", "TV LED 32 inch", "Makasih kak barangnya!", "Kemarin", 0));
        return list;
    }
}
