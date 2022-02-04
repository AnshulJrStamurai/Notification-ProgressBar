package com.example.notification_progress_bar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var isTextViewPressed = false

        findViewById<TextView>(R.id.textView).setOnClickListener {
            isTextViewPressed = !isTextViewPressed

            if(isTextViewPressed){
                (it as TextView).text = "Pressed"
                startBGService()
            }else{
                (it as TextView).text = "Stopped"
                stopBGService()
            }
        }

    }

    private fun startBGService(){
        Intent(this, BGService::class.java).apply {
            startService(this)
        }
    }

    private fun stopBGService(){
        Intent(this, BGService::class.java).apply {
            stopService(this)
        }
    }
}