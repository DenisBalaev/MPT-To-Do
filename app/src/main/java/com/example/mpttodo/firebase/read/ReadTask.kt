package com.example.mpttodo.firebase.read

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mpttodo.R
import com.example.mpttodo.adapter.AdapterTask
import com.example.mpttodo.adapter.AdapterTitleList
import com.example.mpttodo.alarm.AlarmManagerCustomer
import com.example.mpttodo.firebase.delete.DeleteItem
import com.example.mpttodo.fragment.FragmentUpdateTask
import com.example.mpttodo.fragment.HomeFragment
import com.example.mpttodo.intarface.BroadcastList
import com.example.mpttodo.intarface.ListInterface
import com.example.mpttodo.model.ListCreate
import com.example.mpttodo.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class ReadTask(private val context: Context, private val mAuth: FirebaseAuth, private val view: View,val idList:String) {

    val reference = FirebaseDatabase.getInstance()
    var listTask = arrayListOf<Task>()
    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewTask)

    fun broadCastLIstInformationRead(interf: BroadcastList) {
        reference.getReference(mAuth.uid.toString()).child("Title").child(idList).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        interf.broadcastListInformation(
                            snapshot.child("nameList").value.toString(),
                            snapshot.child("color").value.toString().toInt()
                        )
                    }catch (e:Exception){
                        interf.broadcastListInformation("",0)
                    }
                }

            })
    }

    fun readingTasks(interfaceTask: BroadcastList,nameList:String,fragmentManager: FragmentManager){
        reference.getReference(mAuth.uid.toString()).child("Title").child(idList).child("Task")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    listTask.clear()
                    for (dataSnapshot in snapshot.children){
                        val textTask = dataSnapshot.child("textTask").value.toString()
                        val time = dataSnapshot.child("time").value.toString()
                        val date = dataSnapshot.child("date").value.toString()
                        val listNow = dataSnapshot.child("idListNow").value.toString()
                        val numberAlarm = dataSnapshot.child("numberAlarm").value.toString().toInt()
                        listTask.add(Task(dataSnapshot.key.toString(),textTask,time,date,listNow,numberAlarm))
                    }

                    inputDataTask(listTask,nameList,fragmentManager)
                    try {
                        interfaceTask.endReadingTask()
                    }catch (e:Exception){}
                }

            })
    }

    @SuppressLint("NewApi", "SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    fun inputDataTask(list:MutableList<Task>, nameList: String, fragmentManager: FragmentManager){
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = AdapterTask(context, list,nameList) { it ->
            val bundle = Bundle()

            val dateCustom = if (it.date != "Задать дату"){
                SimpleDateFormat("dd MMMM yyyy").format(Date.from(LocalDate.parse(it.date).atStartOfDay(ZoneId.systemDefault()).toInstant()))
            }else{
                it.date
            }

            bundle.putString("idTask",it.id)
            bundle.putString("message",it.textTask)
            bundle.putString("date",dateCustom)
            bundle.putString("time",it.time)
            bundle.putString("AlarmID",it.numberAlarm.toString())
            bundle.putString("idListNow",it.idListNow)

            val fragmentUpdateTask = FragmentUpdateTask()
            fragmentUpdateTask.arguments = bundle

            fragmentManager.beginTransaction().replace(R.id.fragment_wrapper,fragmentUpdateTask)
                .addToBackStack(null).commit()
        }

        val myHelper = ItemTouchHelper(myCallback)
        myHelper.attachToRecyclerView(recyclerView)
    }

    val myCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val index = viewHolder.adapterPosition
            mAuth.uid?.let { DeleteItem().deleteItemTask(it,idList,listTask[index].id) }
            AlarmManagerCustomer(context).alarmCancel(listTask[index].numberAlarm)
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(context, R.color.red))
                .addSwipeLeftActionIcon(R.drawable.ic_delete)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
}