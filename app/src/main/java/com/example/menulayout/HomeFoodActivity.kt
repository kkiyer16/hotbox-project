package com.example.menulayout

import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_home_food.*
import java.text.SimpleDateFormat
import java.util.*

class HomeFoodActivity : AppCompatActivity() {

    lateinit var subscription_category : Spinner
    private var subs = arrayOfNulls<String>(5)

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
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
