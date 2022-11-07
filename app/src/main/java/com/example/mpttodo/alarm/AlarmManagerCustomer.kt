package com.example.mpttodo.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.mpttodo.Constant
import com.google.android.material.internal.ContextUtils.getActivity
import java.util.*


class AlarmManagerCustomer(private val context: Context) {

    private var calendar = Calendar.getInstance()
    var constant = Constant()

    @SuppressLint("RestrictedApi")
    fun setAlarm(time: Calendar, date: Calendar, statusTime:Boolean, statusDate:Boolean,textTask:String,numberAlarm:Int) {

        if (statusTime || statusDate) {
            val alarmManager = getActivity(context)!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (statusTime) {
                calendar.set(Calendar.HOUR_OF_DAY, time[Calendar.HOUR_OF_DAY])
                calendar.set(Calendar.MINUTE, time[Calendar.MINUTE])
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }

            if (statusDate) {
                calendar.set(Calendar.YEAR, date[Calendar.YEAR])
                calendar.set(Calendar.MONDAY, date[Calendar.MONDAY])
                calendar.set(Calendar.DAY_OF_MONTH, date[Calendar.DAY_OF_MONTH])
            }

            if (statusDate && !statusTime) {
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }

            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra(constant.AlarmTextTask, textTask)
            intent.putExtra(constant.AlarmID, numberAlarm)

            Thread.sleep(100)

            val pendingIntent = PendingIntent.getBroadcast(context, numberAlarm, intent, 0)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun alarmCancel(numberAlarm:Int){
        val alarmManager = getActivity(context)!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(context, numberAlarm, intent, 0)
        try {
            alarmManager.cancel(pendingIntent)
        } catch (e: Exception) {
            Log.e(TAG, "AlarmManager update was not canceled. $e")
        }
    }
}