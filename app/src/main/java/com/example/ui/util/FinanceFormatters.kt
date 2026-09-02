package com.example.ui.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object FinanceFormatters {
    private val decimalFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayDateFormat = SimpleDateFormat("MMM d", Locale.US)
    private val monthYearFormat = SimpleDateFormat("MMM yyyy", Locale.US)

    fun formatAED(amount: Double): String {
        return "AED ${decimalFormat.format(amount)}"
    }

    fun todayISO(): String {
        return isoDateFormat.format(Date())
    }

    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Good morning!"
            hour < 18 -> "Good afternoon!"
            else -> "Good evening!"
        }
    }

    fun relativeDate(isoDate: String): String {
        try {
            val date = isoDateFormat.parse(isoDate) ?: return isoDate
            val todayCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val targetCal = Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val diffMillis = todayCal.timeInMillis - targetCal.timeInMillis
            val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis)

            return when (diffDays) {
                0L -> "Today"
                1L -> "Yesterday"
                else -> displayDateFormat.format(date)
            }
        } catch (e: Exception) {
            return isoDate
        }
    }

    fun formatMonthLabel(isoMonth: String): String {
        // isoMonth is in "yyyy-MM" format
        try {
            val sdfIn = SimpleDateFormat("yyyy-MM", Locale.US)
            val sdfOut = SimpleDateFormat("MMM", Locale.US)
            val date = sdfIn.parse(isoMonth) ?: return isoMonth
            return sdfOut.format(date)
        } catch (e: Exception) {
            return isoMonth
        }
    }
}
