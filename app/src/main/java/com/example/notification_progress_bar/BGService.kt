package com.example.notification_progress_bar

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class BGService: Service() {

    companion object {
        const val NOTIF_ID = 1001
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        startForeground(NOTIF_ID, showNotification())

        return START_NOT_STICKY
    }

    private fun showNotification() : Notification{
        val channelID = "notif_progress"
        val label = "Progress"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            NotificationChannel(channelID, label, importance).apply {
                notificationManager.createNotificationChannel(this)
            }
        }

        val priority = Notification.PRIORITY_DEFAULT
        val icon = R.drawable.ic_launcher_background
        val title = "Notification Title"

        val builder = NotificationCompat.Builder(this)
            .setContentTitle(title)
            .setContentText("Sub title")
            .setSmallIcon(icon)
            .setPriority(priority)
            .setOngoing(true)
            .setAutoCancel(false)
            .setChannelId(channelID)

        val PROGRESS_MAX = 10 //secs
        var PROGRESS = 0

        val handler by lazy {
            Handler(mainLooper)
        }

        NotificationManagerCompat.from(this).apply {
            val handlerRunnable = object : Runnable {
                override fun run() {
                    if(PROGRESS == PROGRESS_MAX){
                        // timer completes
                        builder.setContentText("Deal is over")
                        builder.setProgress(0,0,false)

                        stopSelf()

//                        notify(NOTIF_ID, builder.build())
                        handler.removeCallbacks(this)
                        return
                    }

                    builder.setProgress(PROGRESS_MAX, PROGRESS, false)
                    notify(NOTIF_ID, builder.build())
                    PROGRESS += 1
                    builder.setContentText("Time left: " + (PROGRESS_MAX - PROGRESS).toString())

                    builder.setSilent(true)
                    handler.postDelayed(this, 1000) //1 sec delay
                }

            }

            handler.post(handlerRunnable)

        }


        return builder.build()

    }
}