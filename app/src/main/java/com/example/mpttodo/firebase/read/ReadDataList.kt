package com.example.mpttodo.firebase.read

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mpttodo.Constant
import com.example.mpttodo.R
import com.example.mpttodo.SharedPreference
import com.example.mpttodo.adapter.AdapterTitleList
import com.example.mpttodo.firebase.delete.DeleteItem
import com.example.mpttodo.firebase.update.Update
import com.example.mpttodo.firebase.write.Recording
import com.example.mpttodo.intarface.ListInterface
import com.example.mpttodo.model.ListCreate
import com.example.mpttodo.verefication.Verification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


class ReadDataList(private val context: Context, private val mAuth:FirebaseAuth, private val view: View) {

    val reference = FirebaseDatabase.getInstance()
    var listTitle = arrayListOf<ListCreate>()
    var listNameTitle = arrayListOf<String>()
    val verification = Verification()
    val constant = Constant()
    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerList)
    var status = true

    suspend fun readTitle(newTitle:String,color:Int,code:String,interfaceList:ListInterface){
        var codeCustom = code
        var newTitleInit = newTitle

        reference.getReference(mAuth.uid.toString()).child("Title").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot){
                listTitle.clear()

                for (dataSnapshot in snapshot.children){
                    try {
                        val namReadTitle = dataSnapshot.child("nameList").value.toString()
                        val colorInt = dataSnapshot.child("color").value.toString().toInt()
                        listTitle.add(
                            ListCreate(dataSnapshot.key.toString(), namReadTitle, colorInt)
                        )
                        listNameTitle.add(namReadTitle)
                        if (namReadTitle == newTitleInit) {
                            status = false
                        }
                        if (namReadTitle == "Завершенные") {
                            SharedPreference(context).save(constant.keySharedIdListСompleted, dataSnapshot.key.toString())
                        }
                    }catch (e:Exception){
                        Log.d("ErrorReadTitle",e.message.toString())
                    }
                }

                if (codeCustom == "search") {
                    if (!status){
                        newTitleInit = verification.newGenerationListName(newTitleInit,listNameTitle)
                    }
                    Recording(FirebaseDatabase.getInstance(), mAuth.uid.toString()).writeTitle(newTitleInit, color)
                }

                try {
                    interfaceList.progressBars()
                }catch(e:Exception) {}

               CoroutineScope(Dispatchers.IO).launch {
                    inputDataTitle(listTitle,interfaceList)
               }

                Log.d("ReadTitle","Все данные выведенны")

                codeCustom = ""
                newTitleInit = ""
            }
        })
    }

    suspend fun inputDataTitle(list:MutableList<ListCreate>,interfaceList:ListInterface){
        withContext(Dispatchers.Main) {
            recyclerView!!.layoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = AdapterTitleList(context, list) { it ->
                interfaceList.clickItemTitle(it)
            }

        }
    }

    fun readUpdateNameList(updateNameList: String,idList:String,cod:String) {

        var updateName = updateNameList
        var code = cod

        reference.getReference(mAuth.uid.toString()).child("Title")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    listNameTitle.clear()

                    for (dataSnapshot in snapshot.children) {
                        val namReadTitle = dataSnapshot.child("nameList").value.toString()
                        listNameTitle.add(namReadTitle)

                        if (namReadTitle == updateName) {
                            status = false
                        }
                    }

                    if (code == "1") {
                        if (!status) {
                            updateName = verification.newGenerationListName(updateName, listNameTitle)
                        }
                        Update(mAuth.uid.toString()).updateItem("Title/$idList/nameList", updateName)
                    }

                    code = ""
                    updateName = ""
                }
            })
    }

    fun deleteVerification(idCom:String,idList: String){
        FirebaseDatabase.getInstance().getReference(mAuth.uid.toString()).child("Title").child(idCom).child("Task")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapshot in snapshot.children) {
                        if (dataSnapshot.child("idListNow").value == idList){
                            DeleteItem().deleteItemTask(mAuth.uid.toString(),idCom,dataSnapshot.key.toString())
                        }
                    }
                }
            })
    }

}


