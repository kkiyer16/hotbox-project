package com.example.menulayout

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FoodAdapter(var con: Context, var list: ArrayList<ModelFood>) :
    RecyclerView.Adapter<viewHolder>() {

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

        Glide.with(con).load(foodItem.imagefood).centerCrop().dontAnimate().into(holder.ig_fd_image)
        holder.tv_fd_name.text = foodItem.namefood
        holder.tv_fd_price.text = foodItem.pricefood
        holder.tv_fd_offprice.text = foodItem.offerfood
    }
}

class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ig_fd_image = itemView.findViewById<ImageView>(R.id.food_image_inside_rv)
    val ig_fd_fav = itemView.findViewById<ImageView>(R.id.image_view_add_to_fav_inside_rv)
    val ig_fd_cart = itemView.findViewById<ImageView>(R.id.image_view_add_to_cart_inside_rv)

    val tv_fd_name = itemView.findViewById<TextView>(R.id.txt_food_name_inside_rv)
    val tv_fd_price = itemView.findViewById<TextView>(R.id.txt_food_price_inside_rv)
    val tv_fd_offprice = itemView.findViewById<TextView>(R.id.txt_food_offer_price_inside_rv)
}