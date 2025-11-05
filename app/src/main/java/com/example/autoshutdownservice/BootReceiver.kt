package com.example.autoshutdownservice

//package com.example.autoshutdown

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Boot completed → Starting service")
            val serviceIntent = Intent(context, PowerDisconnectService::class.java)
            context?.startForegroundService(serviceIntent)
        }
    }
}
