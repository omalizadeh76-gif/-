package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.data.local.AppDatabase
import com.example.data.model.AlarmSoundType
import com.example.data.model.ExpiryStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NotificationHelper {

    const val CHANNEL_ID_PREFIX = "med_expiry_alert_channel_"
    const val CHANNEL_NAME = "هشدارهای انقضای دارو و تجهیزات"
    const val CHANNEL_DESC = "اعلان‌های یادآوری نزدیک شدن به تاریخ انقضا (۱ ماه قبل)"

    private var currentPreviewRingtone: Ringtone? = null

    fun getSoundUri(context: Context, soundType: AlarmSoundType): Uri {
        return when (soundType) {
            AlarmSoundType.ALARM -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            AlarmSoundType.NOTIFICATION -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            AlarmSoundType.RINGTONE -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
    }

    fun getSavedSoundType(context: Context): AlarmSoundType {
        val prefs = context.getSharedPreferences("medical_app_prefs", Context.MODE_PRIVATE)
        val name = prefs.getString("alarm_sound_type", AlarmSoundType.ALARM.name)
        return try {
            AlarmSoundType.valueOf(name ?: AlarmSoundType.ALARM.name)
        } catch (e: Exception) {
            AlarmSoundType.ALARM
        }
    }

    fun setSavedSoundType(context: Context, soundType: AlarmSoundType) {
        val prefs = context.getSharedPreferences("medical_app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("alarm_sound_type", soundType.name).apply()
        // Recreate channel with new sound
        createNotificationChannel(context, soundType)
    }

    fun createNotificationChannel(context: Context, soundType: AlarmSoundType = getSavedSoundType(context)) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = CHANNEL_ID_PREFIX + soundType.name.lowercase()
            val importance = NotificationManager.IMPORTANCE_HIGH
            val soundUri = getSoundUri(context, soundType)

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(
                    if (soundType == AlarmSoundType.ALARM) AudioAttributes.USAGE_ALARM
                    else AudioAttributes.USAGE_NOTIFICATION
                )
                .build()

            val channel = NotificationChannel(channelId, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
                setSound(soundUri, audioAttributes)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun playPreviewSound(context: Context, soundType: AlarmSoundType) {
        stopPreviewSound()
        try {
            val uri = getSoundUri(context, soundType)
            currentPreviewRingtone = RingtoneManager.getRingtone(context, uri)?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    isLooping = false
                }
                play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopPreviewSound() {
        try {
            currentPreviewRingtone?.let {
                if (it.isPlaying) {
                    it.stop()
                }
            }
            currentPreviewRingtone = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun checkAndNotifyExpiringMedicines(context: Context): Int = withContext(Dispatchers.IO) {
        val soundType = getSavedSoundType(context)
        createNotificationChannel(context, soundType)

        val database = AppDatabase.getInstance(context)
        val items = database.medicalItemDao().getAllItemsList()
        val now = System.currentTimeMillis()

        val expiringSoonItems = items.filter { item ->
            item.hasExpiryDate && item.getExpiryStatus(now) == ExpiryStatus.EXPIRING_SOON
        }

        val expiredItems = items.filter { item ->
            item.hasExpiryDate && item.getExpiryStatus(now) == ExpiryStatus.EXPIRED
        }

        var notificationsCount = 0

        // 1. If we have expiring soon items (close to 1 month / 30 days)
        if (expiringSoonItems.isNotEmpty()) {
            val title = "⚠️ هشدار: ${expiringSoonItems.size} دارو/تجهیزات در آستانه انقضا"
            val itemNames = expiringSoonItems.take(3).joinToString("، ") { item ->
                val days = item.getRemainingDays(now).coerceAtLeast(0)
                "${item.name} ($days روز مانده)"
            }
            val moreText = if (expiringSoonItems.size > 3) " و ${expiringSoonItems.size - 3} مورد دیگر..." else ""
            val body = "داروهای زیر تا حدود ۱ ماه دیگر منقضی می‌شوند:\n$itemNames$moreText\nلطفاً در صورت نیاز جایگزین نمایید."

            sendNotification(
                context = context,
                notificationId = 1001,
                title = title,
                body = body,
                isWarning = true,
                soundType = soundType
            )
            notificationsCount += expiringSoonItems.size
        }

        // 2. If we have expired items
        if (expiredItems.isNotEmpty()) {
            val title = "🚨 اخطار: ${expiredItems.size} قلم داروی منقضی شده!"
            val itemNames = expiredItems.take(3).joinToString("، ") { it.name }
            val moreText = if (expiredItems.size > 3) " و ${expiredItems.size - 3} مورد دیگر..." else ""
            val body = "داروهای منقضی شده: $itemNames$moreText\nاز مصرف داروهای منقضی اکیداً خودداری کرده و آن‌ها را امحا کنید."

            sendNotification(
                context = context,
                notificationId = 1002,
                title = title,
                body = body,
                isWarning = false,
                soundType = soundType
            )
            notificationsCount += expiredItems.size
        }

        notificationsCount
    }

    fun sendTestNotification(context: Context) {
        val soundType = getSavedSoundType(context)
        createNotificationChannel(context, soundType)
        val title = "🔔 آزمایش سیستم اعلان انقضای دارو"
        val body = "سیستم یادآوری فعال است. داروها و تجهیزاتی که تاریخ انقضای آن‌ها به کمتر از ۱ ماه برسد، خودکار اطلاع داده می‌شوند."

        sendNotification(
            context = context,
            notificationId = 9999,
            title = title,
            body = body,
            isWarning = true,
            soundType = soundType
        )
    }

    fun sendItemSpecificNotification(context: Context, itemName: String, daysLeft: Long) {
        val soundType = getSavedSoundType(context)
        createNotificationChannel(context, soundType)
        val title = "⏰ یادآور انقضای دارو: $itemName"
        val body = if (daysLeft > 0) {
            "تنها $daysLeft روز تا انقضای این دارو باقی مانده است (کمتر از ۱ ماه)."
        } else {
            "تاریخ انقضای این دارو به پایان رسیده است!"
        }

        sendNotification(
            context = context,
            notificationId = itemName.hashCode(),
            title = title,
            body = body,
            isWarning = daysLeft > 0,
            soundType = soundType
        )
    }

    fun sendAdminBroadcastNotification(
        context: Context,
        title: String,
        body: String,
        priority: String = "HIGH"
    ) {
        val soundType = getSavedSoundType(context)
        createNotificationChannel(context, soundType)
        val fullTitle = "📢 پیام مدیریت: $title"
        val notifId = ((System.currentTimeMillis() / 1000) % 100000).toInt() + 2000

        sendNotification(
            context = context,
            notificationId = notifId,
            title = fullTitle,
            body = body,
            isWarning = priority != "CRITICAL",
            soundType = soundType
        )
    }

    private fun sendNotification(
        context: Context,
        notificationId: Int,
        title: String,
        body: String,
        isWarning: Boolean,
        soundType: AlarmSoundType
    ) {
        val channelId = CHANNEL_ID_PREFIX + soundType.name.lowercase()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val soundUri = getSoundUri(context, soundType)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setColor(if (isWarning) 0xFFEA580C.toInt() else 0xFFE11D48.toInt())

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS permission not granted
        }
    }
}
