package com.example.menulayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class BreakfastActivity : AppCompatActivity() {

    private val fStore = FirebaseFirestore.getInstance()
    lateinit var recylerView: RecyclerView
    lateinit var foodAdapter: FoodAdapter
    private val mArrayList: ArrayList<ModelFood> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breakfast)

        val actionBar = supportActionBar
        actionBar!!.title = "Breakfast"

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        val retData = fStore.collection("HotBox Admin").document("F0y2F2SeaoWHjY7sIHFr4JRf1HF2")
            .collection("Breakfast")
        retData.get()
            .addOnSuccessListener {
                for (i in it) {
                    if (!i.exists()) {
                        Log.d("Main", "Empty")
                    } else {
                        Log.d("data", i.get("foodname").toString())
                        try {
                            val types: ModelFood = ModelFood(
                                i.getString("imageuri")!!,
                                i.getString("foodname")!!,
                                i.getString("foodprice")!!,
                                i.getString("foodofferprice")!!,
                                i.getString("fooddescription")!!
                            )
                            mArrayList.add(types)
                        } catch (e: Exception) {
                            Log.d("exe", e.toString())
                        }
                    }
                }
                foodAdapter.update(mArrayList)
            }

        recylerView = findViewById(R.id.recyler_view_breakfast)
        recylerView.setHasFixedSize(true)
        val layoutmanager = LinearLayoutManager(this)
        recylerView.layoutManager = layoutmanager
        foodAdapter = FoodAdapter(applicationContext, mArrayList)
        recylerView.adapter = foodAdapter


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
