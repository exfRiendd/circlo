package com.example.myapplication.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.circlo.R;
import com.example.circlo.adapters.BarangAdapter;
import com.example.circlo.models.Barang;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private BarangAdapter adapter;
    private List<Barang> allBarang;
    private TextView tvJumlahHasil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allBarang = getDummyBarang();
        tvJumlahHasil = view.findViewById(R.id.tv_jumlah_hasil);

        RecyclerView rvHasil = view.findViewById(R.id.rv_hasil);
        rvHasil.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BarangAdapter(new ArrayList<>(allBarang));
        rvHasil.setAdapter(adapter);

        updateJumlah(allBarang.size());

        EditText etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBarang(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterBarang(String query) {
        List<Barang> filtered = new ArrayList<>();
        for (Barang b : allBarang) {
            if (b.getNama().toLowerCase().contains(query.toLowerCase())
                    || b.getKategori().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(b);
            }
        }
        adapter.updateData(filtered);
        updateJumlah(filtered.size());
    }

    private void updateJumlah(int jumlah) {
        tvJumlahHasil.setText(jumlah + " barang");
    }

    private List<Barang> getDummyBarang() {
        List<Barang> list = new ArrayList<>();
        list.add(new Barang("Kursi Kayu Vintage", "Furniture", "Jakarta Selatan", "2 jam lalu"));
        list.add(new Barang("Rak Buku Minimalis", "Furniture", "Jakarta Barat", "1 hari lalu"));
        list.add(new Barang("TV LED 32 inch", "Elektronik", "Depok", "3 jam lalu"));
        return list;
    }
}
