package com.example.menulayout

import android.os.Bundle
import android.util.Log
//import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_notification.*

class NotificationFragment : Fragment() {

    private val fStore = FirebaseFirestore.getInstance()
    lateinit var recylerView: RecyclerView
    lateinit var notifAdapter: NotifAdapter
    private val mArrayList : ArrayList<ModelNotif> = ArrayList()
    private val userID = FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_notification, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recylerView = view.findViewById(R.id.notification_recycler_view)
        recylerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recylerView.layoutManager = layoutManager
        notifAdapter = NotifAdapter(activity!!.applicationContext, mArrayList)
        recylerView.adapter = notifAdapter

        val ref = fStore.collection("HotBox").document(userID).collection("Notification")
        ref.get().addOnSuccessListener {
            if (it.isEmpty){
                dont_have_notifications_yet.visibility = View.VISIBLE
            }
            else{
                ref.addSnapshotListener { snap, e ->
                    if (snap!=null){
                        for (i in snap.documentChanges){
                            if (i.type == DocumentChange.Type.ADDED){
                                if (i.document.exists()){
                                    try {
                                        val types = ModelNotif(
                                            i.document.getString("foodcategory")!!,
                                            i.document.getString("foodname")!!,
                                            i.document.getString("notifmsg")!!
                                        )
                                        mArrayList.add(types)
                                    }catch (e: Exception){
                                        e.printStackTrace()
                                        Log.d("e", e.message.toString().trim())
                                    }
                                }
                            }
                        }
                        notifAdapter.update(mArrayList)
                    }
                }
            }
        }
    }
}
