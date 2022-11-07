package com.example.mpttodo

import android.provider.ContactsContract.DisplayNameSources.EMAIL
import com.example.mpttodo.fragment.HomeFragment
import com.example.mpttodo.fragment.LoginFragment
import com.example.mpttodo.fragment.TaskListFragment
import com.example.mpttodo.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Constant {
    val homeFragment = HomeFragment()
    val loginFragment= LoginFragment()
    val taskListFragment = TaskListFragment()
    val fragmentUpdateTask = TaskListFragment()

    val keySharedTitleId = "ID_Title"

    val keySharedUIDUser = "UID_USER"
    val keySharedIdListСompleted= "Completed"

    val AlarmTextTask = "AlarmMessage"
    val AlarmID = "AlarmID"

    val EMAIL = BuildConfig.EMAILPOST
    val PASSWORD = BuildConfig.PASSWORDPOST

    val keyIdTask = "keyIdTask"

}