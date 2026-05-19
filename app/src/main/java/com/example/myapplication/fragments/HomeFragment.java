package com.example.circlo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.circlo.R;
import com.example.circlo.adapters.BarangAdapter;
import com.example.circlo.models.Barang;
import com.example.circlo.profile.NotificationActivity;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Notifikasi icon
        ImageView ivNotif = view.findViewById(R.id.iv_notif);
        ivNotif.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), NotificationActivity.class)));

        // Tombol mulai posting di banner
        Button btnMulaiPosting = view.findViewById(R.id.btn_mulai_posting);
        btnMulaiPosting.setOnClickListener(v -> {
            if (getActivity() != null) {
                ((androidx.fragment.app.FragmentActivity) getActivity())
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new PostFragment())
                        .commit();
            }
        });

        // RecyclerView barang
        RecyclerView rvBarang = view.findViewById(R.id.rv_barang);
        rvBarang.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBarang.setAdapter(new BarangAdapter(getDummyBarang()));
    }

    private List<Barang> getDummyBarang() {
        List<Barang> list = new ArrayList<>();
        list.add(new Barang("Kursi Kayu Vintage", "Furniture", "Jakarta Selatan", "2 jam lalu"));
        list.add(new Barang("Rak Buku Minimalis", "Furniture", "Jakarta Barat", "1 hari lalu"));
        list.add(new Barang("TV LED 32 inch", "Elektronik", "Depok", "3 jam lalu"));
        list.add(new Barang("Baju Batik Pria", "Pakaian", "Bogor", "5 jam lalu"));
        list.add(new Barang("Mainan Lego Set", "Mainan", "Bekasi", "2 hari lalu"));
        return list;
    }
}
