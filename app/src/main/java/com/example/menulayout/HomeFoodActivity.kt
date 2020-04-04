package com.example.menulayout

import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_home_food.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class HomeFoodActivity : AppCompatActivity() {

    lateinit var subscription_category : Spinner
    private var subs = arrayOfNulls<String>(5)
    private val fStore = FirebaseFirestore.getInstance()
    private val userid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_food)

        val actionBar = supportActionBar
        actionBar!!.title = "Order Dabba From Home"

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        subscription_category = findViewById(R.id.spinner_subscription)
        subs = resources.getStringArray(R.array.subs_categories)

        val arr_adap = ArrayAdapter(this, android.R.layout.simple_spinner_item, subs)
        arr_adap.setDropDownViewResource(android.R.layout.simple_spinner_item)
        subscription_category.adapter = arr_adap

        subscription_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(this@HomeFoodActivity, "Select any Subscription", Toast.LENGTH_LONG).show()
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }
        }

        image_view_clock.setOnClickListener {
            val cal = Calendar.getInstance()
            val timesetListener = TimePickerDialog.OnTimeSetListener { _, hr, min ->
                cal.set(Calendar.HOUR_OF_DAY, hr)
                cal.set(Calendar.MINUTE, min)
                tv_delivery_time.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }
            TimePickerDialog(this, timesetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this)).show()
        }

        button_order_home_dabba.setOnClickListener {
            saveHomeDabbaOrderDetails()
        }
    }

    private fun saveHomeDabbaOrderDetails(){
        val uuid = UUID.randomUUID().toString()
        val name = et_name_order_dabba.text.toString().trim()
        val pick_up_add = et_pickup_add.text.toString().trim()
        val del_add = et_del_add.text.toString().trim()
        val mob_no = et_mob_no_order_dabba.text.toString().trim()
        val del_time = tv_delivery_time.text.toString().trim()
        val sub_cat = subscription_category.selectedItem.toString().trim()
        val dateTime = SimpleDateFormat("MMM dd, yyyy HH:mm").format(Calendar.getInstance().time).toString().trim()
        Log.d("time", dateTime)

        if (name.isEmpty()){
            et_name_order_dabba.error = "Please Enter Name"
            et_name_order_dabba.requestFocus()
        }
        else if (pick_up_add.isEmpty()){
            et_pickup_add.error = "Please Enter Name"
            et_pickup_add.requestFocus()
        }
        else if (del_add.isEmpty()){
            et_del_add.error = "Please Enter Name"
            et_del_add.requestFocus()
        }
        else if (mob_no.isEmpty()){
            et_mob_no_order_dabba.error = "Please Enter Name"
            et_mob_no_order_dabba.requestFocus()
        }
        else if (del_time.isEmpty()){
            tv_delivery_time.error = "Please Enter Name"
            tv_delivery_time.requestFocus()
        }
        else if (sub_cat == "Select Subscription"){
            Toast.makeText(this, "Please Select Subscription", Toast.LENGTH_SHORT).show()
        }
        else{
            try {
                val foodDabba = HashMap<String, Any>()
                foodDabba["name"] = name
                foodDabba["pickupaddress"] = pick_up_add
                foodDabba["deliveryaddress"] = del_add
                foodDabba["mobileno"] = mob_no
                foodDabba["ordered_at"] = dateTime
                foodDabba["deliverytime"] = del_time
                foodDabba["subscription"] = sub_cat
                foodDabba["uid"] = userid

                progress_bar_order_home_dabba.visibility = View.VISIBLE
                val ref = fStore.collection("Orders").document(userid).collection("HomeOrders")
                ref.get().addOnSuccessListener {
                    if (!it.isEmpty){
                        Toast.makeText(this, "Please wait for your previous order to deliver", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    else{
                        val ref1 = fStore.collection("HotBox").document(userid)
                            .collection("HomeOrders").document(uuid)
                        ref1.set(foodDabba, SetOptions.merge())
                            .addOnCompleteListener {
                                if (it.isSuccessful){
                                    fStore.collection("Orders").document("AllOrders").get()
                                        .addOnSuccessListener { data ->
                                            if (data.exists()) {
                                                fStore.collection("Orders").document("AllOrders")
                                                    .update(
                                                        "TotalList",
                                                        FieldValue.arrayUnion(userid)
                                                    ).addOnSuccessListener {
                                                        Log.d("ffd", userid.toString())
                                                    }

                                            } else {
                                                fStore.collection("Orders").document("AllOrders")
                                                    .set(
                                                        hashMapOf(
                                                            "TotalList" to arrayListOf(userid.toString())
                                                        )
                                                    ).addOnSuccessListener {
                                                        Log.d("ffd", userid.toString())
                                                    }

                                            }

                                        }
                                    fStore.collection("Orders").document(userid).collection("HomeOrders")
                                        .document(uuid)
                                        .set(foodDabba, SetOptions.merge())
                                        .addOnCompleteListener {tk->
                                            if (tk.isSuccessful){
                                                progress_bar_order_home_dabba.visibility = View.INVISIBLE
                                                Toast.makeText(this, "Order Placed Successfully", Toast.LENGTH_LONG).show()
                                                Log.d("cnf", "Order Placed Successfully")
                                                startActivity(Intent(this, MainActivity::class.java))
                                                finish()
                                            }
                                        }
                                        .addOnFailureListener {ex->
                                            Toast.makeText(this, "Failed to Place Order", Toast.LENGTH_LONG).show()
                                            Log.d("cnf", ex.message.toString().trim())
                                        }
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to Place Order", Toast.LENGTH_LONG).show()
                                Log.d("cnf", it.message.toString().trim())
                            }
                    }
                }

            }catch (e: Exception){
                e.printStackTrace()
                Log.d("ex", e.message.toString().trim())
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
