package com.example.mpttodo.alertDialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.mpttodo.Constant
import com.example.mpttodo.R
import com.example.mpttodo.SharedPreference
import com.example.mpttodo.alarm.AlarmManagerCustomer
import com.example.mpttodo.firebase.write.Recording
import com.example.mpttodo.verefication.Verification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class AlertDialogTask (private var context: Context) {

    var buttonPositive: Button?= null

    private var timeCurrent = Calendar.getInstance()
    private var dateCurrent = Calendar.getInstance()

    var tvDate:TextView ?= null
    var tvDateDelete:TextView ?= null
    var tvTime:TextView ?= null
    var tvTimeDelete:TextView ?= null
    var inputEditText:EditText ?= null
    var inputTextTask:String ?= null

    var statusTime = false
    var statusDate = false

    var mAuth = FirebaseAuth.getInstance()

    @SuppressLint("SimpleDateFormat")
    fun createTask (idList:String){
        val view = LayoutInflater.from(context).inflate(R.layout.alert_create_task,null)
        tvDate = view.findViewById<TextView>(R.id.selectedDate) as TextView
        tvDateDelete = view.findViewById<TextView>(R.id.selectedDateDelete) as TextView
        tvTime = view.findViewById<TextView>(R.id.selectedTime) as TextView
        tvTimeDelete = view.findViewById<TextView>(R.id.selectedTimeDelete) as TextView
        inputEditText = view.findViewById(R.id.textTaskInput) as EditText

        tvDate!!.setOnClickListener {
          setDate()
        }

        tvTime!!.setOnClickListener {
            setTime()
        }

        tvDateDelete!!.setOnClickListener {
            tvDate!!.text = "Задать дату"
        }

        tvTimeDelete!!.setOnClickListener {
            tvTime!!.text = "Задать время"
        }

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Создание задачи")
        builder.setView(view)
        builder.setPositiveButton("Создать"){ _, _ ->

            val currentDateAndTime = Calendar.getInstance();
            val numberAlarm = currentDateAndTime[Calendar.HOUR_OF_DAY]+
                    currentDateAndTime[Calendar.MINUTE] +
                    currentDateAndTime[Calendar.SECOND] +
                    currentDateAndTime[Calendar.MILLISECOND] +
                    currentDateAndTime[Calendar.YEAR] +
                    currentDateAndTime[Calendar.MONDAY] +
                    currentDateAndTime[Calendar.DAY_OF_MONTH]

            if (tvDate!!.text != "Задать дату"){
                statusDate = true
            }

            if (tvTime!!.text != "Задать время"){
                statusTime = true
            }

            val idTask = Recording(FirebaseDatabase.getInstance(), mAuth.uid.toString())
                .writeTaskReturn(
                    idList,
                    Verification().checkingSpaces(inputTextTask!!),
                    tvTime!!.text.toString(),
                    if (tvDate!!.text == "Задать дату")
                        tvDate!!.text.toString()
                    else
                        SimpleDateFormat("yyyy-MM-dd").format(dateCurrent.time)
                    ,
                    idList,
                    numberAlarm
                )

            AlarmManagerCustomer(context)
                .setAlarm(timeCurrent,dateCurrent,statusTime,statusDate,inputTextTask!!,numberAlarm)

            SharedPreference(context).save(idTask,Constant().keyIdTask)

        }.setNegativeButton("Отмена"){ _, _ -> }

        val dialog = builder.create()
        dialog!!.show()

        buttonPositive = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
        buttonPositive!!.isEnabled = false

        inputEditText!!.addTextChangedListener(textWatcherInputTextTask)
    }

    val textWatcherInputTextTask =  object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            inputTextTask = s.toString()
            buttonPositive!!.isEnabled = inputTextTask!!.trim(' ').isNotEmpty()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    fun setDate() {
        val datePicker = DatePickerDialog(context, date,
            dateCurrent[Calendar.YEAR],
            dateCurrent[Calendar.MONTH],
            dateCurrent[Calendar.DAY_OF_MONTH]
        )

        datePicker.datePicker.minDate = Calendar.getInstance().timeInMillis;
        datePicker.show()
    }

    fun setTime(){
        val timePicker = TimePickerDialog(context,
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            time,
            timeCurrent[Calendar.HOUR_OF_DAY], timeCurrent[Calendar.MINUTE],
            true)

        timePicker.show()
    }

    private fun setInitialDate() {
        tvDate!!.text = DateUtils.formatDateTime(context,
            dateCurrent.timeInMillis,
            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR)
    }

    private fun setInitialTime() {
        tvTime!!.text = DateUtils.formatDateTime(context,
            timeCurrent.timeInMillis, DateUtils.FORMAT_SHOW_TIME)
    }

    var time=TimePickerDialog.OnTimeSetListener{ _, hourOfDay, minute ->
        timeCurrent[Calendar.HOUR_OF_DAY]=hourOfDay
        timeCurrent[Calendar.MINUTE]= minute
        setInitialTime()
    }

    var date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        dateCurrent[Calendar.YEAR] = year
        dateCurrent[Calendar.MONDAY]= monthOfYear
        dateCurrent[Calendar.DAY_OF_MONTH] = dayOfMonth
        setInitialDate()
    }
}