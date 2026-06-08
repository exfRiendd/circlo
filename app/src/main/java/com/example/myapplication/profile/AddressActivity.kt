package com.example.myapplication.profile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;

public class AddressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> finish());

        Button btnTambah = findViewById(R.id.btn_tambah);
        btnTambah.setOnClickListener(v ->
                Toast.makeText(this, "Fitur tambah alamat akan segera hadir", Toast.LENGTH_SHORT).show());

        RecyclerView rvAddresses = findViewById(R.id.rv_addresses);
        rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        // TODO: pasang AddressAdapter jika sudah ada data alamat
    }
}
