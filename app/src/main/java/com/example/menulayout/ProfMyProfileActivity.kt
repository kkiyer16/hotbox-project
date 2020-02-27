package com.example.menulayout

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_prof_my_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class ProfMyProfileActivity : AppCompatActivity() {

    lateinit var fStore : FirebaseFirestore
    lateinit var fAuth : FirebaseAuth
    private val userid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prof_my_profile)

        val actionBar = supportActionBar
        actionBar!!.title = "My Profile"

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        btn_profile_update.setOnClickListener {
            update_details_to_database()
            finish()
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        image_view_calendar.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, i, i2, i3 ->
                edit_profile_dob.setText(""+ i3 +"/"+ (i2+1) +"/"+ i)
            }, year, month, day)
            dpd.show()
        }
    }

    private fun update_details_to_database(){
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        val namepro = findViewById<EditText>(R.id.edit_profile_name)
        val usernamepro = findViewById<EditText>(R.id.edit_profile_username)
        val dobpro = findViewById<EditText>(R.id.edit_profile_dob)

        val np = namepro.text.toString()
        val up = usernamepro.text.toString()
        val dobp = dobpro.text.toString()

        if(TextUtils.isEmpty(np)){ namepro.error = "Should not be Empty"}
        if(TextUtils.isEmpty(up)){ usernamepro.error = "Should not be Empty"}
        if(TextUtils.isEmpty(dobp)){ dobpro.error = "Should not be Empty"}

        if (np.isEmpty() || up.isEmpty() || dobp.isEmpty()){
            Toast.makeText(this, "Enter Required Credentials", Toast.LENGTH_LONG).show()
        }
        else{
                try {
                    val docRef = fStore.collection("HotBox")
                        .document(userid)
                        .collection("Users").document("Personal Details")
                    docRef.update("FullName", np)
                    docRef.update("UserName", up)
                    add_dob_to_database()
                    Toast.makeText(this, "Data Updated Successfully", Toast.LENGTH_LONG).show()

                }catch (e: Exception){
                    Log.d("Main",e.toString())
                }
        }
    }

    private fun add_dob_to_database(){
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        val dobpro = findViewById<EditText>(R.id.edit_profile_dob)

        val dobp = dobpro.text.toString()


        try {
            val userData = HashMap<String, Any>()
            userData["DOB"] = dobp

            val docRef = fStore.collection("HotBox")
                .document(userid)
                .collection("Users").document("Personal Details")
            docRef.set(userData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, "DOB Added Successfully", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to Add", Toast.LENGTH_LONG).show()
                    Log.d("Main", e.toString())
                }

        }catch (e: Exception){
            Log.d("Main",e.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
