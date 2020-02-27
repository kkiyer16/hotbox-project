package com.example.menulayout

import android.content.Context
import android.graphics.ColorSpace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddressAdapter(var con: Context, var list: ArrayList<ModelAddress>) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(con)
        val view: View = layoutInflater.inflate(R.layout.rv_address_items, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(list: ArrayList<ModelAddress>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val addressItem = list[position]

        holder.tv_name.text = addressItem.name
        holder.tv_house.text = addressItem.buildno
        holder.tv_road.text = addressItem.roadname
        holder.tv_landmark.text = addressItem.landmark
        holder.tv_city.text = addressItem.city
        holder.tv_state.text = addressItem.state
        holder.tv_pin_code.text = addressItem.pincode
        holder.tv_mobile.text = addressItem.mobileno

    }

}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv_name = itemView.findViewById(R.id.name_add) as TextView
    val tv_house = itemView.findViewById(R.id.house_add) as TextView
    val tv_road = itemView.findViewById(R.id.road_add) as TextView
    val tv_landmark = itemView.findViewById(R.id.landmark_add) as TextView
    val tv_city = itemView.findViewById(R.id.city_add) as TextView
    val tv_state = itemView.findViewById(R.id.state_add) as TextView
    val tv_pin_code = itemView.findViewById(R.id.pin_code_add) as TextView
    val tv_mobile = itemView.findViewById(R.id.mob_no_add) as TextView
}