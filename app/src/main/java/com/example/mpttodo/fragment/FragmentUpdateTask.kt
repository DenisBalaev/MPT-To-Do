package com.example.mpttodo.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.mpttodo.Constant
import com.example.mpttodo.CustomerDateTime
import com.example.mpttodo.R
import com.example.mpttodo.SharedPreference
import com.example.mpttodo.alarm.AlarmManagerCustomer
import com.example.mpttodo.firebase.update.Update
import com.example.mpttodo.navigation.Navigation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_update_task.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class FragmentUpdateTask : Fragment() {

    var idTask:String ?= null
    var message:String ?= null
    var dateB:String ?= null
    var timeB:String ?= null
    var alarmNumber:Int ?= null
    var idListNow:String ?= null

    var toolbar:Toolbar? = null
    var navigation:Navigation ?= null
    var constant:Constant ?= null
    var inputTextTask:String ?= null

    var edtUpdateTaskText:EditText ?= null
    var saveBtn:ImageButton ?= null

    private var timeCurrent = Calendar.getInstance()
    private var dateCurrent = Calendar.getInstance()

    var mAut = FirebaseAuth.getInstance()
    var shered:SharedPreference ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        navigation = Navigation()
        constant = Constant()
        shered = SharedPreference(requireContext())
    }

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val bundle = this.arguments
         idTask = bundle!!.getString("idTask").toString()
         message = bundle.getString("message").toString()
         dateB = bundle.getString("date").toString()
         timeB = bundle.getString("time").toString()
         alarmNumber = bundle.getString("AlarmID").toString().toInt()
         idListNow = bundle.getString("idListNow")
        val view = inflater.inflate(R.layout.fragment_update_task, container, false)

         saveBtn = view.findViewById(R.id.save_btn)
         edtUpdateTaskText = view.findViewById(R.id.edtUpdateTaskText)
         toolbarInit(view)

         return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDate.text = dateB
        tvTime.text = timeB
        edtUpdateTaskText!!.setText(message)
        inputTextTask = edtUpdateTaskText!!.text.toString()

        edtUpdateTaskText!!.addTextChangedListener(textWatcherInput)

        tvDate.setOnClickListener {
            setDate()
        }

        tvTime.setOnClickListener {
            setTime()
        }

        tvDateDelete.setOnClickListener {
            tvDate.text ="Задать дату"
        }

        tvTimeDelete.setOnClickListener {
            tvTime.text ="Задать дату"
        }
    }


    val textWatcherInput =  object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            inputTextTask = s.toString()
            saveBtn!!.isEnabled = inputTextTask!!.trim(' ').isNotEmpty()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    fun toolbarInit(view: View){

        toolbar = view.findViewById(R.id.toolBarUpdate)
        toolbar!!.title = ""
        val support = activity as AppCompatActivity
        support.setSupportActionBar(toolbar!!)

        support.supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        support.supportActionBar!!.setHomeButtonEnabled(true);
        support.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back)

        toolbar!!.setNavigationOnClickListener {
            backListTaskFragment()
        }

        saveBtn!!.setOnClickListener {
            updateTask()
            navigation!!.makeCurrentFragment(constant!!.taskListFragment,requireFragmentManager())
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun updateTask(){
        val update = Update(mAut.uid.toString())

        var statusUpdate = false

        val dateC = if (tvDate!!.text == "Задать дату")
            tvDate!!.text.toString()
        else
            SimpleDateFormat("yyyy-MM-dd").format(dateCurrent.time)

        if (dateB != tvDate.text.toString()) {
            update.updateItem("Title/${shered!!.open(constant!!.keySharedTitleId)}/Task/$idTask/date",dateC)
            statusUpdate = true
        }

        if (timeB != tvTime.text.toString()){
            update.updateItem("Title/${shered!!.open(constant!!.keySharedTitleId)}/Task/$idTask/time",tvTime.text.toString())
            statusUpdate = true
        }

        if (message != inputTextTask){
            update.updateItem("Title/${shered!!.open(constant!!.keySharedTitleId)}/Task/$idTask/textTask",inputTextTask.toString())
            statusUpdate = true
        }

        if (statusUpdate){
            AlarmManagerCustomer(requireContext()).alarmCancel(alarmNumber.toString().toInt())

            val task = com.example.mpttodo.model.Task(idTask!!,inputTextTask.toString(),tvTime.text.toString(),dateC,idListNow!!,alarmNumber.toString().toInt())

            CustomerDateTime().convertDateTime(task,requireContext())
        }
    }

    fun backListTaskFragment(){
        navigation!!.makeCurrentFragment(constant!!.fragmentUpdateTask,requireFragmentManager())
    }

    fun setDate() {
        val datePicker = DatePickerDialog(requireContext(), date,
            dateCurrent[Calendar.YEAR],
            dateCurrent[Calendar.MONTH],
            dateCurrent[Calendar.DAY_OF_MONTH]
        )

        datePicker.datePicker.minDate = Calendar.getInstance().timeInMillis;
        datePicker.show()
    }

    fun setTime(){
        val timePicker = TimePickerDialog(requireContext(),
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

    var time= TimePickerDialog.OnTimeSetListener{ _, hourOfDay, minute ->
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