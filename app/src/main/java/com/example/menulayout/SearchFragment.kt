package com.example.menulayout

import android.graphics.ColorSpace
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.base.CaseFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList


class SearchFragment : Fragment() {

    lateinit var recyclerView : RecyclerView
    lateinit var searchView : SearchView
    lateinit var foodAdapter : FoodAdapter
    private val mArrayList: ArrayList<ModelFood> = ArrayList()
    lateinit var arrayList : ArrayList<ModelFood>
    private val fStore = FirebaseFirestore.getInstance()
    private val fAuth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val retview = inflater.inflate(R.layout.fragment_search, container, false)

        return retview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView = view.findViewById(R.id.frag_search_view)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if(p0 == ""){
                    mArrayList.clear()
                }
                else {
                    searchFoodBreakfast(p0!!)
                }
                return true
            }
        })

        recyclerView = view.findViewById(R.id.frag_search_recycler_view)
        recyclerView.setHasFixedSize(true)
        val layoutmanager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutmanager
        foodAdapter = FoodAdapter(activity!!.applicationContext, mArrayList)
        recyclerView.adapter = foodAdapter
    }

    private fun searchFoodBreakfast(s: String) {
        fStore.collection("HotBox Admin")
            .document("F0y2F2SeaoWHjY7sIHFr4JRf1HF2")
            .collection("Breakfast")
            .whereEqualTo("foodname",s)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                //mArrayList.clear()
                if (firebaseFirestoreException != null) {
                    Toast.makeText(context, "No Food Found", Toast.LENGTH_LONG).show()
                }
                for (i in querySnapshot!!) {
                    val foodsearch = ModelFood(
                        i.getString("imageuri")!!,
                        i.getString("foodname")!!,
                        i.getString("foodprice")!!,
                        i.getString("foodofferprice")!!,
                        i.getString("fooddescription")!!,
                        i.getString("foodcategory")!!
                    )
                    Log.d("key", i.getString("foodname").toString())
                    mArrayList.add(foodsearch)
                }
                foodAdapter.update(mArrayList)
            }
        searchFoodNonVegLunch(s)
        searchFoodVegLunch(s)
        searchFoodSnacks(s)
    }

    private fun searchFoodNonVegLunch(s: String) {
        fStore.collection("HotBox Admin")
            .document("F0y2F2SeaoWHjY7sIHFr4JRf1HF2")
            .collection("NonVeg Lunch")
            .whereEqualTo("foodname", s)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
               // mArrayList.clear()
                if (firebaseFirestoreException != null) {
                    Toast.makeText(context, "No Food Found", Toast.LENGTH_LONG).show()
                }
                for (i in querySnapshot!!) {
                    val foodsearch = ModelFood(
                        i.getString("imageuri")!!,
                        i.getString("foodname")!!,
                        i.getString("foodprice")!!,
                        i.getString("foodofferprice")!!,
                        i.getString("fooddescription")!!,
                        i.getString("foodcategory")!!
                    )
                    Log.d("key", i.getString("foodname").toString())
                    mArrayList.add(foodsearch)
                }
                foodAdapter.update(mArrayList)
            }
    }

    private fun searchFoodVegLunch(s: String) {
        fStore.collection("HotBox Admin")
            .document("F0y2F2SeaoWHjY7sIHFr4JRf1HF2")
            .collection("Veg Lunch")
            .whereEqualTo("foodname", s)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                //mArrayList.clear()
                if (firebaseFirestoreException != null) {
                    Toast.makeText(context, "No Food Found", Toast.LENGTH_LONG).show()
                }
                for (i in querySnapshot!!) {
                    val foodsearch = ModelFood(
                        i.getString("imageuri")!!,
                        i.getString("foodname")!!,
                        i.getString("foodprice")!!,
                        i.getString("foodofferprice")!!,
                        i.getString("fooddescription")!!,
                        i.getString("foodcategory")!!
                    )
                    Log.d("key", i.getString("foodname").toString())
                    mArrayList.add(foodsearch)
                }
                foodAdapter.update(mArrayList)
            }
    }

    private fun searchFoodSnacks(s: String){
        fStore.collection("HotBox Admin")
            .document("F0y2F2SeaoWHjY7sIHFr4JRf1HF2")
            .collection("Snacks")
            .whereEqualTo("foodname",s)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
               // mArrayList.clear()
                if(firebaseFirestoreException != null){
                    Toast.makeText(context, "No Food Found", Toast.LENGTH_LONG).show()
                }
                for (i in querySnapshot!!) {
                    val foodsearch = ModelFood(
                        i.getString("imageuri")!!,
                        i.getString("foodname")!!,
                        i.getString("foodprice")!!,
                        i.getString("foodofferprice")!!,
                        i.getString("fooddescription")!!,
                        i.getString("foodcategory")!!
                    )
                    Log.d("key", i.getString("foodname").toString())
                    mArrayList.add(foodsearch)
                }
                foodAdapter.update(mArrayList)
            }
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}
