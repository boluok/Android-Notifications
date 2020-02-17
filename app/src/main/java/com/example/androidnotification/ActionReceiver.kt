package com.example.androidnotification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ActionReceiver :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.getStringExtra("action")
        val channel = intent.getSerializableExtra("channel") as Channel
        val messageID = intent.getIntExtra("messageID",0)
         val notificationManagerInterface = NotificationManagerInterfaceImpl(context = context!!,mChannel = channel)
        when(action){
            "Yes" ->{
                println("Yes Action Worked")
            }
            "No" ->{
                println("No Action Worked")
            }
            "Maybe" -> {
                println("Maybe Action worked")
            }
        }
        notificationManagerInterface.removeNotification(messageID)
        val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        context.sendBroadcast(it)
    }

}