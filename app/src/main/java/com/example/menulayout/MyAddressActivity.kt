package com.example.menulayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_my_address.*
import kotlin.collections.ArrayList

class MyAddressActivity : AppCompatActivity() {

    private val userid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val fStore = FirebaseFirestore.getInstance()
    lateinit var recylerView: RecyclerView
    lateinit var addressList: ArrayList<ModelAddress>
    lateinit var addressAdapter: AddressAdapter
    private val mArratList: ArrayList<ModelAddress> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_address)

        val actionBar = supportActionBar
        actionBar!!.title = "My Address"

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        add_new_address.setOnClickListener {
            startActivity(Intent(this, ProfMyAddressActivity::class.java))
        }

        val retData = fStore.collection("HotBox").document(userid)
            .collection("Users Address")
        retData.get()
            .addOnSuccessListener {
                for (i in it) {
                    if (!i.exists()) {
                        Log.d("Main", "Empty")
                    } else {
                        Log.d("data", i.get("buildno").toString())
                        try {
                            val types: ModelAddress = ModelAddress(
                                i.getString("name")!!,
                                i.getString("buildno")!!,
                                i.getString("roadname")!!,
                                i.getString("city")!!,
                                i.getString("pincode")!!,
                                i.getString("state")!!,
                                i.getString("landmark")!!,
                                i.getString("mobileno")!!,
                                i.getString("altmob")!!,
                                i.getString("addresstype")!!
                            )
                            mArratList.add(types)
                        } catch (e: Exception) {
                            Log.d("exe", e.toString())
                        }
                    }
                }
                addressAdapter.update(mArratList)
            }

        recylerView = findViewById(R.id.recyler_view_address)
        recylerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recylerView.layoutManager = layoutManager
        addressAdapter = AddressAdapter(applicationContext, mArratList)
        recylerView.adapter = addressAdapter

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
