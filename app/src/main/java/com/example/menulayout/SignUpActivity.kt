package com.example.menulayout

import android.annotation.SuppressLint
import android.content.Intent
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
import kotlin.collections.HashMap

class SignUpActivity : AppCompatActivity() {

    /*private lateinit var auth: FirebaseAuth
    lateinit var fname : EditText
    lateinit var usname : EditText
    lateinit var email : EditText
    lateinit var pass : EditText*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        /*auth = FirebaseAuth.getInstance()
        fname = findViewById(R.id.fullname)
        email = findViewById(R.id.email_id)
        pass = findViewById(R.id.password)
        usname = findViewById(R.id.username)*/

        signupbtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            //signUp()
        }
    }
    
    /*private fun signUp(){
        val fn = fname.text.toString()
        val un = usname.text.toString()
        val em = email.text.toString()
        val ps = pass.text.toString()

        if(fn.isEmpty()){
            fname.error = "Please Enter FullName"
            return
        }
        if(un.isEmpty()){
            usname.error = "Please Enter UserName"
            return
        }
        if(em.isEmpty()){
            email.error = "Please Enter Email ID"
            return
        }
        if(ps.isEmpty()){
            pass.error = "Please Enter Password"
            return
        }

        //val user = User(fn,un,em,ps)
        val ref  = FirebaseFirestore.getInstance()


    }*/
}