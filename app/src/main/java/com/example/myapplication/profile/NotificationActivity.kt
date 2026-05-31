package com.example.myapplication.profile

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.NotificationAdapter
import com.example.myapplication.models.NotifItem
import com.example.myapplication.models.NotificationDb
import com.example.myapplication.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class NotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        findViewById<ImageView>(R.id.iv_back).setOnClickListener { finish() }

        val tvTandaiSemua: TextView = findViewById(R.id.tv_tandai_semua)
        val tvBelumDibaca: TextView = findViewById(R.id.tv_belum_dibaca)
        val rvNotifications: RecyclerView = findViewById(R.id.rv_notifications)
        rvNotifications.layoutManager = LinearLayoutManager(this)

        val userId = SupabaseClientProvider.client
            .auth.currentSessionOrNull()?.user?.id ?: return

        lifecycleScope.launch {
            try {
                val dbNotifs = SupabaseClientProvider.client
                    .postgrest["notifications"]
                    .select { filter { eq("user_id", userId) } }
                    .decodeList<NotificationDb>()
                    .sortedByDescending { it.createdAt }

                val notifList = dbNotifs.map {
                    NotifItem(it.judul, it.konten, it.createdAt.take(10), it.sudahDibaca)
                }

                val belumDibaca = notifList.count { !it.isSudahDibaca }
                tvBelumDibaca.text = "$belumDibaca notifikasi belum dibaca"
                rvNotifications.adapter = NotificationAdapter(notifList.toMutableList())

                tvTandaiSemua.setOnClickListener {
                    lifecycleScope.launch {
                        try {
                            SupabaseClientProvider.client.postgrest["notifications"]
                                .update({ set("sudah_dibaca", true) }) {
                                    filter { eq("user_id", userId) }
                                }
                            tvBelumDibaca.text = "0 notifikasi belum dibaca"
                            Toast.makeText(
                                this@NotificationActivity,
                                "Semua notifikasi ditandai dibaca",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(this@NotificationActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(this@NotificationActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}