package com.example.menulayout

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class ModelFood (
    var imageuri : String,
    var foodname : String,
    var foodprice : String,
    var foodofferprice : String,
    var fooddescription : String,
    var foodcategory : String
) : Serializable
{
    var foodid:String=""
    var isLiked : Int = 0

    fun set(id:String){
        this.foodid=id
    }
    fun setl(i:Int){
       this.isLiked=i
    }

}