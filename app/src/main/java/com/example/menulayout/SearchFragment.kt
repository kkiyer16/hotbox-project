package com.example.menulayout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {

    lateinit var recyclerView : RecyclerView
    lateinit var searchView : SearchView
    lateinit var foodAdapter : FoodAdapter
    private var mArrayList: ArrayList<ModelFood> = ArrayList()
    private val fStore = FirebaseFirestore.getInstance()
    private val adminID = "F0y2F2SeaoWHjY7sIHFr4JRf1HF2"

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

                    mArrayList.clear()
                    foodAdapter.update(mArrayList)
                    Log.d("text", p0!!.split(' ').joinToString(" ") { it.capitalize()  })
                    searchFoodBreakfast(p0!!.split(' ').joinToString(" ") { it.capitalize()  })
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
        fStore.collection("HotBoxAdmin")
            .document(adminID)
            .collection("Breakfast")
            .whereGreaterThanOrEqualTo("foodname",s)
            .get().addOnSuccessListener{
                for (i in it!!) {
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
        fStore.collection("HotBoxAdmin")
            .document(adminID)
            .collection("NonVegLunch")
            .whereGreaterThanOrEqualTo("foodname", s)
            .get().addOnSuccessListener{
                for (i in it!!) {
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
        fStore.collection("HotBoxAdmin")
            .document(adminID)
            .collection("VegLunch")
            .whereGreaterThanOrEqualTo("foodname", s)
            .get().addOnSuccessListener{
                for (i in it!!) {
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
        fStore.collection("HotBoxAdmin")
            .document(adminID)
            .collection("Snacks")
            .whereGreaterThanOrEqualTo("foodname",s)
            .get().addOnSuccessListener{
                for (i in it!!) {
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

                val split=s.capitalize().split(' ')
                Log.d("split", split.toTypedArray().contentToString())
                mArrayList=mArrayList.filter {
                   return@filter check(split,it.foodname.capitalize().split(' '))
                } as ArrayList<ModelFood>
                foodAdapter.update(mArrayList)
            }
    }

    fun check(split: List<String>, s: List<String>):Boolean{
        for(s1 in split){
            for(s2 in s){
                if(s1.contains(s2)){
                    return true
                }
            }
        }
        return false
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


//class hw{
//    fun convert(str: String): String {
//        val s = StringBuffer()
//        var ch = ' '
//        for (i in 0 until str.length) {
//            if (ch == ' ' && str[i] != ' ') {
//                s.append(Character.toUpperCase(str[i]))
//            } else {
//                s.append(str[i])
//            }
//            ch = str[i]
//        }
//        return s.toString().trim { it <= ' ' }
//    }
//
//    @JvmStatic
//    fun main(args: Array<String>) {
//        var s = "vada sambar"
//        s = s.toLowerCase()
//        println(convert(s))
//    }
//}
