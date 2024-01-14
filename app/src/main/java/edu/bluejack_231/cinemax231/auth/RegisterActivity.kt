package edu.bluejack_231.cinemax231.auth

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import at.favre.lib.crypto.bcrypt.BCrypt
import edu.bluejack_231.cinemax231.utility.BaseActivity

import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack_231.cinemax231.R
import java.util.*

class RegisterActivity : BaseActivity() {
    lateinit var usernameedt: EditText
    lateinit var passwordedt: EditText
    lateinit var phoneedt: EditText
    lateinit var registerBtn: MaterialButton
    lateinit var loginBtn: LinearLayout

    fun init(){
        usernameedt = findViewById(R.id.username)
        passwordedt = findViewById(R.id.password)
        phoneedt = findViewById(R.id.phone)
        registerBtn = findViewById(R.id.registerBtn)
        loginBtn = findViewById(R.id.loginBtn)
    }

    fun setEvent(){
        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        registerBtn.setOnClickListener{
            var username = usernameedt.text
            var password = passwordedt.text
            var phone = phoneedt.text

            if(username.isEmpty() || password.isEmpty() || phone.isEmpty()){
                Toast.makeText(this, "All Fields Must Be Filled", Toast.LENGTH_SHORT).show()
            }
            else if(username.length < 4){
                Toast.makeText(this, "Username Length must more than 3", Toast.LENGTH_SHORT).show()
            }
            else if(!password.contains(Regex("[0-9]"))){
                Toast.makeText(this, "Password must contain at least 1 number", Toast.LENGTH_SHORT).show()
            }
            else{

                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(username.toString()).get().addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        Toast.makeText(this, "Username Already Existed", Toast.LENGTH_SHORT).show()
                    } else {
                        db.collection("users").whereEqualTo("phone", phone.toString()).get()
                            .addOnSuccessListener { result ->
                                if (!result.isEmpty) {
                                    Toast.makeText(
                                        this,
                                        "Phone Number Already Existed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    var uuid = UUID.randomUUID().toString()
                                    val SALT_ROUNDS = 6
                                    val hash = BCrypt.withDefaults().hashToString(12, password.toString().toCharArray())
                                    val user = hashMapOf(
                                        "id" to uuid,
                                        "username" to username.toString(),
                                        "password" to hash.toString(),
                                        "phone" to phone.toString()
                                    )
                                    db.collection("users").document(uuid).set(user)
                                        .addOnSuccessListener {
                                            startActivity(Intent(this, LoginActivity::class.java))
                                            finish()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                this,
                                                "Error While Register",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }.addOnFailureListener { e ->
                            Toast.makeText(this, "Error Checking Phone Number", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        this.window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
//        supportActionBar!!.hide()
        setContentView(R.layout.activity_register)
        init()
        setEvent()
    }
}