package com.example.mpttodo.firebase.write

import com.example.mpttodo.intarface.Writing
import com.example.mpttodo.model.ListCreate
import com.example.mpttodo.model.Task
import com.google.firebase.database.FirebaseDatabase

class Recording(private val myDataBase: FirebaseDatabase,private var uid:String):Writing {

    override fun writeTitle(namTitle: String,color:Int) {
        val mRef = myDataBase.getReference(uid).child("Title")
        val titleCreate = ListCreate(mRef.push().key!!, namTitle, color)
        mRef.push().setValue(titleCreate)
    }

    override fun writeTask(idList:String,textTask: String,time:String,date:String,idListNow:String,numberAlarm:Int) {
        val mRef = myDataBase.getReference(uid).child("Title").child(idList).child("Task")
        val task = Task(mRef.push().key!!, textTask,time,date,idListNow,numberAlarm)
        mRef.push().setValue(task)
    }

    override fun writeTaskReturn(idList:String,textTask: String,time:String,date:String,idListNow:String,numberAlarm:Int):String {
        val mRef = myDataBase.getReference(uid).child("Title").child(idList).child("Task")
        val id = mRef.push().key!!
        val task = Task(id, textTask,time,date,idListNow,numberAlarm)
        mRef.push().setValue(task)
        return id
    }

}