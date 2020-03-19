package com.example.menulayout

import android.app.Activity
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_check_out.*
import kotlinx.android.synthetic.main.activity_home_food.*
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CheckOutActivity : AppCompatActivity(), Serializable {

    private val fStore = FirebaseFirestore.getInstance()
    private val userid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private lateinit var chk_name : TextView
    private lateinit var chk_house : TextView
    private lateinit var chk_road : TextView
    private lateinit var chk_landmark : TextView
    private lateinit var chk_city : TextView
    private lateinit var chk_state : TextView
    private lateinit var chk_pin_code : TextView
    private lateinit var chk_mob_no : TextView
    private lateinit var chk_total_price : TextView
    private val resultcode = 123
    private var dateTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)

        val mIntent = intent
        val a = mIntent.getStringExtra("total")

        supportActionBar!!.title = "Check Out"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        chk_name = findViewById(R.id.name_check_out)
        chk_house = findViewById(R.id.house_check_out)
        chk_road = findViewById(R.id.road_check_out)
        chk_landmark = findViewById(R.id.landmark_check_out)
        chk_city = findViewById(R.id.city_check_out)
        chk_state = findViewById(R.id.state_check_out)
        chk_pin_code = findViewById(R.id.pin_code_check_out)
        chk_mob_no = findViewById(R.id.mob_no_check_out)
        chk_total_price = findViewById(R.id.check_out_product_sub_total)
        chk_total_price.text = a

        image_view_clock_check_out.setOnClickListener {
            val cal = Calendar.getInstance()
            val timesetListener = TimePickerDialog.OnTimeSetListener { _, hr, min ->
                cal.set(Calendar.HOUR_OF_DAY, hr)
                cal.set(Calendar.MINUTE, min)
                tv_delivery_time_check_out.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }
            TimePickerDialog(this, timesetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this)).show()
        }

        dateTime = SimpleDateFormat("MMM dd, yyyy HH:mm").format(Calendar.getInstance().time).toString().trim()
        Log.d("time", dateTime)

        address_check_out_rel_layout_2.visibility = View.INVISIBLE
        food_details_check_rel_layout_3.visibility = View.INVISIBLE

        click_here_to_select_address.setOnClickListener {
            address_check_out_rel_layout_2.visibility = View.VISIBLE
            food_details_check_rel_layout_3.visibility = View.VISIBLE
            startActivityForResult(Intent(this, MyAddressActivity::class.java).apply {
                putExtra("other_activity","checkout")
            },resultcode)
        }

        check_out_confirm_order_button.setOnClickListener {
            confirmOrder()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==123) {
            if (resultCode == Activity.RESULT_OK) {
                val add = data!!.getSerializableExtra("address_object") as ModelAddress
                chk_name.text = add.name
                chk_house.text = add.buildno
                chk_road.text = add.roadname
                chk_city.text = add.city
                chk_pin_code.text = add.pincode
                chk_state.text = add.state
                chk_landmark.text = add.landmark
                chk_mob_no.text = add.mobileno
                Log.d("adderes", "true")
            }
        }
    }

    private fun confirmOrder(){
        val uuid = UUID.randomUUID().toString()

        val name = chk_name.text.toString().trim().trim()
        val home = chk_house.text.toString().trim()
        val road = chk_road.text.toString().trim()
        val city = chk_city.text.toString().trim()
        val pin = chk_pin_code.text.toString().trim()
        val state = chk_state.text.toString().trim()
        val landm = chk_landmark.text.toString().trim()
        val mob = chk_mob_no.text.toString().trim()
        val tot = chk_total_price.text.toString().trim()
        val deltime = tv_delivery_time_check_out.text.toString().trim()
        val orderedat = dateTime

        try {
            val cnfmData = HashMap<String, Any>()
            cnfmData["uid"] = userid
            cnfmData["name"] = name
            cnfmData["home"] = home
            cnfmData["road"] = road
            cnfmData["city"] = city
            cnfmData["pin"] = pin
            cnfmData["state"] = state
            cnfmData["landmark"] = landm
            cnfmData["mobno"] = mob
            cnfmData["totalprice"] = tot
            cnfmData["deliverytime"] = deltime
            cnfmData["ordered_at"] = orderedat

            confirm_order_progress_bar.visibility = View.VISIBLE

            val ref1 = fStore.collection("HotBox Admin").document("Orders").collection(userid)
            ref1.get().addOnSuccessListener {
                if (!it.isEmpty) {
                    Toast.makeText(this, "Please wait for your previous order to deliver", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }
                else {
                    val ref = fStore.collection("HotBox").document("Orders").collection(userid).document(uuid)
                    ref.set(cnfmData, SetOptions.merge())
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                fStore.collection("HotBox Admin").document("Orders")
                                    .collection(userid)
                                    .document(uuid)
                                    .set(cnfmData, SetOptions.merge())
                                    .addOnCompleteListener { tk ->
                                        if (tk.isSuccessful) {
                                            confirm_order_progress_bar.visibility = View.INVISIBLE
                                            Toast.makeText(this, "Order Placed Successfully", Toast.LENGTH_LONG).show()
                                            Log.d("cnf", "Order Placed Successfully")
                                            clearUserCartAfterConfirmOrder()
                                            startActivity(Intent(this, MainActivity::class.java))
                                            finish()
                                        }
                                    }
                                    .addOnFailureListener { ex ->
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
            Log.d("ex", e.message.toString())
        }
    }

    private fun clearUserCartAfterConfirmOrder(){
        Thread(
            Runnable {
                    var ref = fStore.collection("HotBox").document(userid)
                        .collection("Cart List").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            for(ds in querySnapshot!!.documents){
                                fStore.collection("HotBox").document(userid)
                                    .collection("Cart List").document(ds.id).delete().addOnSuccessListener {
                                        Log.d("del",ds.id)
                                    }
                            }

                        }
                    Thread.sleep(5000)
            }
        ).start()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
