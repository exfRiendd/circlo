package com.example.myapplication.profile;

import android.os.Bundle;
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

public class SavedItemsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_items);

        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> finish());

        List<Barang> savedList = getSavedBarang();

        TextView tvJumlah = findViewById(R.id.tv_jumlah);
        tvJumlah.setText(savedList.size() + " barang yang kamu simpan");

        RecyclerView rvSaved = findViewById(R.id.rv_saved);
        rvSaved.setLayoutManager(new LinearLayoutManager(this));
        rvSaved.setAdapter(new BarangAdapter(savedList));
    }

    private List<Barang> getSavedBarang() {
        List<Barang> list = new ArrayList<>();
        list.add(new Barang("Kursi Kayu Vintage", "Furniture", "Jakarta Selatan", "2 jam lalu"));
        list.add(new Barang("Mainan Lego Set", "Mainan", "Bekasi", "2 hari lalu"));
        list.add(new Barang("Baju Batik Pria", "Pakaian", "Bogor", "5 jam lalu"));
        return list;
    }
}
