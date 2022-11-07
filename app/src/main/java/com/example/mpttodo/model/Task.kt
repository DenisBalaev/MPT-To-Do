package com.example.mpttodo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(
    val id: String,
    val textTask:String,
    val time:String,
    val date:String,
    val idListNow:String,
    val numberAlarm:Int
) : Parcelable