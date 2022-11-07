package com.example.mpttodo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ListCreate(
    val id: String,
    val nameList: String,
    val color:Int
) : Parcelable