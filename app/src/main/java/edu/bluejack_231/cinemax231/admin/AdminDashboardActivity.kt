package edu.bluejack_231.cinemax231.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import edu.bluejack_231.cinemax231.R

class AdminDashboardActivity : AppCompatActivity() {
    lateinit var manageMovies: LinearLayout
    lateinit var manageBookings: LinearLayout
    lateinit var bookingHistories: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        manageMovies = findViewById<LinearLayout>(R.id.admin_dashboard_manage_movies)
        manageBookings = findViewById<LinearLayout>(R.id.admin_dashboard_manage_bookings)
        bookingHistories = findViewById<LinearLayout>(R.id.admin_dashboard_booking_histories)

        setEvent()


    }

    fun setEvent() {
        manageMovies.setOnClickListener {
            val intent = Intent(this, MovieViewerActivity::class.java)
            startActivity(intent)
        }

        manageBookings.setOnClickListener {
            val intent = Intent(this, ManageBookingActivity::class.java)
            startActivity(intent)
        }

        bookingHistories.setOnClickListener {
            val intent = Intent(this, BookingHistoryActivity::class.java)
            startActivity(intent)
        }
    }
}