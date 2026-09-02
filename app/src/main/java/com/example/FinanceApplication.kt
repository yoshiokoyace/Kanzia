package com.example

import android.app.Application
import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.remote.SupabaseClient
import com.example.util.AppCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FinanceApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Clean-slate reset: purge old dummy accounts, old SQLite databases, and cached sessions for fresh test
        val resetPrefs = getSharedPreferences("kanzia_reset_flag", Context.MODE_PRIVATE)
        if (!resetPrefs.getBoolean("purged_fresh_test_v3", false)) {
            try {
                deleteDatabase("finance_ledger.db")
                deleteDatabase("finance_ledger.db-shm")
                deleteDatabase("finance_ledger.db-wal")
                getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().clear().commit()
                getSharedPreferences("kanzia_app_cache", Context.MODE_PRIVATE).edit().clear().commit()
                getSharedPreferences("finance_cleanup_prefs", Context.MODE_PRIVATE).edit().clear().commit()
            } catch (_: Exception) {
            }
            resetPrefs.edit().putBoolean("purged_fresh_test_v3", true).commit()
        }

        // Initialize App Cache
        AppCache.init(this)

        // Initialize Supabase Client with OkHttp disk caching
        SupabaseClient.init(this)

        // Pre-warm local Room SQLite database in background for seamless zero-delay UI rendering
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(this@FinanceApplication)
        }
    }
}
