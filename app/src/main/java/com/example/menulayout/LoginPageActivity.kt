package com.example.menulayout

import android.content.Intent
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginPageActivity : AppCompatActivity() {

    lateinit var db : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = FirebaseAuth.getInstance()

        signinbtn.setOnClickListener {
            //startActivity(Intent(this , MainActivity::class.java))
            signIn()
        }

        signup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun signIn(){
        val em = emaillog.text.toString()
        val ps = passlog.text.toString()

        if(em.isEmpty() || ps.isEmpty()){
            Toast.makeText(this, "Please Enter the Required Credentials ", Toast.LENGTH_LONG ).show()
            return
        }
        db.signInWithEmailAndPassword(em, ps)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //Log.d("Main", "Successfully Logged in User: ${it.result?.user?.uid}")
                    Toast.makeText(this, "Welcome to HotBox!!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    return@addOnCompleteListener
                }
                else {
                    Toast.makeText(this, "Failed to Login", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
                Log.d("Main", " Failed to Login : ${it.message}")
                Toast.makeText(this, "Failed to Login : ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onStart() {
        super.onStart()

        if(FirebaseAuth.getInstance().currentUser != null){
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
