package com.example.autoshutdownservice

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
//import android.os.Build

class PowerDisconnectService : Service() {

    private val tag = "PowerDisconnectService"
    private val channelId  = "AutoShutdownServiceChannel"

    private val powerDisconnectReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_POWER_DISCONNECTED) {
                Log.d(tag, "Power disconnected. Shutting down in 5 seconds...")
                Handler(mainLooper).postDelayed({
                    try {
                        val shutdownIntent = Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN")
                        shutdownIntent.putExtra("android.intent.extra.KEY_CONFIRM", false)
                        shutdownIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(shutdownIntent)
                    } catch (e: Exception) {
                        Log.e(tag, "Shutdown failed: ${e.message}")
                    }
                }, 5000)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = Notification.Builder(this, channelId )
            .setContentTitle("Auto Shutdown Service")
            .setContentText("Monitoring power connection...")
            .setSmallIcon(android.R.drawable.ic_lock_power_off)
            .build()

        // ✅ This is what prevents the crash
        startForeground(1, notification)

        // Register receiver for power disconnect
        val filter = IntentFilter(Intent.ACTION_POWER_DISCONNECTED)
        registerReceiver(powerDisconnectReceiver, filter)
        Log.d(tag, "Service started and receiver registered.")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(powerDisconnectReceiver)
        Log.d(tag, "Service destroyed.")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId ,
                "Auto Shutdown Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
//        }
    }
}