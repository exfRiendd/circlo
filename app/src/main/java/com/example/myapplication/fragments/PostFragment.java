package com.example.myapplication.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapplication.R;

import com.example.myapplication.models.ApiResponse;
import com.example.myapplication.models.Barang;
import com.example.myapplication.network.ApiClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostFragment extends Fragment {

    private String kategoriDipilih = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etNamaBarang = view.findViewById(R.id.et_nama_barang);
        EditText etDeskripsi = view.findViewById(R.id.et_deskripsi);
        Button btnPosting = view.findViewById(R.id.btn_posting);
        LinearLayout llTambahFoto = view.findViewById(R.id.ll_tambah_foto);

        // Tombol-tombol kategori
        int[] kategoriIds = {
            R.id.btn_furniture, R.id.btn_elektronik, R.id.btn_pakaian,
            R.id.btn_mainan, R.id.btn_buku, R.id.btn_lainnya
        };
        String[] kategoriNama = {"Furniture", "Elektronik", "Pakaian", "Mainan", "Buku", "Lainnya"};

        for (int i = 0; i < kategoriIds.length; i++) {
            final String nama = kategoriNama[i];
            Button btn = view.findViewById(kategoriIds[i]);
            btn.setOnClickListener(v -> {
                kategoriDipilih = nama;
                // Reset semua tombol
                for (int id : kategoriIds) {
                    Button b = view.findViewById(id);
                    b.setBackgroundResource(R.drawable.bg_chip_unselected);
                    b.setTextColor(getResources().getColor(R.color.circlo_black, null));
                }
                // Tandai yang dipilih
                btn.setBackgroundResource(R.drawable.bg_chip_selected);
                btn.setTextColor(getResources().getColor(R.color.circlo_white, null));
            });
        }

        // Tambah foto (simulasi)
        llTambahFoto.setOnClickListener(v ->
                Toast.makeText(getContext(), "Fitur kamera/galeri", Toast.LENGTH_SHORT).show());

        // Submit posting
        btnPosting.setOnClickListener(v -> {
            String nama      = etNamaBarang.getText().toString().trim();
            String deskripsi = etDeskripsi.getText().toString().trim();
        
            if (nama.isEmpty() || deskripsi.isEmpty() || kategoriDipilih.isEmpty()) {
                Toast.makeText(getContext(), "Lengkapi semua field", Toast.LENGTH_SHORT).show();
                return;
            }
        
            // Ambil user_id dari SharedPreferences (lihat bagian Login)
            SharedPreferences prefs = requireActivity().getSharedPreferences("circlo_prefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("user_id", 0);
        
            Map<String, Object> body = new HashMap<>();
            body.put("user_id",   userId);
            body.put("nama",      nama);
            body.put("deskripsi", deskripsi);
            body.put("kategori",  kategoriDipilih);
            body.put("lokasi",    "Jakarta"); // Bisa diambil dari input atau GPS
        
            ApiClient.getApiService().addBarang(body).enqueue(new Callback<ApiResponse<Barang>>() {
                @Override
                public void onResponse(Call<ApiResponse<Barang>> call,
                                       Response<ApiResponse<Barang>> response) {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        Toast.makeText(getContext(), "Barang berhasil diposting!", Toast.LENGTH_SHORT).show();
                        etNamaBarang.setText("");
                        etDeskripsi.setText("");
                        kategoriDipilih = "";
                    } else {
                        Toast.makeText(getContext(), "Gagal posting barang", Toast.LENGTH_SHORT).show();
                    }
                }
        
                @Override
                public void onFailure(Call<ApiResponse<Barang>> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
