package com.example.androidnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.io.Serializable


interface NotificationManagerInterface{
    fun sendSimpleNotification(messageId: Int,icon:Int,title:String,body:String,priority:Int = NotificationCompat.PRIORITY_DEFAULT,intent: Intent? = null,autoCancel:Boolean=true)
    fun sendActionablePusNotification(messageId: Int,icon:Int,title:String,body:String,vararg actions:ActionablePushNotification,priority:Int = NotificationCompat.PRIORITY_DEFAULT,autoCancel:Boolean=true)
    fun sendExpandableImageNotification(messageId: Int,icon:Int,title:String,body:String,image:Bitmap,priority:Int = NotificationCompat.PRIORITY_DEFAULT,intent: Intent? = null,autoCancel:Boolean=true)
    fun sendExpandableNotification(messageId: Int,icon:Int,title:String,body:String,fullBody:String,priority:Int = NotificationCompat.PRIORITY_DEFAULT,intent: Intent? = null,autoCancel:Boolean=true)
    fun removeNotification(notificationID:Int)
    fun removeAllNotification()
}

class NotificationManagerInterfaceImpl(private val context: Context,private val mChannel: Channel):NotificationManagerInterface{
    init {
        createNotificationChannel(mChannel,context)
    }

    private fun createNotificationChannel(notificationChannel: Channel,context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = notificationChannel.name
            val descriptionText = notificationChannel.description
            val importance = notificationChannel.importance
            val channel = NotificationChannel(notificationChannel.id, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    override fun sendSimpleNotification(messageId: Int,
        icon: Int,
        title: String,
        body: String,
        priority: Int,intent: Intent?,autoCancel:Boolean
    ) {
        val builder = NotificationCompat.Builder(context, mChannel.id).setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(priority)
            .setAutoCancel(autoCancel)
        intent?.let {
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            builder.setContentIntent(pendingIntent)
        }
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(messageId, builder.build())
        }
    }

    override fun sendActionablePusNotification(
        messageId: Int,
        icon: Int,
        title: String,
        body: String,
        vararg actions: ActionablePushNotification,
        priority: Int,
        autoCancel: Boolean
    ) {
        require(actions.size <= 3){"You cannot have more than 3 actions"}
        val builder = NotificationCompat.Builder(context, mChannel.id).setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(priority)
            .setAutoCancel(autoCancel)
        if(!actions.isNullOrEmpty()) addActions(actions,builder,messageId)
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(messageId, builder.build())
        }
    }

    private fun addActions(
        actions: Array<out ActionablePushNotification>,
        builder: NotificationCompat.Builder,
        messageId: Int
    ) {
        actions.forEachIndexed { index, actionablePushNotification ->
            //This is the intent of PendingIntent
            val intentAction = Intent(context, ActionReceiver::class.java)
            intentAction.putExtra("action",actionablePushNotification.actionName)
            intentAction.putExtra("channel",mChannel)
            intentAction.putExtra("messageID",messageId)
            val pendingIntent = PendingIntent.getBroadcast(context,index,intentAction,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(actionablePushNotification.icon,actionablePushNotification.actionName,pendingIntent)
        }
    }


    override fun sendExpandableImageNotification(
        messageId: Int,
        icon: Int,
        title: String,
        body: String,
        image: Bitmap,
        priority: Int,
        intent: Intent?,
        autoCancel: Boolean
    ) {
        val builder = NotificationCompat.Builder(context, mChannel.id).setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(priority)
            .setAutoCancel(autoCancel)
            .setLargeIcon(image)
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(image)
                .bigLargeIcon(null))
        intent?.let {
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            builder.setContentIntent(pendingIntent)
        }
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(messageId, builder.build())
        }
    }


    override fun sendExpandableNotification(
        messageId: Int,
        icon: Int,
        title: String,
        body: String,
        fullBody: String,
        priority: Int,
        intent: Intent?,
        autoCancel: Boolean
    ) {

        val builder = NotificationCompat.Builder(context, mChannel.id).setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(priority)
            .setAutoCancel(autoCancel)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(fullBody))
        intent?.let {
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            builder.setContentIntent(pendingIntent)
        }
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(messageId, builder.build())
        }
    }

    override fun removeNotification(notificationID: Int) {
        NotificationManagerCompat.from(context).cancel(notificationID)
    }

    override fun removeAllNotification() {
        NotificationManagerCompat.from(context).cancelAll()
    }


}

data class Channel(val name:String,val id: String,val description: String,val importance:Int = NotificationManager.IMPORTANCE_DEFAULT):Serializable
data class ActionablePushNotification(val icon: Int,val actionName:String)