package com.example.aukcje20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView

class NotificationShow : AppCompatActivity() {
    private lateinit var tvInformation: TextView
    private lateinit var notificationGoBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_show)

        val text = intent.getStringExtra("Text")

        tvInformation = findViewById(R.id.tv_notificationShow_information)
        notificationGoBack = findViewById(R.id.notificationShow_gobackbtn)

        tvInformation.text = text

        notificationGoBack.setOnClickListener {
            this.finish()
        }

    }
}