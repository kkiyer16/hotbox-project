package com.example.menulayout

class ModelHomeOrders(
    val deliveryaddress : String,
    val deliverytime : String,
    val mobileno : String,
    val name : String,
    val ordered_at : String,
    val pickupaddress : String,
    val subscription : String,
    val statusoforder : String,
    val uid : String
)
{
    var homefoodid = ""
    fun set(id: String){
        this.homefoodid = id
    }
}