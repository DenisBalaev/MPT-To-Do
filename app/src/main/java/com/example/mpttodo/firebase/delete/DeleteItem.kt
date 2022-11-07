package com.example.mpttodo.firebase.delete

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DeleteItem {
    var ref = FirebaseDatabase.getInstance().reference
    fun deleteItemTitle(idTitle:String, uidUser:String){
        ref.child(uidUser).child("Title").child(idTitle).removeValue();
    }

    fun deleteItemTask(uidUser:String,idTitle:String,idTask:String){
        ref.child(uidUser).child("Title").child(idTitle)
            .child("Task").child(idTask).removeValue();
    }
}