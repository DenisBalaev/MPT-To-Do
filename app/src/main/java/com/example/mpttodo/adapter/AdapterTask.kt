package com.example.mpttodo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.CountDownTimer
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.mpttodo.Constant
import com.example.mpttodo.CustomerDateTime
import com.example.mpttodo.R
import com.example.mpttodo.SharedPreference
import com.example.mpttodo.alarm.AlarmManagerCustomer
import com.example.mpttodo.firebase.delete.DeleteItem
import com.example.mpttodo.firebase.write.Recording
import com.example.mpttodo.model.Task
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.database.FirebaseDatabase
import java.security.AccessController.getContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AdapterTask(
    val context: Context,
    val taskList:List<Task>,
    val nameList:String,
    val listener:(Task)->Unit
):RecyclerView.Adapter<AdapterTask.TaskViewHolder>(){

    class TaskViewHolder(view: View): RecyclerView.ViewHolder(view){

        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)
        val tvTextTask = view.findViewById<TextView>(R.id.tvTextTaskRead)
        val tvDateTime = view.findViewById<TextView>(R.id.tvDateTime)
        val linerLayout = view.findViewById<LinearLayout>(R.id.linearLayout)
        val constant = Constant()

        var uidUser:String ?= null
        var idList :String ?= null

        var dataCalendar:LocalDate ?= null
        var timeCalendar:LocalTime ?= null

        var dateDate:String ?= null

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n", "ResourceAsColor", "SimpleDateFormat", "RestrictedApi",
            "ClickableViewAccessibility"
        )
        fun bindView(task:Task, listener: (Task) -> Unit, context: Context, nameList: String) {
            tvTextTask.text = task.textTask

            val date = task.date
            val time = task.time

            if (nameList == "Завершенные") {
                checkBox.isChecked = true
                tvTextTask.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }else {
                initializtion(date,time)
            }

            tvDateTime.setOnClickListener {
                listener(task)
            }

            tvTextTask.setOnClickListener {
                listener(task)
            }

            linerLayout.setOnClickListener {
                listener(task)
            }

            checkBox.setOnClickListener {
                clickCheckBox(nameList,context,task)
            }
        }

        @SuppressLint("NewApi", "SimpleDateFormat", "SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.N)
        fun initializtion(date:String, time:String){
            if (date != "Задать дату" || time != "Задать время") {
                var dateStatus = false

                tvDateTime.visibility = View.VISIBLE
                val dateTimeNow = Date()

                val timeNow = LocalTime.parse(SimpleDateFormat("HH:mm").format(dateTimeNow))
                val dateNow = LocalDate.parse(SimpleDateFormat("yyyy-MM-dd").format(dateTimeNow))

                tvDateTime.text = if (date != "Задать дату") {
                    dateDate = SimpleDateFormat("dd MMMM yyyy").format(Date.from(LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    dataCalendar = LocalDate.parse(date,DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                    if (dataCalendar!!.isBefore(dateNow) ){
                        tvDateTime.setTextColor(Color.RED)
                        dateStatus = true
                    }
                    "$dateDate "
                } else {
                    dateDate = ""
                    ""
                }

                tvDateTime.text = tvDateTime.text.toString() + if (time != "Задать время") {
                    timeCalendar = LocalTime.parse(SimpleDateFormat("HH:mm").format(SimpleDateFormat("HH:mm").parse(time)))

                    if (timeCalendar!!.isBefore(timeNow) && dateStatus || timeCalendar!!.isBefore(timeNow) && dateDate == ""  || (date != "Задать дату" && timeCalendar!!.isBefore(timeNow) && dataCalendar!!.isEqual(dateNow))){
                        tvDateTime.setTextColor(Color.RED)
                    }
                    time
                } else {""}
            }
        }

        @SuppressLint("SimpleDateFormat")
        @RequiresApi(Build.VERSION_CODES.O)
        fun clickCheckBox(nameList: String, context: Context, task: Task){

            val shared = SharedPreference(context)
            uidUser = shared.open(constant.keySharedUIDUser)
            idList = shared.open(constant.keySharedTitleId)
            val idListCompleted = shared.open(constant.keySharedIdListСompleted)

            val recording = Recording(FirebaseDatabase.getInstance(), uidUser!!)

            if (nameList != "Завершенные") {
                recording.writeTask(idListCompleted, task.textTask, task.time, task.date,task.idListNow,task.numberAlarm)
                itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.translate_delete_task))
                startTimer(context,task)
                AlarmManagerCustomer(context).alarmCancel(task.numberAlarm)

            }else{
                recording.writeTask(task.idListNow, task.textTask, task.time, task.date,task.idListNow,task.numberAlarm)

                CustomerDateTime().convertDateTime(task,context)

                DeleteItem().deleteItemTask(uidUser!!, idList!!, task.id)
            }
        }

        fun startTimer(context: Context,task: Task){
            object : CountDownTimer(300, 300) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    Toast.makeText(context,"Задача завершена",Toast.LENGTH_LONG).show()
                    DeleteItem().deleteItemTask(uidUser!!, idList!!, task.id)
                }
            }.start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder(LayoutInflater.from(context).inflate(R.layout.item_task,parent,false))

    override fun getItemCount(): Int = taskList.size

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bindView(taskList[position], listener,context, nameList)
    }

}
