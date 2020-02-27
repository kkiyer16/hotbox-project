package com.example.menulayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_food_menu.*

class FoodMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_menu)

        val actionBar = supportActionBar
        actionBar!!.title = "Food Menu"

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        ig_breakfast.setOnClickListener {
            startActivity(Intent(this, BreakfastActivity::class.java))
        }

        ig_veg_lunch.setOnClickListener {  }

        ig_non_veg_lunch.setOnClickListener {  }

        ig_snack.setOnClickListener {  }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
