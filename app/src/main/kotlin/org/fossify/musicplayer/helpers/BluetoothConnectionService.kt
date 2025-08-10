package org.fossify.musicplayer.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.fossify.musicplayer.BuildConfig
import org.fossify.musicplayer.R
import org.fossify.musicplayer.activities.MainActivity

class BluetoothConnectionService : Service() {
    companion object {
        const val APP_ACTIVE_FLAG = "APP_ACTIVE_FLAG"
    }

    private var appActiveFlag = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            //Log.d("ddd", "BluetoothConnectionService::onReceive() ${intent?.action}")
            if (intent?.action == BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, BluetoothProfile.STATE_DISCONNECTED)
                //Log.d("ddd", "state = ${state} appActiveFlag= ${appActiveFlag}")
                if (state == BluetoothProfile.STATE_CONNECTED) {
                    if (appActiveFlag == false) {
                        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        val deviceName = device?.name ?: "Unknown Device"
                        val deviceAddress = device?.address ?: "Unknown Address"
                        device?.let {
                            Log.d("ddd", "Bluetooth Device ${deviceName} (${deviceAddress}) connected.")
                            val notificationHelper = NotificationHelper.createInstance(context)
                            val notification = notificationHelper.createBluetoothConnectionNotification(deviceName)
                            notificationHelper.notify(it.address.hashCode(), notification)
                        }
                    }
                    else {
                        // Notify MainActivity that a Bluetooth speaker has connected
                        Log.d("ddd", "BluetoothConnectionService informing MainActivity that a device has connected")
                        val intent = Intent(context, MainActivity::class.java).apply {
                            action = "ACTION_BT_CONNECTED"
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        context?.startActivity(intent)
                    }
                }
                else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        val notificationHelper = NotificationHelper.createInstance(context)
                        notificationHelper.cancel(it.address.hashCode())
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        // Register the BroadcastReceiver dynamically when the service is created.
        val filter = IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)
        registerReceiver(receiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (it.hasExtra(APP_ACTIVE_FLAG)) {
                appActiveFlag = it.getBooleanExtra(APP_ACTIVE_FLAG, false)
            }
        }
        // Create and start the foreground notification.
        // This is a mandatory requirement for Foreground Services on Android 12+.
        try {
            startForeground(
                NotificationHelper.NOTIFICATION_ID,
                NotificationHelper.createInstance(this).createForegroundNotification()
            )
        } catch (ignored: Exception) {
        }

        // We return START_STICKY to indicate that the service should be
        // re-created if it is killed by the system.
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the BroadcastReceiver when the service is destroyed.
        unregisterReceiver(receiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
