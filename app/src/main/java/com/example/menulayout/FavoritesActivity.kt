package com.example.menulayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesActivity : AppCompatActivity() {

    private val fStore = FirebaseFirestore.getInstance()
    lateinit var recylerView: RecyclerView
    lateinit var foodAdapter: FoodAdapter
    private val mArrayList: ArrayList<ModelFood> = ArrayList()
    private val userid = FirebaseAuth.getInstance().currentUser!!.uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val actionBar = supportActionBar
        actionBar!!.title = "Favorites"

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        var ref=fStore.collection("HotBox Admin").document("F0y2F2SeaoWHjY7sIHFr4JRf1HF2")
        val retData = fStore.collection("HotBox").document(userid)
            .collection("Favorites")
        retData.get()
            .addOnSuccessListener {
                for (i in it) {
                    if (i.exists()) {
                        ref.collection(i.getString("foodcategory")!!).document(i.id).get()
                            .addOnSuccessListener {data->
                                Log.d("data", data.get("foodname").toString())
                                try {
                                    val types: ModelFood = ModelFood(
                                        data.get("imageuri").toString(),
                                        data.get("foodname").toString(),
                                        data.get("foodprice").toString(),
                                        data.get("foodofferprice").toString(),
                                        data.get("fooddescription").toString(),
                                        data.get("foodcategory").toString()
                                    )
                                    mArrayList.add(types)
                                } catch (e: Exception) {
                                    Log.d("exe", e.toString())
                                }
                                foodAdapter.update(mArrayList)
                            }
                    }
                }
            }

        recylerView = findViewById(R.id.favorites_recycler_view)
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
