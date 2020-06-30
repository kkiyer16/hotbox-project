package com.example.menulayout

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.menulayout.checksum.CheckSumServiceHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import kotlinx.android.synthetic.main.activity_payment.*
import java.util.*
import kotlin.collections.HashMap

class PaymentActivity : AppCompatActivity() {

    private val fStore = FirebaseFirestore.getInstance()
    private val userid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        supportActionBar!!.title = "Select Payment"
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS),
                101
            )
        }

        display_payment_amt.text = intent.extras!!.getString("amt")!!

        payment_mode_paytm.setOnClickListener {
            payUsingPaytm()
        }

        payment_mode_cod.setOnClickListener {
            payUsingCOD()
        }

    }

    private fun payUsingPaytm() {
        val instance = this
        val mContext = applicationContext
        val service = PaytmPGService.getStagingService("")
        val orderId = "order" + (0..99999).random()
        val custId = "cust" + (0..99999).random()
        val amt = intent.extras!!.getString("amt")!!
        Log.d("amtc", amt)
        val paramMap = HashMap<String, String>()
        Log.d("orderid", orderId)
        Log.d("custid", custId)
        paramMap["MID"] = Keys.MID
        paramMap["ORDER_ID"] = orderId
        paramMap["WEBSITE"] = "WEBSTAGING"
        paramMap["INDUSTRY_TYPE_ID"] = "Retail"
        paramMap["CHANNEL_ID"] = "WAP"
        paramMap["CUST_ID"] = custId
        paramMap["TXN_AMOUNT"] = amt
        paramMap["CALLBACK_URL"] = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=${orderId}"

        val paramMap2 = TreeMap<String, String>()
        paramMap2.putAll(paramMap)
        val CHECKSUMHASH = CheckSumServiceHelper().genrateCheckSum(Keys.MID_KEY, paramMap2)
        paramMap["CHECKSUMHASH"] = CHECKSUMHASH
        val Order = PaytmOrder(paramMap)
        service.initialize(Order, null)
        service.startPaymentTransaction(instance,
            true,
            true,
            object : PaytmPaymentTransactionCallback {
                override fun onTransactionResponse(inResponse: Bundle?) {
                    Toast.makeText(mContext, "Payment Transaction Response ${inResponse.toString()}", Toast.LENGTH_LONG).show()
                }

                override fun clientAuthenticationFailed(inErrorMessage: String?) {
                    Toast.makeText(mContext, "Authentication Failed  ${inErrorMessage.toString()}", Toast.LENGTH_LONG).show()
                }

                override fun someUIErrorOccurred(inErrorMessage: String?) {
                    Toast.makeText(mContext, "UI Error $inErrorMessage", Toast.LENGTH_LONG).show()
                }

                override fun onTransactionCancel(inErrorMessage: String?, inResponse: Bundle?) {
                    Toast.makeText(mContext, "Transaction Cancelled", Toast.LENGTH_LONG).show()
                }

                override fun networkNotAvailable() {
                    Toast.makeText(mContext, "Check Your Network Connection", Toast.LENGTH_LONG).show()
                }

                override fun onErrorLoadingWebPage(
                    iniErrorCode: Int,
                    inErrorMessage: String?,
                    inFailingUrl: String?
                ) {
                    Toast.makeText(mContext, "Unable to Load Page $inErrorMessage", Toast.LENGTH_LONG).show()
                }

                override fun onBackPressedCancelTransaction() {
                }

            })
    }

    private fun payUsingCOD() {
        val cod = HashMap<String, Any>()
        cod["paymentmode"] = "COD"
        val ref1 = fStore.collection("HotBox").document(userid).collection("CateringOrders")
        ref1.addSnapshotListener { snapshot, e ->
            for(ds in snapshot!!.documents){
                fStore.collection("HotBox").document(userid).collection("CateringOrders").document(ds.id)
                    .update(cod)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            fStore.collection("Orders").document(userid).collection("CateringOrders")
                                .document(ds.id).update(cod)
                                .addOnCompleteListener {
                                    if (it.isSuccessful){
                                        Toast.makeText(this, "Order Placed Successfully", Toast.LENGTH_LONG).show()
                                        startActivity(Intent(applicationContext, MainActivity::class.java))
                                        Log.d("cnf", "Order Placed Successfully")
                                    }
                                }
                        }
                    }
            }
        }
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
