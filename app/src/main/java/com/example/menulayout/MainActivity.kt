package com.example.menulayout

import android.content.Intent
import android.os.Build
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
//import android.support.annotation.RequiresApi
//import android.support.design.widget.BottomNavigationView
//import android.support.design.widget.NavigationView
//import android.support.v4.app.FragmentTransaction
//import android.support.v4.view.GravityCompat
//import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var homeFragment: HomeFragment
    lateinit var searchFragment: SearchFragment
    lateinit var notificationFragment: NotificationFragment
    lateinit var messageFragment: MessageFragment
    lateinit var authen : FirebaseAuth
    lateinit var firestore:FirebaseFirestore
    private val adminID = "F0y2F2SeaoWHjY7sIHFr4JRf1HF2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.btm_nav)

        setSupportActionBar(toolBar)
        val actionBar = supportActionBar
        actionBar?.title = "HOTBOX"

        val drawerToggle : ActionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawer_layout, toolBar, R.string.open, R.string.close){}

        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        firestore= FirebaseFirestore.getInstance()
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            firestore.collection("HotBox")
                .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .collection("Users")
                .document("PersonalDetails")
                .update("token",it.token.toString())
        }

        homeFragment = HomeFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        bottomNavigationView.setOnNavigationItemSelectedListener {item->
            when(item.itemId){
                R.id.home_btm->{
                    homeFragment = HomeFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, homeFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.search_btm->{
                    searchFragment = SearchFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, searchFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.notif_btm->{
                    notificationFragment = NotificationFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, notificationFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.msg->{
                    messageFragment = MessageFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, messageFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
            }
            true
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId){
            R.id.home -> {
                homeFragment = HomeFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, homeFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.foodmenu -> {
                startActivity(Intent(this, FoodMenuActivity::class.java))
            }
            R.id.favourites -> {
                startActivity(Intent(this,FavoritesActivity::class.java))
            }
            R.id.myorders -> {
                startActivity(Intent(this,MyOrdersActivity::class.java))
            }
            R.id.mycart -> {
                startActivity(Intent(this,MyCartActivity::class.java))
            }
            R.id.feedback -> {
                startActivity(Intent(this,FeedbackActivity::class.java))
            }
            R.id.aboutus -> {
                startActivity(Intent(this,AboutUsActivity::class.java))
            }
            R.id.profile -> {
                startActivity(Intent(this,ProfileActivity::class.java))
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.side_menu, menu)
        return true
    }

    override fun onStart() {
        super.onStart()

        if(FirebaseAuth.getInstance().currentUser == null){
            startActivity(Intent(this, LoginPageActivity::class.java))
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            R.id.log_out_main->{
                AlertDialog.Builder(this).apply {
                    setTitle("Are you Sure?")
                    setPositiveButton("OK"){ _, _ ->
                        FirebaseAuth.getInstance().signOut()
                        LogOut()
                    }
                    setNegativeButton("Cancel"){ _, _ ->
                    }
                }.create().show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun LogOut(){
        Toast.makeText(this, "Logged Out!", Toast.LENGTH_LONG).show()
        startActivity(Intent(this, LoginPageActivity::class.java))
        finish()
    }
}
