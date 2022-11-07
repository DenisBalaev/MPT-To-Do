package com.example.mpttodo.navigation

import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.mpttodo.R

class Navigation:AppCompatDialogFragment() {
    fun makeCurrentFragment(fragment: Fragment,fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_wrapper, fragment)
            commit()
        }
    }
}