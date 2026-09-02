package com.example.util

import android.content.Context
import android.content.SharedPreferences
import android.util.LruCache

/**
 * AppCache provides in-memory and persistent disk caching for:
 * - Saved user account credentials/email (enables quick seamless login)
 * - Last sync timestamp
 * - Frequently used calculations and in-memory cache
 */
object AppCache {
    private const val PREFS_NAME = "kanzia_app_cache"
    private const val KEY_SAVED_EMAIL = "cached_saved_email"
    private const val KEY_LAST_SYNC_TIME = "cached_last_sync_timestamp"

    private var prefs: SharedPreferences? = null

    // In-memory LRU Cache for calculations / fast memory hits
    private val memoryCache = LruCache<String, Any>(50)

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    var savedEmail: String?
        get() = prefs?.getString(KEY_SAVED_EMAIL, null)
        set(value) {
            prefs?.edit()?.apply {
                if (value.isNullOrBlank()) {
                    remove(KEY_SAVED_EMAIL)
                } else {
                    putString(KEY_SAVED_EMAIL, value.trim())
                }
                apply()
            }
        }

    var lastSyncTimestamp: Long
        get() = prefs?.getLong(KEY_LAST_SYNC_TIME, 0L) ?: 0L
        set(value) {
            prefs?.edit()?.putLong(KEY_LAST_SYNC_TIME, value)?.apply()
        }

    fun putInMemory(key: String, value: Any) {
        memoryCache.put(key, value)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getFromMemory(key: String): T? {
        return memoryCache.get(key) as? T
    }

    fun clearMemoryCache() {
        memoryCache.evictAll()
    }
}
