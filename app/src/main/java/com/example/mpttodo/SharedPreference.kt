package com.example.mpttodo

import android.annotation.SuppressLint
import android.content.Context

class SharedPreference(context: Context) {

    private val PREFS_NAME = "MPT_To_Do"
    val shared=context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(key:String,str:String){
        shared.edit().putString(key,str).apply()
    }

    fun open(key:String):String{
        return shared.getString(key,"").toString()
    }

    fun checkingAvailability(key: String):Boolean{
        return shared.contains(key)
    }
}