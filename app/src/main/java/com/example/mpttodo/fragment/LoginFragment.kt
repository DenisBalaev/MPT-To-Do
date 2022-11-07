package com.example.mpttodo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mpttodo.R
import com.example.mpttodo.firebase.aut.RegistrationAuthorization
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*


@Suppress("DEPRECATION")
class LoginFragment : Fragment() {

    var mAuth: FirebaseAuth?=null
    lateinit var regAut: RegistrationAuthorization

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        mAuth = FirebaseAuth.getInstance()
        regAut = context?.let {
            RegistrationAuthorization(mAuth, it, requireFragmentManager())
        }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAuthorization.setOnClickListener {
            regAut.signIn(edtLogin.text.toString(),edtPassword.text.toString())
        }
        btnRegistration.setOnClickListener {
            regAut.signUp(edtLogin.text.toString(),edtPassword.text.toString())
        }
    }
}