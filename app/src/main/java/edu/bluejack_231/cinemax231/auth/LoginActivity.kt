package edu.bluejack_231.cinemax231.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import at.favre.lib.crypto.bcrypt.BCrypt
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.admin.AdminDashboardActivity
import edu.bluejack_231.cinemax231.user.UserNavigationActivity
import edu.bluejack_231.cinemax231.utility.BaseActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : BaseActivity() {

    lateinit var usernameedt: EditText
    lateinit var passwordedt:EditText
    lateinit var loginBtn: MaterialButton
    lateinit var registerBtn: LinearLayout
    lateinit var forgetBtn: TextView

    fun init(){
        usernameedt = findViewById(R.id.username)
        passwordedt = findViewById(R.id.password)
        loginBtn = findViewById(R.id.loginBtn)
        registerBtn = findViewById(R.id.registerBtn)
        forgetBtn = findViewById(R.id.forgotPassword)
    }

    fun setEvent(){
        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        loginBtn.setOnClickListener{
            val username = usernameedt.text.toString()
            val password = passwordedt.text.toString()
            if(username.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "All Fields Must Be Filled", Toast.LENGTH_SHORT).show()
            }
            else{
                if(
                    username.toString().compareTo("admin") == 0
                    &&
                    password.toString().compareTo("admin") == 0)
                {
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                    finish()
                }
                else{

                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").whereEqualTo("username", username).get().addOnSuccessListener {doc ->
                        if(!doc.isEmpty){
                            val user = doc.documents[0].data!!
                            if (user != null) {
                                val storedPassword = user["password"]
                                val phone = user["phone"].toString()
                                val res = BCrypt.verifyer().verify(password.toString().toCharArray(), storedPassword.toString())
                                if(res.verified){
                                    val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
                                    with(sharedPreferences.edit()) {
                                        println("id = " + user["id"].toString())
                                        putString("id", user["id"].toString())
                                        putString("username", username)
                                        putString("phone", phone.toString())
                                        apply()
                                    }
                                    startActivity(Intent(this, UserNavigationActivity::class.java))
                                    finish()
                                }
                                else{
                                    Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        else{
                            Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                        }
                    }
                        .addOnFailureListener{
                            Toast.makeText(this, "Error Logging In", Toast.LENGTH_SHORT).show()
                        }
                }
            }

        }
        forgetBtn.setOnClickListener {
            startActivityForResult(Intent(this, ForgotPasswordActivity::class.java), 1)
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
        setContentView(R.layout.activity_login)
        init()
        setEvent()
    }
}