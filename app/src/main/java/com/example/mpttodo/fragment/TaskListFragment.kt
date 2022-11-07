package com.example.mpttodo.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mpttodo.Constant
import com.example.mpttodo.R
import com.example.mpttodo.SharedPreference
import com.example.mpttodo.alertDialog.AlertDialogCustomer
import com.example.mpttodo.alertDialog.AlertDialogTask
import com.example.mpttodo.firebase.read.ReadTask
import com.example.mpttodo.intarface.BroadcastList
import com.example.mpttodo.navigation.Navigation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Suppress("DEPRECATION", "NAME_SHADOWING")
class TaskListFragment : Fragment(), BroadcastList {

    private var mSwip: SwipeRefreshLayout? = null
    var toolbar: Toolbar?= null
    var backgroung_fon: ConstraintLayout?= null
    var floatingActionButton: FloatingActionButton?= null
    var navigation: Navigation?= null
    var shared: SharedPreference?= null
    var popupMenu: PopupMenu?= null
    var constant: Constant?= null

    var idList:String ?= null
    var nameList:String ?= null

    var readTask: ReadTask?= null
    var mAuth: FirebaseAuth?=null
    var colors:Int ?= null
    var alertDialogCustomer: AlertDialogCustomer?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        navigation = Navigation()
        shared = context?.let { SharedPreference(it) }
        constant = Constant()
        mAuth = FirebaseAuth.getInstance()

        idList = shared!!.open(constant!!.keySharedTitleId)

        shared!!.save(constant!!.keySharedUIDUser,mAuth!!.uid.toString())

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("RestrictedApi", "RtlHardcoded")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)

        backgroung_fon = view.findViewById(R.id.background)
        floatingActionButton = view.findViewById(R.id.btn_addTask)
        mSwip = view.findViewById(R.id.swipeRefresh)

        toolbarInit(view)
        popupInit(view)

        readTask = ReadTask(requireContext(), mAuth!!,view,idList!!)

        CoroutineScope(Dispatchers.IO).launch {
            readTask!!.broadCastLIstInformationRead(this@TaskListFragment)
        }

        alertDialogCustomer = AlertDialogCustomer(requireContext())

        return view
    }

    @SuppressLint("RestrictedApi", "ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btn_addTask.setOnClickListener{
            AlertDialogTask(requireContext()).createTask(idList!!)
        }

        mSwip!!.setOnRefreshListener {
            CoroutineScope(Dispatchers.Main).launch {
                readTask!!.broadCastLIstInformationRead(this@TaskListFragment)
                mSwip!!.isRefreshing = false
            }
        }

        view.isFocusableInTouchMode = true;
        view.requestFocus()
        view.setOnKeyListener(keyListener)
    }

    val keyListener = View.OnKeyListener { _, keyCode, _ ->
           if (keyCode == KeyEvent.KEYCODE_BACK) {
                backFragmentTask()
            }
        true
    }

     fun toolbarInit(view: View){

        toolbar = view.findViewById(R.id.toolbar)
        toolbar!!.title = ""
        val support = activity as AppCompatActivity
        support.setSupportActionBar(toolbar)

        support.supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        support.supportActionBar!!.setHomeButtonEnabled(true);
        support.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back)

        toolbar!!.setNavigationOnClickListener {
            backFragmentTask()
        }
    }

    @SuppressLint("RtlHardcoded")
    @RequiresApi(Build.VERSION_CODES.M)
    fun popupInit(view: View){
        popupMenu = PopupMenu(requireContext(),view.findViewById(R.id.toolbar))
        clickPopupMenu()
        if (nameList == "Завершенные") {
            popupMenu!!.inflate(R.menu.task_special_menu)
        }else{
            popupMenu!!.inflate(R.menu.task_menu)
        }
        popupMenu!!.gravity = Gravity.RIGHT
        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible  = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(mPopup,true)
        }catch (e:Exception){
            Log.e("Popup",e.message.toString())
        }
    }

    fun clickPopupMenu () {
        popupMenu!!.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.ic_change_name_title -> {
                    alertDialogCustomer!!.updateNamTitle(title_name.text.toString(),idList!!,requireView())
                    true
                }
                R.id.ic_change_topics -> {
                    alertDialogCustomer!!.updateColor(colors!!,idList!!)
                    true
                }
                R.id.ic_delete_item_title -> {
                    alertDialogCustomer!!.alertDialogDeleteTitle(idList!!, constant!!.homeFragment,requireFragmentManager(),requireView())
                    true
                }
                else -> false
            }
        }
    }

    fun backFragmentTask(){
        shared!!.shared.edit().clear().apply()
        constant!!.homeFragment.let { it1 -> navigation!!.makeCurrentFragment(it1, requireFragmentManager()) }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.more_task, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("RestrictedApi")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.ic_more->{
                popupMenu!!.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun broadcastListInformation(name: String, color: Int) {
        if (name != "" && color != 0){
            title_name.text = name
            colors = color
            nameList = name
            CoroutineScope(Dispatchers.IO).launch {
                readTask!!.readingTasks(this@TaskListFragment,nameList!!,requireFragmentManager())
            }
        }else{
        }
    }

    override fun endReadingTask() {
        visibilityComponent()
    }

    fun visibilityComponent(){
        toolbar!!.setBackgroundColor(colors!!)
        toolbar!!.visibility = View.VISIBLE
        backgroung_fon!!.setBackgroundColor(colors!!)
        if (nameList != "Завершенные") {
            floatingActionButton!!.backgroundTintList = ColorStateList.valueOf(colors!! + 30)
            floatingActionButton!!.visibility = View.VISIBLE
        }
        progressBar.visibility = View.GONE
    }

}
