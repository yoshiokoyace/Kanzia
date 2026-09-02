package com.example.util

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.example.data.repository.AuthRepository
import java.lang.ref.WeakReference
import kotlin.system.exitProcess

/**
 * Monitors user interaction and background status.
 * Automatically closes the application after 3 minutes (180,000 ms) of inactivity
 * when running in the background (including pressing the Home button or screen off).
 */
object InactivityManager {
    private const val TAG = "InactivityManager"
    const val INACTIVITY_TIMEOUT_MS = 3 * 60 * 1000L // 3 minutes = 180,000 ms

    @Volatile
    var isBackgrounded: Boolean = false
        private set

    @Volatile
    var backgroundTimestamp: Long = 0L
        private set

    @Volatile
    var lastInteractionTimestamp: Long = SystemClock.elapsedRealtime()
        private set

    private val mainHandler = Handler(Looper.getMainLooper())
    private var currentActivity: WeakReference<Activity>? = null

    private val closeAppRunnable = object : Runnable {
        override fun run() {
            val now = SystemClock.elapsedRealtime()
            val timeInBackground = if (backgroundTimestamp > 0) now - backgroundTimestamp else 0
            val timeSinceInteraction = now - lastInteractionTimestamp

            Log.d(TAG, "Inactivity check running: isBackgrounded=$isBackgrounded, timeInBackground=$timeInBackground ms, timeSinceInteraction=$timeSinceInteraction ms")

            if (isBackgrounded && (timeInBackground >= INACTIVITY_TIMEOUT_MS || timeSinceInteraction >= INACTIVITY_TIMEOUT_MS)) {
                Log.w(TAG, "3 minutes of inactivity reached while in background. Automatically closing application.")
                closeApplication()
            }
        }
    }

    fun recordUserInteraction() {
        lastInteractionTimestamp = SystemClock.elapsedRealtime()
    }

    fun onActivityStarted(activity: Activity) {
        currentActivity = WeakReference(activity)
        if (checkAndCloseIfInactive(activity)) {
            return
        }
        isBackgrounded = false
        backgroundTimestamp = 0L
        mainHandler.removeCallbacks(closeAppRunnable)
        recordUserInteraction()
        Log.d(TAG, "App foregrounded. Inactivity timer cleared.")
    }

    fun onActivityStopped(activity: Activity) {
        isBackgrounded = true
        backgroundTimestamp = SystemClock.elapsedRealtime()
        mainHandler.removeCallbacks(closeAppRunnable)
        // Schedule auto-close after 3 minutes in background
        mainHandler.postDelayed(closeAppRunnable, INACTIVITY_TIMEOUT_MS)
        Log.d(TAG, "App sent to background (Home button pressed or app switched). 3-minute auto-close timer started.")
    }

    fun onScreenOff(activity: Activity) {
        isBackgrounded = true
        if (backgroundTimestamp == 0L) {
            backgroundTimestamp = SystemClock.elapsedRealtime()
        }
        mainHandler.removeCallbacks(closeAppRunnable)
        mainHandler.postDelayed(closeAppRunnable, INACTIVITY_TIMEOUT_MS)
        Log.d(TAG, "Screen turned off. 3-minute auto-close timer started.")
    }

    fun onActivityDestroyed(activity: Activity) {
        if (currentActivity?.get() == activity) {
            currentActivity = null
        }
    }

    /**
     * Checks if the app was inactive in the background for >= 3 minutes when user returns.
     * If so, closes the app immediately.
     */
    fun checkAndCloseIfInactive(activity: Activity): Boolean {
        val now = SystemClock.elapsedRealtime()
        val timeInBackground = if (backgroundTimestamp > 0) now - backgroundTimestamp else 0
        val timeSinceInteraction = if (lastInteractionTimestamp > 0) now - lastInteractionTimestamp else 0

        if ((backgroundTimestamp > 0 && timeInBackground >= INACTIVITY_TIMEOUT_MS) ||
            (isBackgrounded && timeSinceInteraction >= INACTIVITY_TIMEOUT_MS)
        ) {
            Log.w(TAG, "App resumed after >= 3 minutes ($timeInBackground ms in background). Automatically closing application.")
            closeApplication(activity)
            return true
        }
        return false
    }

    /**
     * Closes the application completely: finishes the activity, locks the auth session,
     * and terminates the process.
     */
    fun closeApplication(activity: Activity? = currentActivity?.get()) {
        try {
            mainHandler.removeCallbacks(closeAppRunnable)
            // Lock session so user is asked to login upon next launch
            AuthRepository.instance?.lockSession()
            activity?.finishAffinity()
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)
        } catch (e: Exception) {
            Log.e(TAG, "Error closing application: ${e.message}", e)
            activity?.finishAffinity()
        }
    }
}
