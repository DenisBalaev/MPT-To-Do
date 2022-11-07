package com.example.mpttodo.firebase.aut

import android.content.Context
import android.media.MediaPlayer
import android.util.TimeFormatException
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.mpttodo.R
import com.example.mpttodo.firebase.write.Recording
import com.example.mpttodo.fragment.HomeFragment
import com.example.mpttodo.navigation.Navigation
import com.example.mpttodo.verefication.Verification
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

@Suppress("DEPRECATION")
class RegistrationAuthorization(
    private val mAuth: FirebaseAuth?,
    private val context: Context, requireFragmentManager:FragmentManager) {

    private var verification = Verification()
    private val navigation = Navigation()
    private val requireFragManager = requireFragmentManager

    fun signIn(login: String,password: String){
        if (verification.verificationEditText(login,password)){
            mAuth!!.signInWithEmailAndPassword(login,password).addOnCompleteListener{ task ->
                if (verification.isNetworkAvailable(context) || !context.let { Verification().hasConnection(it) }) {
                    if (task.isSuccessful) {
                        FirebaseDatabase.getInstance().getReference(mAuth.uid.toString()).addValueEventListener(object : ValueEventListener {

                            override fun onCancelled(error: DatabaseError) {
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.childrenCount.toInt() == 0){
                                    Recording(FirebaseDatabase.getInstance(), mAuth.uid.toString())
                                        .writeTitle("Завершенные",context.resources.getColor(R.color.portage))
                                }
                                navigation.makeCurrentFragment(HomeFragment(),requireFragManager)
                            }

                        })
                    } else {
                            Toast.makeText(context, "Ошибка", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Проверьте подключение к интернету", Toast.LENGTH_LONG).show()
                }
            }
        }
        else{
            Toast.makeText(context,"Вы не ввели логин или пароль", Toast.LENGTH_LONG).show()
        }
    }

    fun signUp(login: String,password: String){
        if (verification.verificationEditText(login,password)){
            mAuth!!.createUserWithEmailAndPassword(login,password).addOnCompleteListener{ task ->
                if (verification.isNetworkAvailable(context) || !context.let { Verification().hasConnection(it) }) {
                    if (task.isSuccessful) {
                        sendEmailVerification()
                    } else {
                        Toast.makeText(context, "Ошибка", Toast.LENGTH_LONG).show()
                    }
                }
                else
                {
                    Toast.makeText(context,"Проверьте подключение к интернету", Toast.LENGTH_LONG).show()
                }
            }
        }else{
            Toast.makeText(context,"Вы не ввели логин или пароль", Toast.LENGTH_LONG).show()
        }
    }

    fun sendEmailVerification(){
        mAuth!!.currentUser!!.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(context,"Проверьте почту и подтвердите свой почтовый адрес",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context,"Ошибка верефикации почты",Toast.LENGTH_LONG).show()
            }
        }
    }
}
