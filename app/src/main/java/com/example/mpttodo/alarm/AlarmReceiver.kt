package com.example.mpttodo.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.telephony.AvailableNetworkInfo
import androidx.core.app.NotificationCompat
import com.example.mpttodo.Constant
import com.example.mpttodo.R
import com.example.mpttodo.email.JavaMailAPI
import com.example.mpttodo.firebase.update.Update
import com.example.mpttodo.fragment.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
class AlarmReceiver:BroadcastReceiver() {

    companion object {
        private var notificationManager:NotificationManager ?= null
        private var NOTIFY_ID:Int ?= null
        var CHANNEL_ID = "CHANNEL_ID"
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {

        val text = intent!!.getStringExtra(Constant().AlarmTextTask)
        val alarmId = intent.getIntExtra(Constant().AlarmID, 3)

        NOTIFY_ID = alarmId

        CoroutineScope(Dispatchers.IO).launch {
            sendMail(text!!)
        }
        CoroutineScope(Dispatchers.IO).launch {
            notification(text!!, context)
        }
    }

    fun sendMail(text: String){
        val javaMailAPI = JavaMailAPI(
            FirebaseAuth.getInstance().currentUser!!.email!!,
            "Оповещение о преблежающимся сроке выполнения задачи",
            "Подходит срок сдачи здачи \n $text"
        )

        javaMailAPI.execute()
    }

    fun notification(text: String, context: Context?){
        notificationManager = context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context,HomeFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivities(context,0, arrayOf(intent),
            PendingIntent.FLAG_UPDATE_CURRENT)


        val notificationBundle = NotificationCompat.Builder(context,CHANNEL_ID)
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.ic_library_add_check)
            .setContentIntent(pendingIntent)
            .setContentTitle("Напоминание")
            .setContentText(text)
            .setPriority(AvailableNetworkInfo.PRIORITY_HIGH)
            .setWhen(System.currentTimeMillis())


        chenel(notificationManager!!)
        NOTIFY_ID?.let { notificationManager!!.notify(it,notificationBundle.build()) }
    }

    fun updating(idList:String,idTask:String,numberAlarm:Int){
        Update(FirebaseAuth.getInstance().uid.toString()).updateItemInt("Title/$idList/$idTask/numberAlarm",numberAlarm)
    }

    private fun chenel(manager: NotificationManager) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(CHANNEL_ID,CHANNEL_ID,NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(notificationChannel)
        }
    }
}
