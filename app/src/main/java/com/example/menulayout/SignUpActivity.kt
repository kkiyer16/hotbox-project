package com.example.menulayout

import android.annotation.SuppressLint
import android.content.Intent
import android.nfc.Tag
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
import kotlin.collections.HashMap

private const val TAG = "MyActivity"

class SignUpActivity : AppCompatActivity() {

    lateinit var fstore : FirebaseFirestore
    lateinit var authen : FirebaseAuth
    lateinit var docRef : DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        authen = FirebaseAuth.getInstance()

        fstore = FirebaseFirestore.getInstance()

        //db = FirebaseFirestore.getInstance()//.document("HotBox Users/Users")

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

        if(fn.isEmpty() || un.isEmpty() || em.isEmpty() || ps.isEmpty()){
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

        authen.createUserWithEmailAndPassword(em, ps)
            .addOnCompleteListener(this) {
                if(it.isSuccessful){
                    //Toast.makeText(this, "Registered Successfully to HotBox!!", Toast.LENGTH_LONG).show()
                    val usernew = HashMap<String, Any>()
                    //usernew["FullName"] = fn
                    usernew["UserName"] = un
                    usernew["Email_ID"] = em
                    usernew["Password"] = ps
                    docRef = fstore.collection("HotBox Users").document("Users").collection(fn)
                        .document("Personal Details")
                    docRef.set(usernew)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registered Successfully to HotBox!!", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, LoginPageActivity::class.java))
                            finish()
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