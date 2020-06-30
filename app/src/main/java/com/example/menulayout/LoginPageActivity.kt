package com.example.menulayout

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_login.*

class LoginPageActivity : AppCompatActivity() {

    lateinit var db: FirebaseAuth
    lateinit var sh: SharedPreferences
    private val adminID = "F0y2F2SeaoWHjY7sIHFr4JRf1HF2"
    lateinit var signInButton: SignInButton
    private lateinit var signInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1
    private val fStore = FirebaseFirestore.getInstance()
    private val fAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sh = getSharedPreferences("com.example.menulayout", 0)
        db = FirebaseAuth.getInstance()

        signinbtn.setOnClickListener {
            signIn()
        }

        signup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        forgot_password.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        signInClient = GoogleSignIn.getClient(this, gso)
        signInButton = findViewById(R.id.google_sign_in_button)
        signInButton.setOnClickListener {
            googleSignInFun()
        }
    }

    private fun googleSignInFun(){
        val signInIntent = signInClient.signInIntent
        signInClient.signOut()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(comptask: Task<GoogleSignInAccount>?) {
        try{
            val acc = comptask!!.getResult(ApiException::class.java)
            Toast.makeText(applicationContext, "Signed In Successfully", Toast.LENGTH_LONG).show()
            firebaseGoogleAuth(acc)
        }catch (e: ApiException){
            e.printStackTrace()
            Log.d("ex", e.message.toString().trim())
            Toast.makeText(applicationContext, "Signed In Failed", Toast.LENGTH_LONG).show()
            firebaseGoogleAuth(null)
        }
    }

    private fun firebaseGoogleAuth(acct: GoogleSignInAccount?) {
        val authCredential = GoogleAuthProvider.getCredential(acct!!.idToken, null)
        db.signInWithCredential(authCredential).addOnCompleteListener {
            if(it.isSuccessful){
                val firebaseUser = db.currentUser
                updateUI(firebaseUser)
                return@addOnCompleteListener
            }
            else{
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_LONG).show()
                updateUI(null)
            }
        }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (googleSignInAccount != null){
            val personName = googleSignInAccount.displayName.toString().trim()
            val personEmail = googleSignInAccount.email.toString().trim()
            val personUsername = googleSignInAccount.givenName.toString().trim()
            val storeDetails = HashMap<String, Any>()
            storeDetails["FullName"] = personName
            storeDetails["UserName"] = personUsername
            storeDetails["Email_ID"] = personEmail
            val dbRef = fStore.collection("HotBox").document(fAuth.uid.toString())
                .collection("Users").document("PersonalDetails")
            dbRef.set(storeDetails, SetOptions.merge())
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
                        Toast.makeText(applicationContext, "Logged In Successfully", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "Failed", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun signIn() {
        val em = emaillog.text.toString()
        val ps = passlog.text.toString()

        if (em.isEmpty() || ps.isEmpty()) {
            Toast.makeText(this, "Please Enter the Required Credentials ", Toast.LENGTH_LONG).show()
            return
        }
        db.signInWithEmailAndPassword(em, ps)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Main", "Successfully Logged in User: ${it.result?.user?.uid}")
                    Toast.makeText(this, "Welcome to HotBox!!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    return@addOnCompleteListener
                } else {
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

        sh = getSharedPreferences("com.example.menulayout", 0)

        if (FirebaseAuth.getInstance().currentUser != null) {
            if (sh.getBoolean("isadmin", false)) {
//                startActivity(Intent(this, AdminMainActivity::class.java))
//                finish()
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}
