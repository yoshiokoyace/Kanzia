package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.MainAppScreen
import com.example.ui.theme.FinanceLedgerTheme
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.InactivityManager

class MainActivity : ComponentActivity() {
    private val viewModel: FinanceViewModel by viewModels()

    private val screenOffReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                InactivityManager.onScreenOff(this@MainActivity)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenOffReceiver, filter)

        setContent {
            FinanceLedgerTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        InactivityManager.recordUserInteraction()
    }

    override fun onStart() {
        super.onStart()
        if (InactivityManager.checkAndCloseIfInactive(this)) {
            return
        }
        InactivityManager.onActivityStarted(this)
    }

    override fun onStop() {
        super.onStop()
        InactivityManager.onActivityStopped(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(screenOffReceiver)
        } catch (_: Exception) {}
        InactivityManager.onActivityDestroyed(this)
    }
}


