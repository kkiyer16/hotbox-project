package com.example.menulayout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
//import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.home_frag_scroll_layout.*
import java.io.Serializable
import java.util.*

@Suppress("SENSELESS_COMPARISON")
class HomeFragment : Fragment() {

    lateinit var dots_layout : LinearLayout
    lateinit var mPager : ViewPager
    var path : IntArray = intArrayOf(R.drawable.slide_food1, R.drawable.slide_food2, R.drawable.slide_food3, R.drawable.slide_food4)
    lateinit var dots : Array<ImageView>
    lateinit var adapter : PageView
    var currentPage : Int = 0
    lateinit var timer : Timer
    private val DELAY_MS : Long = 1500
    private val PERIOD_MS : Long = 1500

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val retv = inflater.inflate(R.layout.fragment_home, container, false)

        return retv
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inc_lay = view.findViewById<View>(R.id.include_lay_home)
        val inc_card_home = inc_lay.findViewById<CardView>(R.id.card_order_food_from_home)
        val inc_card_catering = inc_lay.findViewById<CardView>(R.id.card_order_food_from_catering)
        val dis_card1 = inc_lay.findViewById<CardView>(R.id.display_card_1)
        val dis_card2 = inc_lay.findViewById<CardView>(R.id.display_card_2)
        val dis_card3 = inc_lay.findViewById<CardView>(R.id.display_card_3)

        inc_card_home.setOnClickListener {
            startActivity(Intent(activity, HomeFoodActivity::class.java))
        }

        inc_card_catering.setOnClickListener {
            startActivity(Intent(activity, FoodMenuActivity::class.java))
        }

        Glide.with(context!!).load(R.drawable.roti_sabji).centerCrop().dontAnimate().into(hf_image_view_1)

        Glide.with(context!!).load(R.drawable.chicken_masala).centerCrop().dontAnimate().into(hf_image_view_2)

        Glide.with(context!!).load(R.drawable.samosa).centerCrop().dontAnimate().into(hf_image_view_3)

        dis_card1.setOnClickListener {
            startActivity(Intent(activity, VegLunchActivity::class.java))
        }

        dis_card2.setOnClickListener {
            startActivity(Intent(activity, NonVegActivity::class.java))
        }

        dis_card3.setOnClickListener {
            startActivity(Intent(activity, SnacksActivity::class.java))
        }

        mPager = view.findViewById(R.id.view_pager) as ViewPager
        adapter = PageView(context!!, path)
        mPager.adapter = adapter
        dots_layout = view.findViewById(R.id.dotsLayout) as LinearLayout
        createDots(0)
        updatePage()
        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                currentPage = position
                try {
                    createDots(position)
                }catch (e: NullPointerException){

                }
            }
        })
    }

    private fun updatePage(){
        val handler = Handler()
        val Update : Runnable = Runnable {
            if (currentPage == path.size){
                currentPage = 0
            }
            mPager.setCurrentItem(currentPage++, true)
        }
        timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                handler.post(Update)
            }
        }, DELAY_MS,PERIOD_MS)
    }

    fun createDots(position : Int){
        if (dots_layout != null){
            dots_layout.removeAllViews()
        }
        dots = Array(path.size) { ImageView(context) }

        for (i in 0..path.size-1){
            dots[i] = ImageView(context)
            if(i == position){
                dots[i].setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.active_dots))
            }
            else{
                dots[i].setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.inactive_dots))
            }

            val params : LinearLayout.LayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(4,0,4,0)
            dots_layout.addView(dots[i], params)

        }
    }
}
