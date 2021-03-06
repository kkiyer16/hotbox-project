package com.example.menulayout

//import android.support.v7.app.AppCompatActivity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_sign_up.*

private const val TAG = "MyActivity"

class SignUpActivity : AppCompatActivity() {

    lateinit var fstore : FirebaseFirestore
    lateinit var authen : FirebaseAuth
    lateinit var docRef : DocumentReference
    private val fStore = FirebaseFirestore.getInstance()
    private val adminID = "F0y2F2SeaoWHjY7sIHFr4JRf1HF2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        authen = FirebaseAuth.getInstance()

        fstore = FirebaseFirestore.getInstance()

        signupbtn.setOnClickListener {
            //startActivity(Intent(this, MainActivity::class.java))
            signUp()
        }
    }

    private fun signUp(){
        val fname = findViewById<View>(R.id.fullname) as EditText
        val uname = findViewById<View>(R.id.username) as EditText
        val emails = findViewById<View>(R.id.email_id) as EditText
        val pass = findViewById<View>(R.id.password) as EditText

        val fn = fname.text.toString()
        val un = uname.text.toString()
        val em = emails.text.toString()
        val ps = pass.text.toString()
        val mob = mobilenumber.text.toString()

        if(fn.isEmpty() || un.isEmpty() || em.isEmpty() || ps.isEmpty() || mob.isEmpty()){
            Toast.makeText(this, "Enter Required Credentials ", Toast.LENGTH_LONG ).show()
        }

        if (TextUtils.isEmpty(fn)){
            fname.error = "Fullname is Required"
            return
        }

        if (TextUtils.isEmpty(un)){
            uname.error = "Username is Required"
            return
        }

        if (TextUtils.isEmpty(ps)){
            pass.error = "Password is Required"
            return
        }

        if (ps.length < 6){
            pass.error = "Password must be at least 6 characters"
            return
        }

        if (TextUtils.isEmpty(em)){
            emails.error = "Email ID is Required"
            return
        }

        if (TextUtils.isEmpty(mob)){
            mobilenumber.error = "Mobile Number is Required"
            return
        }

        if(mob.length > 10 || mob.length < 10){
            mobilenumber.error = "Invalid Mobile Number Format"
            return
        }

        authen.createUserWithEmailAndPassword(em, ps)
            .addOnCompleteListener(this) {
                if(it.isSuccessful){
                    val usernew = HashMap<String, Any>()
                    usernew["FullName"] = fn
                    usernew["UserName"] = un
                    usernew["Email_ID"] = em
                    usernew["mobilenumber"] = mob
//                    usernew["Password"] = ps
                    docRef = fstore.collection("HotBox").document(FirebaseAuth.getInstance().uid.toString())
                        .collection("Users").document("PersonalDetails")
                    docRef.set(usernew, SetOptions.merge())
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                fStore.collection("Users").document("AllUsers").get()
                                    .addOnSuccessListener { data->
                                        if (data.exists()){
                                            fStore.collection("Users").document("AllUsers")
                                                .update(
                                                    "TotalList",
                                                    FieldValue.arrayUnion(FirebaseAuth.getInstance().uid.toString())
                                                ).addOnSuccessListener {
                                                    Log.d("ffd", FirebaseAuth.getInstance().uid.toString())
                                                }
                                        }
                                        else{
                                            fStore.collection("Users").document("AllUsers")
                                                .set(
                                                    hashMapOf(
                                                        "TotalList" to arrayListOf(FirebaseAuth.getInstance().uid.toString())
                                                    )
                                                ).addOnSuccessListener {
                                                    Log.d("ffd", FirebaseAuth.getInstance().uid.toString())
                                                }
                                        }
                                    }
                            }
                        }
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registered Successfully to HotBox!!", Toast.LENGTH_LONG).show()
                            Toast.makeText(this, "Welcome to HotBox!!", Toast.LENGTH_LONG).show()
                            Log.d(TAG, "UID:${FirebaseAuth.getInstance().uid}")
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                        .addOnFailureListener {
                            Log.w(TAG, "Error Adding document")
                            Toast.makeText(this, "Failed to Register", Toast.LENGTH_LONG ).show()
                        }

                    return@addOnCompleteListener
                }else{
                    Log.w(TAG, "Error Adding document")
                    Toast.makeText(this, "Failed to Register to Database", Toast.LENGTH_LONG ).show()
                }
            }
    }
}