package com.example.mpttodo

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.mpttodo.alarm.AlarmManagerCustomer
import com.google.android.gms.tasks.Task
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class CustomerDateTime {

    @RequiresApi(Build.VERSION_CODES.O)
    fun localTimeToDate(localTime: LocalTime): Calendar {
        val calendar = Calendar.getInstance()
        calendar.clear()
        //assuming year/month/date information is not important
        calendar[0, 0, 0, localTime.hour, localTime.minute] = localTime.second
        return calendar
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun localDateToDate(localDate: LocalDate): Calendar {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar[localDate.year, localDate.monthValue] = localDate.dayOfMonth
        return calendar
    }

    @SuppressLint("NewApi", "SimpleDateFormat")
    fun convertDateTime(task:com.example.mpttodo.model.Task,context: Context){
        var isDate = false
        var isTime = false

        var time = Calendar.getInstance()
        time.timeInMillis
        time[Calendar.HOUR_OF_DAY]
        time[Calendar.MINUTE]

        var date = Calendar.getInstance()
        date[Calendar.YEAR]
        date[Calendar.MONTH]
        date[Calendar.DAY_OF_MONTH]

        val custom = CustomerDateTime()

        if (task.time != "Задать время") {
            time = custom.localTimeToDate(LocalTime.parse(SimpleDateFormat("HH:mm").format(SimpleDateFormat("HH:mm").parse(task.time))))
            isTime = true
        }
        if (task.date != "Задать дату") {
            date = custom.localDateToDate(LocalDate.parse(task.date, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            isDate = true
        }

        AlarmManagerCustomer(context).setAlarm(time,date,isDate,isTime,task.textTask,task.numberAlarm)
    }
}