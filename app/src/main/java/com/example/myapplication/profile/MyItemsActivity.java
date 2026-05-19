package com.example.myapplication.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.circlo.R;
import com.example.circlo.adapters.BarangAdapter;
import com.example.circlo.models.Barang;
import java.util.ArrayList;
import java.util.List;

public class MyItemsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);

        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> finish());

        TextView tabAktif = findViewById(R.id.tab_aktif);
        TextView tabSelesai = findViewById(R.id.tab_selesai);
        RecyclerView rvMyItems = findViewById(R.id.rv_my_items);
        rvMyItems.setLayoutManager(new LinearLayoutManager(this));

        List<Barang> aktifList = getBarangAktif();
        List<Barang> selesaiList = getBarangSelesai();

        rvMyItems.setAdapter(new BarangAdapter(aktifList));

        tabAktif.setOnClickListener(v -> {
            tabAktif.setBackgroundResource(R.drawable.bg_tab_active);
            tabAktif.setTextColor(getColor(R.color.circlo_green));
            tabSelesai.setBackground(null);
            tabSelesai.setTextColor(getColor(R.color.circlo_gray));
            rvMyItems.setAdapter(new BarangAdapter(aktifList));
        });

        tabSelesai.setOnClickListener(v -> {
            tabSelesai.setBackgroundResource(R.drawable.bg_tab_active);
            tabSelesai.setTextColor(getColor(R.color.circlo_green));
            tabAktif.setBackground(null);
            tabAktif.setTextColor(getColor(R.color.circlo_gray));
            rvMyItems.setAdapter(new BarangAdapter(selesaiList));
        });
    }

    private List<Barang> getBarangAktif() {
        List<Barang> list = new ArrayList<>();
        list.add(new Barang("Kursi Kayu Vintage", "Furniture", "Jakarta Selatan", "2 jam lalu"));
        list.add(new Barang("Rak Buku Minimalis", "Furniture", "Jakarta Barat", "1 hari lalu"));
        return list;
    }

    private List<Barang> getBarangSelesai() {
        List<Barang> list = new ArrayList<>();
        list.add(new Barang("TV LED 32 inch", "Elektronik", "Depok", "1 minggu lalu"));
        return list;
    }
}
