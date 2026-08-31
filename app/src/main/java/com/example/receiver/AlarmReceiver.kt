package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notification.AlarmScheduler
import com.example.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_CHECK_EXPIRY = "com.example.CHECK_EXPIRY_NOTIFICATION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Perform check and trigger notifications if medicines/equipment are expiring (< 30 days) or expired
                NotificationHelper.checkAndNotifyExpiringMedicines(context)

                // Reschedule for next day
                AlarmScheduler.scheduleDailyCheck(context)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
