@file:Suppress("DEPRECATION")

package com.example.menulayout

import  android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException
import java.util.*
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import id.zelory.compressor.Compressor
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import kotlin.collections.HashMap

@Suppress("DEPRECATION", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
class ProfileActivity : AppCompatActivity() {

    private var selectedPhotoUri : Uri? = null
    lateinit var fAuth : FirebaseAuth
    lateinit var fStore: FirebaseFirestore
    private var storage : FirebaseStorage? = null
    private var PICK_IMAGE_REQUEST = 1234
    private var coms : Bitmap? = null
    private var storageReference : StorageReference? = null
    lateinit var byteArrayOutputStream: ByteArrayOutputStream
    lateinit var imgPath : UploadTask
    lateinit var imgData : ByteArray
    private val userid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val adminID = "F0y2F2SeaoWHjY7sIHFr4JRf1HF2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val actionBar = supportActionBar
        actionBar!!.title = "Account"

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        val fName = findViewById<TextView>(R.id.name_of_user_tv)

        currentUser.let {
            if (it!!.isEmailVerified){
                email_id_not_verified.visibility = View.INVISIBLE
            }
            else{
                email_id_not_verified.visibility = View.VISIBLE
            }
        }

        email_id_not_verified.setOnClickListener {
            currentUser!!.sendEmailVerification().addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this, "Verification Email Sent", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this, it.exception!!.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        fStore.collection("HotBox")
            .document(userid)
            .collection("Users").document("PersonalDetails")
            .get().addOnSuccessListener { ds->
                val username = ds.getString("FullName")
                val profile = ds.getString("url").toString()
                val email_id = ds.getString("Email_ID").toString()
                fName.text= username
                email_of_user_tv.text = email_id
                Glide.with(this).load(profile).placeholder(R.drawable.unisex_avatar).dontAnimate()
                    .fitCenter().into(user_profile_image)
            }

        tv_share.setOnClickListener {
            val message = "Download the HotBox App from Playstore and Enjoy the tastes " +
                    "of different cuisines and let us serve you the eternity"

            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.type = "text/plain"

            startActivity(Intent.createChooser(intent, "Share to : "))
        }

        tv_myprofile.setOnClickListener {
            startActivity(Intent(this, ProfMyProfileActivity::class.java))
        }

        tv_changepwd.setOnClickListener {
            startActivity(Intent(this, ProfChangePwd::class.java))
        }

        tv_myaddress.setOnClickListener {
            startActivity(Intent(this, MyAddressActivity::class.java))
        }

        user_profile_image.setOnClickListener {
            //            showFileChooser()
            val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (permission != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
                }
                else{
                    showFileChooser()
                }
            }else{
                showFileChooser()
            }
        }

        upload_profile_image.setOnClickListener {
            //            uploadFile()
            if (selectedPhotoUri != null){
                val newFile = File(selectedPhotoUri!!.path)
                try {
                    coms = Compressor(this).setMaxWidth(125)
                        .setMaxHeight(125)
                        .setQuality(50)
                        .compressToBitmap(newFile)
                }catch(e : IOException){
                    e.printStackTrace()
                }
                byteArrayOutputStream = ByteArrayOutputStream()
                coms?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                imgData = byteArrayOutputStream.toByteArray()
                imgPath = storageReference!!.child(FirebaseAuth.getInstance().uid.toString()).putBytes(imgData)
                imgPath.addOnCompleteListener {
                    if (it.isSuccessful){
                        storeData(it as UploadTask)
                    }else{
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun storeData(task: UploadTask){
        try {
            task.result.storage.downloadUrl.addOnSuccessListener { uri->
                val userData = HashMap<String, Any>()
                userData["url"] = uri.toString()
                val ref = fStore.collection("HotBox").document(userid)
                    .collection("Users").document("PersonalDetails")
                ref.update(userData)
                Toast.makeText(this, "Uploaded Successfully to Database", Toast.LENGTH_LONG).show()
            }
        }catch (e : Exception){
            Log.d("Main",e.toString())
        }
    }

    private fun showFileChooser(){
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1,1)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result : CropImage.ActivityResult = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK){
                selectedPhotoUri = result.uri
                try {
                    val b: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
//                    user_profile_image!!.setImageBitmap(b)
                    Glide.with(this).load(b).into(user_profile_image)
                }catch (e : IOException){
                    e.printStackTrace()
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                val ex : Exception = result.error
                Log.d("Main", ex.toString())
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
