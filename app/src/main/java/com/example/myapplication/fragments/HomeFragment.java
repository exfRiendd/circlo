package com.example.myapplication.fragments;

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
import com.example.myapplication.R;
import com.example.myapplication.adapters.BarangAdapter;
import com.example.myapplication.models.Barang;
import com.example.myapplication.profile.NotificationActivity;
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
        loadBarangFromApi();
    }

    private void loadBarangFromApi() {
    ApiService api = ApiClient.getApiService();
    api.getBarang().enqueue(new Callback<ApiResponse<List<Barang>>>() {
        @Override
        public void onResponse(Call<ApiResponse<List<Barang>>> call,
                               Response<ApiResponse<List<Barang>>> response) {
            if (response.isSuccessful() && response.body() != null
                    && response.body().isSuccess()) {
                List<Barang> barangList = response.body().getData();
                rvBarang.setAdapter(new BarangAdapter(barangList));
            } else {
                Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<List<Barang>>> call, Throwable t) {
            Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}
}
