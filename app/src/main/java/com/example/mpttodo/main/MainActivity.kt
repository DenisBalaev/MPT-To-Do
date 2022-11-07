package com.example.mpttodo.main

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.mpttodo.Constant
import com.example.mpttodo.R
import com.example.mpttodo.verefication.Verification
import com.google.android.material.internal.ContextUtils
import com.google.firebase.auth.FirebaseAuth

@Suppress("ALWAYS_NULL")
class MainActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        fragmentManagerCustomer = supportFragmentManager

        if (!Verification().isNetworkAvailable(this)) {
            Toast.makeText(this, "Проверьте подключение к интернету", Toast.LENGTH_LONG).show()
        }
    }

    fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_wrapper, fragment)
            commit()
        }
    }

    override fun onStart() {
        super.onStart()
        val constant = Constant()
        val userAuth = mAuth!!.currentUser;
        if (userAuth != null && userAuth.isEmailVerified){
            makeCurrentFragment(constant.homeFragment)
        }else{
            makeCurrentFragment(constant.loginFragment)
        }
    }

    companion object {
        var fragmentManagerCustomer: FragmentManager? = null
            private set
    }
}