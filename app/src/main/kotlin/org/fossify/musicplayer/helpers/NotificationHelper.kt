package org.fossify.musicplayer.helpers

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import org.fossify.commons.extensions.notificationManager
import org.fossify.musicplayer.R
import org.fossify.musicplayer.activities.MainActivity

/** Helper class to manage all-things-notification. */
@SuppressLint("NewApi")
class NotificationHelper(private val context: Context) {

    private var notificationManager = context.notificationManager

    fun createNoPermissionNotification(): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setContentTitle(context.getString(org.fossify.commons.R.string.no_storage_permissions))
            .setSmallIcon(R.drawable.ic_music_note_vector)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(getContentIntent())
            .setChannelId(NOTIFICATION_CHANNEL)
            .setCategory(Notification.CATEGORY_PROGRESS)
            .build()
    }

    fun createMediaScannerNotification(contentText: String, progress: Int, max: Int): Notification {
        val title = context.getString(org.fossify.commons.R.string.scanning)
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_music_note_vector)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(getContentIntent())
            .setChannelId(NOTIFICATION_CHANNEL)
            .setCategory(Notification.CATEGORY_PROGRESS)
            .setOngoing(true)
            .setProgress(max, progress, progress == 0)
            .apply {
                if (contentText.isNotEmpty()) {
                    setContentText(contentText)
                }
            }.build()
    }

    fun createForegroundNotification(): Notification {
        createBluetoothMonitorChannel()
        return NotificationCompat.Builder(context, "music_player_monitor_bluetooth_channel")
            .setContentTitle(context.getString(R.string.notify_bt_conn_mon_title))
            .setContentText(context.getString(R.string.notify_bt_conn_mon_text))
            .setSmallIcon(R.drawable.ic_music_note_vector)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(getContentIntent())
            .build()
    }

    fun createBluetoothConnectionNotification(deviceName: String): Notification {
        val launchIntent = Intent(context, MainActivity::class.java)
        launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(context, "music_player_bluetooth_channel")
            .setSmallIcon(R.drawable.ic_music_note_vector)
            .setContentTitle(context.getString(R.string.tap_to_launch) + " " + context.getString(R.string.app_launcher_name))
            .setContentText("${deviceName} ${context.getString(R.string.connected)}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    fun notify(id: Int, notification: Notification) = notificationManager.notify(id, notification)

    fun cancel(id: Int) = notificationManager.cancel(id)

    private fun getContentIntent(): PendingIntent {
        val contentIntent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, contentIntent, FLAG_IMMUTABLE)
    }

    fun createBluetoothChannel () {
        val channel = NotificationChannel(
            "music_player_bluetooth_channel",
            "Bluetooth Events",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun createBluetoothMonitorChannel () {
        val channel = NotificationChannel(
            "music_player_monitor_bluetooth_channel",
            "Monitor Bluetooth",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val NOTIFICATION_CHANNEL = "music_player_channel"
        const val NOTIFICATION_ID = 42

        private fun createNotificationChannel(context: Context, notificationManager: NotificationManager) {
            var notificationChannel: NotificationChannel? = notificationManager
                .getNotificationChannel(NOTIFICATION_CHANNEL)
            if (notificationChannel == null) {
                notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationChannel.enableLights(false)
                notificationChannel.enableVibration(false)
                notificationChannel.setShowBadge(false)

                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        fun createInstance(context: Context): NotificationHelper {
            createNotificationChannel(context, context.notificationManager)
            return NotificationHelper(context)
        }
    }
}
