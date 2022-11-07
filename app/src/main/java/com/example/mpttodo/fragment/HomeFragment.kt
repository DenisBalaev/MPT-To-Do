package com.example.mpttodo.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mpttodo.Constant
import com.example.mpttodo.R
import com.example.mpttodo.SharedPreference
import com.example.mpttodo.alertDialog.AlertDialogCustomer
import com.example.mpttodo.firebase.read.ReadDataList
import com.example.mpttodo.intarface.ListInterface
import com.example.mpttodo.model.ListCreate
import com.example.mpttodo.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class HomeFragment : Fragment(), ListInterface {

    var mAuth: FirebaseAuth?=null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    var recyclerView: RecyclerView?=null
    var color=0
    lateinit var listInterface: ListInterface
    var sharedPreference:SharedPreference ?= null
    var constant:Constant ?= null
    var navigation:Navigation ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

        constant = Constant()
        sharedPreference = context?.let { SharedPreference(it) }
        navigation = Navigation()

        if (sharedPreference!!.checkingAvailability(constant!!.keySharedTitleId)){
            constant!!.taskListFragment.let { navigation!!.makeCurrentFragment(it,requireFragmentManager()) }
        }

        listInterface = this
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =inflater.inflate(R.layout.fragment_home, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        init(view)

        CoroutineScope(Dispatchers.Main).launch {
            context?.let { ReadDataList(it, mAuth!!, view).readTitle("", 0, "",listInterface) }
        }

        return view
    }

    fun init(view: View) {
        mAuth = FirebaseAuth.getInstance()
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerList)
    }

    @SuppressLint("InflateParams", "NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createList.setOnClickListener {
            context?.let { it1 -> AlertDialogCustomer(it1).alertDialogList(view,listInterface) }
        }

        mSwipeRefreshLayout!!.setOnRefreshListener {
            CoroutineScope(Dispatchers.Main).launch {
                context?.let { ReadDataList(it,mAuth!!,view).readTitle("",0,"",listInterface) }
                swipeRefreshLayout.isRefreshing = false
            }
        }

        view.isFocusableInTouchMode = true;
        view.requestFocus()
        view.setOnKeyListener(keyListener)
    }

    val keyListener = View.OnKeyListener { v, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        true
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.ic_main_exit->{
                mAuth!!.signOut()
                Navigation().makeCurrentFragment(LoginFragment(),requireFragmentManager())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun clickItemTitle(list: ListCreate) {
        sharedPreference!!.save(constant!!.keySharedTitleId,list.id)
        constant!!.taskListFragment.let { navigation!!.makeCurrentFragment(it,requireFragmentManager()) }
    }

    override fun progressBars() {
        progressBar.visibility = View.GONE
        coordinatorLayout.visibility = View.VISIBLE
    }
}