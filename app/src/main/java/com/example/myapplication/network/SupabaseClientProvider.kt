package com.example.myapplication.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.okhttp.OkHttp

object SupabaseClientProvider {

    private const val SUPABASE_URL = "https://zkdmtcgnsksehpvphomd.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InprZG10Y2duc2tzZWhwdnBob21kIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODAxMzk2NjIsImV4cCI6MjA5NTcxNTY2Mn0.gEYSSMLPZyWsnlsDLchV5qaw8Is5OyZAcVFfwhI5qmU"

    val client by lazy {
        createSupabaseClient(SUPABASE_URL, SUPABASE_KEY) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
            install(Storage)
            httpEngine = OkHttp.create()
        }
    }
}