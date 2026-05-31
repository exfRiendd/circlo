package com.example.myapplication.utils

import java.time.ZonedDateTime
import java.time.ZoneId
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateHelper {

    fun toRelative(isoString: String): String {
        return try {
            val time = ZonedDateTime.parse(isoString)
                .withZoneSameInstant(ZoneId.systemDefault())
            val now = ZonedDateTime.now()
            val diff = Duration.between(time, now)

            when {
                diff.toMinutes() < 1    -> "Baru saja"
                diff.toMinutes() < 60   -> "${diff.toMinutes()} menit lalu"
                diff.toHours() < 24     -> "${diff.toHours()} jam lalu"
                diff.toDays() < 7       -> "${diff.toDays()} hari lalu"
                diff.toDays() < 30      -> "${diff.toDays() / 7} minggu lalu"
                else -> {
                    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id"))
                    time.format(formatter)
                }
            }
        } catch (_: Exception) {
            isoString.take(10)
        }
    }
}