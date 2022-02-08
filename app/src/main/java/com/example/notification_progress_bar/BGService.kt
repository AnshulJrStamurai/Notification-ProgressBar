package com.example.notification_progress_bar

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


class BGService : Service() {

    companion object {
        const val NOTIF_ID = 1001
        const val NOTIF_DISMISS_ID = 1002

    }

    val handler by lazy {
        Handler(mainLooper)
    }
    lateinit var handleRunnable: Runnable
    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)


        if (intent != null) {
            if (intent.action.toString() == NOTIF_DISMISS_ID.toString()) {
                handler.removeCallbacks(handleRunnable)
                stopForeground(true)
                stopSelf()
            } else {
                startForeground(NOTIF_ID, showNotification())
            }
        }
        return START_NOT_STICKY
    }

    private fun showNotification(): Notification {
        val channelID = "notif_progress"
        val label = "Progress"

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            NotificationChannel(channelID, label, importance).apply {
                notificationManager.createNotificationChannel(this)
            }
        }

        val priority = Notification.PRIORITY_DEFAULT
        val icon = R.drawable.ic_launcher_background
        val title = "Notification Title"

        val stopNotificationIntent = Intent(this, BGService::class.java)
        stopNotificationIntent.action = NOTIF_DISMISS_ID.toString()
        val dismissIntent = PendingIntent.getService(
            this,
            NOTIF_ID,
            stopNotificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val PROGRESS_MAX = 10 //secs
        var PROGRESS = 0

        val builder = NotificationCompat.Builder(this)
            .setSubText(title)
            .setSmallIcon(icon)
            .setContentTitle("Time Left: $PROGRESS_MAX")
            .setLargeIcon(getBitmapFromVectorDrawable(applicationContext, icon))
            .setPriority(priority)
            .setOngoing(true)
            .setAutoCancel(false)
            .setChannelId(channelID)
            .addAction(android.R.drawable.ic_delete, "Stop", dismissIntent);

        NotificationManagerCompat.from(this).apply {
            handleRunnable = object : Runnable {
                override fun run() {
                    if (PROGRESS == PROGRESS_MAX) {
                        // timer completes
                        builder.setContentTitle("Deal is over")
                        builder.setProgress(0, 0, false)

                        stopSelf()

                        handler.removeCallbacks(this)
                        return
                    }

                    builder.setProgress(PROGRESS_MAX, PROGRESS, false)
                    notify(NOTIF_ID, builder.build())
                    PROGRESS += 1
                    builder.setContentTitle("Time left: " + (PROGRESS_MAX - PROGRESS).toString())

                    builder.setSilent(true)
                    handler.postDelayed(this, 1000) //1 sec delay
                }

            }

            handler.post(handleRunnable)

        }


        return builder.build()

    }

    fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context!!, drawableId)
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}