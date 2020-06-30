package com.example.menulayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_prof_change_pwd.*

class ProfChangePwd : AppCompatActivity() {

    private var TAG = "MyProfileActivity"
    lateinit var auth : FirebaseAuth
    lateinit var fStore : FirebaseFirestore
    private val userid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val adminID = "F0y2F2SeaoWHjY7sIHFr4JRf1HF2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prof_change_pwd)

        val actionBar = supportActionBar
        actionBar!!.title = "Change Password"

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        layout_password.visibility = View.VISIBLE
        layout_update_password.visibility = View.GONE

        btn_authenticate_password.setOnClickListener {
            val pwd = et_old_password.text.toString().trim()

            if(pwd.isEmpty()){
                et_old_password.error = "Password Required"
                et_old_password.requestFocus()
                return@setOnClickListener
            }

            currentUser.let { user->
                val credential = EmailAuthProvider.getCredential(user?.email!!, pwd)
                progress_bar_profile.visibility = View.VISIBLE
                user.reauthenticate(credential)
                    .addOnCompleteListener { task->
                        progress_bar_profile.visibility = View.GONE
                        when{
                            task.isSuccessful->{
                                layout_password.visibility = View.GONE
                                layout_update_password.visibility = View.VISIBLE
                            }
                            task.exception is FirebaseAuthInvalidCredentialsException ->{
                                et_old_password.error = "Invalid Password"
                                Toast.makeText(this, "Entered Password is Invalid", Toast.LENGTH_LONG).show()
                                et_old_password.requestFocus()
                            }
                            else -> Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        btn_update_password.setOnClickListener {
            val password = et_new_password.text.toString().trim()
            val cnfmpassword = et_confirm_password.text.toString().trim()

            if (password.isEmpty()){
                et_new_password.error = "Enter New password"
                et_new_password.requestFocus()
                return@setOnClickListener
            }
            if (password.length<6){
                et_new_password.error = "Password must be atleast 6 char"
                et_new_password.requestFocus()
                return@setOnClickListener
            }
            if(password != cnfmpassword){
                et_confirm_password.error = "Password did not match"
                et_confirm_password.requestFocus()
                return@setOnClickListener
            }

            currentUser?.let { user->
                progress_bar_profile.visibility = View.VISIBLE
                user.updatePassword(password)
                    .addOnCompleteListener { task->
                        if (task.isSuccessful){
                            Toast.makeText(this, "Password Updated", Toast.LENGTH_LONG).show()
                            progress_bar_profile.visibility = View.INVISIBLE
                        }else{
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
