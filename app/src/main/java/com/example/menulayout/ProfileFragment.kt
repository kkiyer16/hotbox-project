package com.example.menulayout


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.provider.MediaStore
//import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream


//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// *
// */
class ProfileFragment : Fragment() {

    lateinit var CirleImageView : CircularImageView
    private val REQUEST_IMAGE_CAPTURE = 100
    private lateinit var uri : Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_circular_image_view.setOnClickListener {
            takePictureIntent()
        }
    }

    private fun takePictureIntent(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {pictureIntent->
            pictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadImageAndSaveUri(imageBitmap)
        }
    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap){
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("Profile Pics/${FirebaseAuth.getInstance().currentUser?.uid}")

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        val upload = storageRef.putBytes(image)

        progress_bar_save.visibility = View.VISIBLE

        upload.addOnCompleteListener { UploadTask->
            progress_bar_save.visibility = View.INVISIBLE
            if(UploadTask.isSuccessful){
                storageRef.downloadUrl.addOnCompleteListener { urlTask->
                    urlTask.result?.let {
                        uri = it
                        Toast.makeText(activity, uri.toString(), Toast.LENGTH_LONG).show()
                        profile_circular_image_view.setImageBitmap(bitmap)
                    }
                }
            }else{
                UploadTask.exception?.let {
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
