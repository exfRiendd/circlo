package com.example.myapplication.profile;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.adapters.NotificationAdapter;
import com.example.myapplication.models.NotifItem;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> finish());

        TextView tvTandaiSemua = findViewById(R.id.tv_tandai_semua);
        tvTandaiSemua.setOnClickListener(v ->
                Toast.makeText(this, "Semua notifikasi ditandai dibaca", Toast.LENGTH_SHORT).show());

        List<NotifItem> notifList = getDummyNotif();

        TextView tvBelumDibaca = findViewById(R.id.tv_belum_dibaca);
        long belumDibaca = notifList.stream().filter(n -> !n.isSudahDibaca()).count();
        tvBelumDibaca.setText(belumDibaca + " notifikasi belum dibaca");

        RecyclerView rvNotifications = findViewById(R.id.rv_notifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(new NotificationAdapter(notifList));
    }

    private List<NotifItem> getDummyNotif() {
        List<NotifItem> list = new ArrayList<>();
        list.add(new NotifItem("Barang kamu diminati", "Sarah tertarik dengan Kursi Kayu Vintage", "5 menit lalu", false));
        list.add(new NotifItem("Pesan baru", "Reja: Apakah barangnya masih tersedia?", "10 menit lalu", false));
        list.add(new NotifItem("Barang diambil", "TV LED 32 inch telah berhasil diambil", "1 hari lalu", true));
        return list;
    }
}
