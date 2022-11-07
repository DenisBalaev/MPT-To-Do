package com.example.mpttodo.intarface

interface Writing {
    fun writeTitle(namTitle:String,color:Int)
    fun writeTask(idList:String,textTask: String,time:String,date:String,idListNow:String,numberAlarm:Int)
    fun writeTaskReturn(idList:String,textTask: String,time:String,date:String,idListNow:String,numberAlarm:Int):String
}