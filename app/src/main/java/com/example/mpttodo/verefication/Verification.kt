package com.example.mpttodo.verefication

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException


@Suppress("DEPRECATION")
class Verification {
    //Метод для проверки подключения к интернету
    fun hasConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifiInfo != null && wifiInfo.isConnected) {
            return true
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifiInfo != null && wifiInfo.isConnected) {
            return true
        }

        wifiInfo = cm.activeNetworkInfo
        return wifiInfo != null && wifiInfo.isConnected
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
               when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    fun verificationEditText(login:String,password:String):Boolean{
        if (login.isNotEmpty() && password.isNotEmpty())
            return true
        return false
    }

    fun checkingSpaces(text:String):String{
        var str = text
        if (text.first() != ' '){
            return text
        }

        text.forEach { c ->
            if (c != ' ')
                return str
            else
                str = str.trim(' ')
        }
        return str
    }

    fun newGenerationListName(name:String,list: List<String>):String
    {
        var number = 1
        var newNameList:String

        while (true)
        {
            newNameList = "$name (${number})"
            val index = list.indexOf(newNameList)

            if (index < 0){
                break;
            }
            number++
        }

        return newNameList
    }
}