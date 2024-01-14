package edu.bluejack_231.cinemax231

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import edu.bluejack_231.cinemax231.auth.LoginActivity
import edu.bluejack_231.cinemax231.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

//        startActivity(Intent(this, LoginActivity::class.java))
//        finish()

//        installSplashScreen()
        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 1000)

    }

}