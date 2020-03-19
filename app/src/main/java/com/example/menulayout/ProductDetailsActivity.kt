package com.example.menulayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_product_details.*
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

class ProductDetailsActivity : AppCompatActivity(), Serializable {

    private lateinit var food_image :  ImageView
    private lateinit var food_name : TextView
    private lateinit var food_price : TextView
    private lateinit var food_quantity : ElegantNumberButton
    private lateinit var food_desc : TextView
    private lateinit var food_cart_btn : Button
    private lateinit var food_category : TextView
    lateinit var img : String
    lateinit var offer : String
    private val fStore = FirebaseFirestore.getInstance()
    private val userid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val uuid = UUID.randomUUID().toString()
    private var fid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        val i = intent
        val m  = i.getSerializableExtra("object") as ModelFood
        fid = m.foodid
        Log.d("Main", fid.toString())

        val actionBar = supportActionBar
        actionBar!!.title = m.foodname

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        food_image = findViewById(R.id.product_details_food_image)
        food_name = findViewById(R.id.product_details_food_name)
        food_price = findViewById(R.id.product_details_food_price)
        food_quantity = findViewById(R.id.product_details_food_quantity)
        food_desc = findViewById(R.id.product_details_food_description)
        food_cart_btn = findViewById(R.id.product_details_button)
        food_category = findViewById(R.id.product_details_food_category)

        food_name.text = m.foodname
        Log.d("Main", food_name.text.toString().trim())
        food_price.text = m.foodprice
        food_desc.text = m.fooddescription
        food_category.text = m.foodcategory
        img = m.imageuri
        Log.d("Main", "Image URL : $img")
        offer = m.foodofferprice
        Log.d("Main", "Offer : $offer")
        Glide.with(this).load(m.imageuri).centerCrop().dontAnimate().into(food_image)

        food_cart_btn.setOnClickListener {
            addToCart()
        }
    }

    private fun addToCart() {
        val fd_name = food_name.text.toString().trim()
        val fd_price = food_price.text.toString().trim()
        val fd_desc = food_desc.text.toString().trim()
        val fd_cat = food_category.text.toString().trim()
        Log.d("Main", "Category : $fd_cat")
        val fd_qty = food_quantity.number.toString().trim()
        val fd_img = img
        val fd_offer = offer

        product_details_progress_bar.visibility = View.VISIBLE

        try {
            val cartData = HashMap<String, Any>()
            cartData["nameoffood"] = fd_name
            cartData["priceoffood"] = fd_price
            cartData["descoffood"] = fd_desc
            cartData["categoryoffood"] = fd_cat
            cartData["qtyoffood"] = fd_qty
            cartData["imageoffood"] = fd_img
            cartData["offeroffood"] = fd_offer

            val ref = fStore.collection("HotBox").document(userid).collection("Cart List").document(fid!!)
            ref.set(cartData, SetOptions.merge())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val mRef = fStore.collection("HotBox Admin")
                            //.document("F0y2F2SeaoWHjY7sIHFr4JRf1HF2")
                            .document("Cart List")
                            .collection(userid)
                            .document(fid!!)
                        mRef.set(cartData, SetOptions.merge())
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    product_details_progress_bar.visibility = View.INVISIBLE
                                    Toast.makeText(this, "$fd_name added to Cart", Toast.LENGTH_LONG).show()
                                    startActivity(Intent(this, FoodMenuActivity::class.java))
                                    finish()
                                    Log.d("Main", "$fd_name added to Cart")
                                } else {
                                    Toast.makeText(this, "$fd_name Failed to add into Cart", Toast.LENGTH_LONG).show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, it.message.toString().trim(), Toast.LENGTH_LONG).show()
                                Log.d("Main", it.message.toString().trim())
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message.toString().trim(), Toast.LENGTH_LONG).show()
                    Log.d("Main", it.message.toString().trim())
                }

        }catch (e: Exception){
            Log.d("Main", e.message.toString().trim())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
