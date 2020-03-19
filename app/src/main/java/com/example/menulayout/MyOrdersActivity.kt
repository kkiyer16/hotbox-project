package com.example.menulayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class MyOrdersActivity : AppCompatActivity() {

    private val fStore = FirebaseFirestore.getInstance()
    lateinit var recylerView: RecyclerView
    lateinit var ordersAdapter: MyOrdersAdapter
    private val mArrayList : ArrayList<ModelOrders> = ArrayList()
    private val userid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)

        supportActionBar!!.title = "My Orders"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val retData = fStore.collection("HotBox").document("Orders").collection(userid)
        retData.addSnapshotListener { snap, excep ->
            if (snap != null){
                for (i in snap.documentChanges){
                    if (i.type == DocumentChange.Type.ADDED){
                        if (i.document.exists()){
                            try {
                                val types : ModelOrders = ModelOrders(
                                    i.document.getString("city")!!,
                                    i.document.getString("deliverytime")!!,
                                    i.document.getString("home")!!,
                                    i.document.getString("landmark")!!,
                                    i.document.getString("mobno")!!,
                                    i.document.getString("name")!!,
                                    i.document.getString("ordered_at")!!,
                                    i.document.getString("pin")!!,
                                    i.document.getString("road")!!,
                                    i.document.getString("state")!!,
                                    i.document.getString("totalprice")!!
                                )
                                types.set(i.document.id)
                                mArrayList.add(types)

                            }catch (e: Exception){
                                e.printStackTrace()
                                Log.d("ex", e.message.toString().trim())
                            }
                        }
                    }
                }
                ordersAdapter.update(mArrayList)
            }
        }

        recylerView = findViewById(R.id.my_orders_recycler_view)
        recylerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recylerView.layoutManager = layoutManager
        ordersAdapter = MyOrdersAdapter(applicationContext, mArrayList)
        recylerView.adapter = ordersAdapter
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
