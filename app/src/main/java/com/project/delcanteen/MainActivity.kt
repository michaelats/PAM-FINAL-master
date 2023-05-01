package com.project.delcanteen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.delcanteen.activity.LoginActivity
import com.project.delcanteen.activity.MasukActivity
import com.project.delcanteen.fragment.AccountFragment
import com.project.delcanteen.fragment.CartFragment
import com.project.delcanteen.fragment.HomeFragment
import com.project.delcanteen.helper.SharedPref

class MainActivity : AppCompatActivity() {
    private val fragmentHome: Fragment = HomeFragment()
    private val fragmentAccount: Fragment = AccountFragment()
    private val fragmentCart: Fragment = CartFragment()

    private val fm:FragmentManager = supportFragmentManager
    private var active:Fragment = fragmentHome

    private lateinit var menu:Menu
    private lateinit var menuItem:MenuItem
    private lateinit var bottomNavigationView:BottomNavigationView

    private val  statusLogin = false
    private lateinit var s:SharedPref
    private var dariDetail: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        s = SharedPref(this)
        setUpBottomNav()
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessage, IntentFilter("event:keranjang"))
    }

    val mMessage: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            dariDetail = true
        }
    }

    fun setUpBottomNav(){
        fm.beginTransaction() .add(R.id.container, fragmentHome).show(fragmentHome).commit()
        fm.beginTransaction() .add(R.id.container, fragmentCart).hide(fragmentCart).commit()
        fm.beginTransaction() .add(R.id.container, fragmentAccount).hide(fragmentAccount).commit()
        bottomNavigationView = findViewById(R.id.nav_view)
        menu = bottomNavigationView.menu
        menuItem = menu.getItem(0)
        menuItem.isChecked = true

        bottomNavigationView.setOnNavigationItemSelectedListener { item->

            when(item.itemId){
                R.id.navigation_home->{
                    callFragment(0, fragmentHome)
                }
                R.id.navigation_cart->{
                    callFragment(1, fragmentCart)
                }
                R.id.navigation_account->{
                    if (s.getStatusLogin()){
                        callFragment(2, fragmentAccount)
                    }else{
                        startActivity(Intent(this, MasukActivity::class.java))
                    }

                }
            }

            false
        }
    }

    fun callFragment(int: Int, fragment: Fragment){
        menuItem = menu.getItem(int)
        menuItem.isChecked = true
        fm.beginTransaction().hide(active).show(fragment).commit()
        active = fragment
    }
    override fun onResume() {
        if (dariDetail) {
            dariDetail = false
            callFragment(1, fragmentCart)
        }
        super.onResume()
    }
}