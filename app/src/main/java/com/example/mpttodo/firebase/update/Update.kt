package com.example.mpttodo.firebase.update

import com.google.firebase.database.FirebaseDatabase

class Update(uid:String) {

    var mAut = uid

    fun updateItem(path:String,newValue:String){
        val mRef = FirebaseDatabase.getInstance().getReference("${mAut}/")
        mRef.child(path).setValue(newValue)
    }

    fun updateItemInt(path:String,newValue:Int){
        val mRef = FirebaseDatabase.getInstance().getReference("${mAut}/")
        mRef.child(path).setValue(newValue)
    }
}