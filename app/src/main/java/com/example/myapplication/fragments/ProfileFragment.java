package com.example.myapplication.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapplication.R;
import com.example.myapplication.auth.SignUpActivity;
import com.example.myapplication.models.ApiResponse;
import com.example.myapplication.models.User;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.profile.AddressActivity;
import com.example.myapplication.profile.MyItemsActivity;
import com.example.myapplication.profile.NotificationActivity;
import com.example.myapplication.profile.SavedItemsActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvNama, tvLokasi, tvJoined;
    private int userId;
    private String currentLokasi = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNama   = view.findViewById(R.id.tv_nama);
        tvLokasi = view.findViewById(R.id.tv_lokasi);
        tvJoined = view.findViewById(R.id.tv_joined);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("circlo_prefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", 0);

        // Load profil dari API
        loadProfile();

        // Klik lokasi untuk edit
        tvLokasi.setOnClickListener(v -> showEditLokasiDialog());

        // Menu navigasi
        view.findViewById(R.id.menu_barang_saya).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MyItemsActivity.class)));

        view.findViewById(R.id.menu_barang_tersimpan).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), SavedItemsActivity.class)));

        view.findViewById(R.id.menu_alamat).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AddressActivity.class)));

        view.findViewById(R.id.menu_notifikasi).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), NotificationActivity.class)));

        // Logout
        view.findViewById(R.id.menu_logout).setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Logout")
                    .setMessage("Yakin ingin keluar?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        requireActivity().getSharedPreferences("circlo_prefs", Context.MODE_PRIVATE)
                                .edit().clear().apply();
                        startActivity(new Intent(getActivity(), SignUpActivity.class));
                        requireActivity().finishAffinity();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void loadProfile() {
        ApiClient.getApiService().getProfile(userId).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call,
                                   Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    User user = response.body().getUser();

                    tvNama.setText(user.getUsername());

                    if (user.getLokasi() != null && !user.getLokasi().isEmpty()) {
                        tvLokasi.setText( user.getLokasi());
                        currentLokasi = user.getLokasi();
                    } else {
                        tvLokasi.setText(" Tap untuk tambah lokasi");
                    }

                    // Format tanggal bergabung
                    if (user.getCreatedAt() != null) {
                        try {
                            SimpleDateFormat inputFormat = new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            SimpleDateFormat outputFormat = new SimpleDateFormat(
                                    "MMMM yyyy", new Locale("id", "ID"));
                            Date date = inputFormat.parse(user.getCreatedAt());
                            tvJoined.setText("Bergabung " + outputFormat.format(date));
                        } catch (Exception e) {
                            tvJoined.setText("Bergabung " + user.getCreatedAt());
                        }
                    }

                } else {
                    Toast.makeText(getContext(),
                            "Response gagal: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showEditLokasiDialog() {
        EditText etLokasi = new EditText(getContext());
        etLokasi.setHint("Contoh: Jakarta, Indonesia");
        etLokasi.setText(currentLokasi);
        etLokasi.setPadding(40, 20, 40, 20);

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Lokasi")
                .setView(etLokasi)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String lokasi = etLokasi.getText().toString().trim();
                    if (!lokasi.isEmpty()) {
                        updateLokasi(lokasi);
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void updateLokasi(String lokasi) {
        Map<String, Object> body = new HashMap<>();
        body.put("user_id", userId);
        body.put("lokasi", lokasi);

        ApiClient.getApiService().updateProfile(body).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call,
                                   Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    currentLokasi = lokasi;
                    tvLokasi.setText( lokasi);
                    Toast.makeText(getContext(),
                            "Lokasi berhasil diperbarui", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),
                            "Gagal memperbarui lokasi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Tidak dapat terhubung ke server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}