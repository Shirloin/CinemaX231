package edu.bluejack_231.cinemax231.user

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.squareup.picasso.Picasso
import edu.bluejack_231.cinemax231.R

class UserTransactionDetailActivity : AppCompatActivity() {

    private lateinit var imgv:ImageView
    private lateinit var name: TextView
    private lateinit var date: TextView
    private lateinit var time: TextView
    private lateinit var tr_date: TextView
    private lateinit var seats: TextView
    private lateinit var price: TextView
    private lateinit var total: TextView
    private lateinit var qty: TextView
    private lateinit var toolbar: Toolbar
    fun init(){
        imgv = findViewById(R.id.movieImgv)
        name = findViewById(R.id.movieName)
        date = findViewById(R.id.movieDate)
        time = findViewById(R.id.movieTime)
        tr_date = findViewById(R.id.transaction_date)
        seats = findViewById(R.id.seats)
        price = findViewById(R.id.moviePrice)
        total = findViewById(R.id.total)
        qty = findViewById(R.id.qty)
        toolbar = findViewById(R.id.appbar)
    }

    private fun loadData(extras: Bundle) {
        Picasso.get().load(extras.getString("movieImage")).into(imgv)
        name.setText(extras.getString("movieName"))
        date.setText(extras.getString("movieSchedule"))
        time.setText(extras.getString("movieTime"))
        tr_date.setText(extras.getString("date") + " " + extras.getString("time"))
        qty.setText(extras.getInt("quantity").toString())
        price.setText(extras.getInt("moviePrice").toString())
        total.setText(extras.getInt("total").toString())
        val seatsList = extras.getString("seats")
//        val seatsString = seatsList?.joinToString(separator = ", ") ?: ""
//        Log.d("seat", seatsString)
        seats.setText(seatsList)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_user_transaction_detail)
        init()
        val extras = intent.extras
        if (extras != null) {
            loadData(extras)
        }
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}