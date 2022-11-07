package com.example.mpttodo.alertDialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.mpttodo.Constant
import com.example.mpttodo.R
import com.example.mpttodo.SharedPreference
import com.example.mpttodo.firebase.delete.DeleteItem
import com.example.mpttodo.firebase.read.ReadDataList
import com.example.mpttodo.firebase.update.Update
import com.example.mpttodo.intarface.ListInterface
import com.example.mpttodo.navigation.Navigation
import com.example.mpttodo.verefication.Verification
import com.google.firebase.auth.FirebaseAuth
import com.thebluealliance.spectrum.SpectrumPalette
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Suppress("DEPRECATION", "CAST_NEVER_SUCCEEDS")
class AlertDialogCustomer(private val context:Context) {

    private var nameCreateList : EditText? = null
    private var spectr : SpectrumPalette? = null
    var color:Int ?=null
    var newListName:String ?= null
    var buttonPositive:Button ?= null
    var constant: Constant = Constant()
    val deleteItem = DeleteItem()
    var navigation: Navigation = Navigation()

    var mAut = FirebaseAuth.getInstance()

    var update = Update(mAut.uid.toString())

    var dialog:AlertDialog ?= null


    @SuppressLint("WrongConstant")
    fun alertDialogList(viewFragment: View, interfaceList:ListInterface){

        val view = LayoutInflater.from(context).inflate(R.layout.alert_create_list,null)
        nameCreateList = view.findViewById(R.id.nameList)
        spectr = view.findViewById(R.id.spectrum)

        spectr!!.setOnColorSelectedListener {
                clk-> color=clk
        }

        spectr!!.setSelectedColor(view.resources.getColor(R.color.portage))
        color = view.resources.getColor(R.color.portage)

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Создать список")
        builder.setView(view)
        builder.setPositiveButton("Создать список"){ _, _ ->

            val createList = Verification().checkingSpaces(newListName!!)
            CoroutineScope(Dispatchers.Main).launch {
                ReadDataList(context, mAut, viewFragment)
                    .readTitle(createList, color!!, "search",interfaceList)
            }

        }.setNegativeButton("Отмена"){ _, _ -> }

        val dialog = builder.create()
        dialog.show()

        buttonPositive = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
        buttonPositive!!.isEnabled = false

        nameCreateList!!.addTextChangedListener(textWatcherNewTitle)

    }

    val textWatcherNewTitle =  object :TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            newListName = s.toString()
            buttonPositive!!.isEnabled = newListName!!.trim(' ').isNotEmpty()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }


    fun alertDialogDeleteTitle(idTitle:String,fragment: Fragment,requireManager: FragmentManager,view: View){
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder
            .setTitle("Удаление списка")
            .setMessage("Вы точно хотите удалить этот список?")
            .setPositiveButton("Да"){_,_->
                ReadDataList(context,mAut,view).deleteVerification(SharedPreference(context).open(constant.keySharedIdListСompleted),idTitle)
                deleteItem.deleteItemTitle(idTitle, FirebaseAuth.getInstance().uid.toString())
                SharedPreference(context).shared.edit().clear().apply()
                fragment.let { it1 -> navigation.makeCurrentFragment(it1, requireManager) }
            }
            .setNegativeButton("Нет"){_,_->
            }.create().show()
    }

    fun updateColor(colorCurrent:Int,idList:String){
        val view = LayoutInflater.from(context).inflate(R.layout.alert_update_color,null)
        spectr = view.findViewById(R.id.spectrum)

        spectr!!.setOnColorSelectedListener {
                clk-> color=clk
        }

        spectr!!.setSelectedColor(colorCurrent)
        color = colorCurrent

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Изменение темы")
        builder.setView(view)
        builder.setPositiveButton("Применить"){ _, _ ->
            update.updateItemInt("Title/$idList/color", color!!)
        }.setNegativeButton("Отмена"){ _, _ -> }

        dialog = builder.create()
        dialog!!.show()
    }

    fun updateNamTitle(nameList:String,idList:String,viewFragment: View){
        val view = LayoutInflater.from(context).inflate(R.layout.alert_update_name,null)
        nameCreateList = view.findViewById(R.id.nameListNew)
        nameCreateList!!.setText(nameList)
        newListName = nameList

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Изменение названия списка")
        builder.setView(view)
        builder.setPositiveButton("Сохранить"){ _, _ ->

            if (nameList != newListName) {
                ReadDataList(context, mAut, viewFragment)
                    .readUpdateNameList(newListName!!, idList,"1")
            }else{
                dialog!!.dismiss()
            }
        }.setNegativeButton("Отмена"){ _, _ -> }

        dialog = builder.create()
        dialog!!.show()

        buttonPositive = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
        buttonPositive!!.isEnabled = true

        nameCreateList!!.addTextChangedListener(textWatcherNewTitle)
    }
}