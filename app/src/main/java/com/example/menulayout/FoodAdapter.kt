package com.example.menulayout

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FoodAdapter(var con: Context, var list: ArrayList<ModelFood>) :
    RecyclerView.Adapter<viewHolder>() {

    private val userid = FirebaseAuth.getInstance().currentUser!!.uid.toString()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(con)
        val v : View = layoutInflater.inflate(R.layout.layout_breakfast_rv, parent, false)
        return viewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(list: ArrayList<ModelFood>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val foodItem = list[position]

        Glide.with(con).load(foodItem.imageuri).centerCrop().dontAnimate().into(holder.ig_fd_image)
        holder.tv_fd_name.text = foodItem.foodname
        holder.tv_fd_price.text = foodItem.foodprice
        holder.tv_fd_offprice.text = foodItem.foodofferprice

        holder.parent_lay.setOnClickListener {
            val intent = Intent(con, ProductDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("object",foodItem)
            con.startActivity(intent)
            //Toast.makeText(con,foodItem.foodname+" Clicked", Toast.LENGTH_LONG).show()
        }

        holder.ig_fd_cart.setOnClickListener {
            val intent = Intent(con, ProductDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("object",foodItem)
            con.startActivity(intent)
        }

        holder.chk_add_fav.isChecked = foodItem.isLiked == 1

        holder.chk_add_fav.setOnCheckedChangeListener { compoundButton, b ->
            if(compoundButton.isPressed) {
                if (b) {
                    val uuid = UUID.randomUUID().toString()
                    val favData = HashMap<String, Any>()
                    favData["foodcategory"] = foodItem.foodcategory

                    FirebaseFirestore.getInstance().collection("HotBox")
                        .document(userid)
                        .collection("Favorites")
                        .document(foodItem.foodid)
                        .set(favData, SetOptions.merge())
                        .addOnSuccessListener {
                            Toast.makeText(con, foodItem.foodname + " added to Favorites", Toast.LENGTH_LONG).show()
                            holder.chk_add_fav.isChecked = true
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(con, "Failed to Add", Toast.LENGTH_LONG).show()
                            Log.d("Main", e.toString())
                        }
                }
                else {
                    FirebaseFirestore.getInstance().collection("HotBox")
                        .document(userid)
                        .collection("Favorites")
                        .document(foodItem.foodid)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(con, foodItem.foodname + " removed from Favorites", Toast.LENGTH_LONG).show()
                            holder.chk_add_fav.isChecked = false
                        }
                        .addOnFailureListener {
                            Log.d("Main", it.message.toString())
                            Toast.makeText(con, foodItem.foodname + " failed to remove from Favorites", Toast.LENGTH_LONG).show()
                        }
                }
            }
        }

    }
}

class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val parent_lay = itemView.findViewById<RelativeLayout>(R.id.parent_rel_lay)
    val ig_fd_image = itemView.findViewById<ImageView>(R.id.food_image_inside_rv)
    val chk_add_fav = itemView.findViewById<CheckBox>(R.id.favorites_add_to_fav)
    val ig_fd_cart = itemView.findViewById<ImageView>(R.id.image_view_add_to_cart_inside_rv)

    val tv_fd_name = itemView.findViewById<TextView>(R.id.txt_food_name_inside_rv)
    val tv_fd_price = itemView.findViewById<TextView>(R.id.txt_food_price_inside_rv)
    val tv_fd_offprice = itemView.findViewById<TextView>(R.id.txt_food_offer_price_inside_rv)
}