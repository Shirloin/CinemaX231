package edu.bluejack_231.cinemax231.auth

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import edu.bluejack_231.cinemax231.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import org.apache.commons.lang3.RandomStringUtils

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var phone: EditText
    lateinit var btn: MaterialButton
    lateinit var dialog: Dialog
    lateinit var toolbar: Toolbar

    lateinit var confirmBtn: MaterialButton
    lateinit var codeedt: EditText

    var code = ""
    var newPass = ""
    val db = FirebaseFirestore.getInstance()
    fun init(){
        phone = findViewById(R.id.phone)
        btn = findViewById(R.id.sendBtn)
        dialog = Dialog(this)
        toolbar = findViewById(R.id.appbar)
    }

    fun setEvent(){

        btn.setOnClickListener{
            val number = phone.text.toString()
            db.collection("users").whereEqualTo("phone", number).get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val doc = querySnapshot.documents[0]
                    if(doc.exists()){
                        checkSendSMS()
                        dialog.setContentView(R.layout.modal_confirmation_code)
                        codeedt = dialog.findViewById(R.id.code)
                        confirmBtn = dialog.findViewById(R.id.confirmBtn)
                        setConfirmEvent()
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        val displayMetrics = resources.displayMetrics
                        val width = displayMetrics.widthPixels
                        dialog.window?.setLayout((width * 0.9).toInt(), WindowManager.LayoutParams.WRAP_CONTENT) // 90% screen width
                        dialog.show()
                    }
                    else{
                        Toast.makeText(this, "Phone Number Is Not Registered", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun setConfirmEvent(){
        confirmBtn.setOnClickListener {
            val inputCode = codeedt.text
            if(code.toString() != inputCode.toString()){
                Toast.makeText(this, "Wrong Confirmation Code", Toast.LENGTH_SHORT).show()
            }
            else{
                val number = phone.text.toString()
                db.collection("users").whereEqualTo("phone", number).get().addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val doc = querySnapshot.documents[0]
                        doc.reference.update("password", newPass)
                            .addOnSuccessListener {
                                val intent = Intent(this, LoginActivity::class.java)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error While Reset Password", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Phone Number Not Found", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Error Retrieving User", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun checkSendSMS(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), 1)
        }
        else{
            sendSMS()
        }
    }

    fun sendSMS(){
        val smsManager = SmsManager.getDefault()
        val number = phone.text.toString()
        newPass = getRandomString()
        code = getRandomString()
        Log.d("Code", code)
        val msg = "Here is your new password " + newPass + " and here is your confirmation code " + code
        smsManager.sendTextMessage(number.toString(), null, msg, null, null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), 1)
        }
    }
    fun getRandomString(): String {
        return RandomStringUtils.randomAlphanumeric(10)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        init()
        setEvent()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onDestroy() {
        if(dialog.isShowing){
            dialog.dismiss()
        }
        super.onDestroy()
    }
}