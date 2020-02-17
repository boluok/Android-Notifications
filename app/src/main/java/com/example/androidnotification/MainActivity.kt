package com.example.androidnotification

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {
    private val notificationManagerInterface:NotificationManagerInterface by lazy { NotificationManagerInterfaceImpl(context = this,mChannel = Channel("Default Channel","34","Default messages")) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updateUI()
    }

    private fun updateUI() {
        defaultMessages.setOnClickListener {
            notificationManagerInterface.sendSimpleNotification(2,R.drawable.ic_launcher_background,"Hello","This is a test push notification")
        }
        expandableNotifcation.setOnClickListener {
            val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.flower)
            notificationManagerInterface.sendExpandableImageNotification(1,R.drawable.ic_launcher_background,"Expandable Notification","This is a test for an expandable push notification",largeIcon)
        }
        expandableText.setOnClickListener {
            notificationManagerInterface.sendExpandableNotification(3,R.drawable.ic_launcher_background,"Expandable Notification","This is a test for an expandable push notification","Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.")
        }
        actionablePush.setOnClickListener {
            notificationManagerInterface.sendActionablePusNotification(4,R.drawable.ic_launcher_background,"Actionable Push","Select an action", ActionablePushNotification(R.drawable.flower,"Yes"),ActionablePushNotification(R.drawable.flower,"No"),ActionablePushNotification(R.drawable.flower,"Maybe"))
        }
    }
}