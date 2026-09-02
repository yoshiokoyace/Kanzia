package com.example.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.BuildConfig
import com.example.data.remote.api.SupabaseApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object SupabaseClient {
    private const val TAG = "SupabaseClient"

    @Volatile
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val supabaseUrl: String
        get() = BuildConfig.SUPABASE_URL.trim()

    val supabaseKey: String
        get() = BuildConfig.SUPABASE_ANON_KEY.trim()

    val isConfigured: Boolean
        get() = supabaseUrl.isNotBlank() &&
                !supabaseUrl.contains("your-project") &&
                supabaseKey.isNotBlank()

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("apikey", supabaseKey)
            .header("Authorization", "Bearer $supabaseKey")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
        chain.proceed(requestBuilder.build())
    }

    private val cacheInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)
        // Enable seamless caching for GET queries (stocks, transactions, etc.)
        if (originalRequest.method.equals("GET", ignoreCase = true)) {
            response.newBuilder()
                .header("Cache-Control", "public, max-age=60")
                .removeHeader("Pragma")
                .build()
        } else {
            response
        }
    }

    private val offlineCacheInterceptor = Interceptor { chain ->
        var request = chain.request()
        val context = appContext
        if (context != null && request.method.equals("GET", ignoreCase = true) && !isNetworkAvailable(context)) {
            // Serve stale cache up to 7 days if offline
            request = request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=604800")
                .build()
        }
        chain.proceed(request)
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val network = connectivityManager?.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (_: Exception) {
            false
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor { message ->
            Log.d(TAG, message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val builder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(offlineCacheInterceptor)
            .addNetworkInterceptor(cacheInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)

        appContext?.let { ctx ->
            try {
                val cacheDir = File(ctx.cacheDir, "http_cache")
                val cache = Cache(cacheDir, 20L * 1024 * 1024) // 20 MB seamless cache
                builder.cache(cache)
                Log.d(TAG, "OkHttp disk cache initialized at: ${cacheDir.absolutePath}")
            } catch (e: Exception) {
                Log.w(TAG, "Could not initialize OkHttp disk cache: ${e.message}")
            }
        }

        builder.build()
    }

    val api: SupabaseApi? by lazy {
        if (!isConfigured) {
            Log.w(TAG, "Supabase credentials are not yet configured.")
            null
        } else {
            try {
                val formattedUrl = if (supabaseUrl.endsWith("/")) supabaseUrl else "$supabaseUrl/"
                Retrofit.Builder()
                    .baseUrl(formattedUrl)
                    .client(okHttpClient)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
                    .create(SupabaseApi::class.java)
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing Supabase API client: ${e.message}", e)
                null
            }
        }
    }
}
