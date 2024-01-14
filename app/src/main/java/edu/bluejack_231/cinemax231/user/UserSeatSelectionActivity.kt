package edu.bluejack_231.cinemax231.user

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import edu.bluejack_231.cinemax231.R
import com.google.firebase.firestore.FirebaseFirestore
import dev.jahidhasanco.seatbookview.SeatBookView
import dev.jahidhasanco.seatbookview.SeatClickListener
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class UserSeatSelectionActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var seatTotal: TextView
    lateinit var totalPrice: TextView
    lateinit var bookBtn: Button
    lateinit var seatLayout: LinearLayout
    var start = true

    private var defaultSeats = (
            "/AAAAA" +
                    "/AAAAA" +
                    "/AAAAA" +
                    "/AAAAA" +
                    "/AAAAA" +
                    "/AAAAA" +
                    "/AAAAA" +
                    "/AAAAA" +
                    "/AAAAA" +
                    "/AAAAA"
            )

    private var title = listOf(
        "/", "A1", "A2", "A3", "A4", "A5",
        "/", "B1", "B2", "B3", "B4", "B5",
        "/", "C1", "C2", "C3", "C4", "C5",
        "/", "D1", "D2", "D3", "D4", "D5",
        "/", "E1", "E2", "E3", "E4", "E5",
        "/", "F1", "F2", "F3", "F4", "F5",
        "/", "G1", "G2", "G3", "G4", "G5",
        "/", "H1", "H2", "H3", "H4", "H5",
        "/", "I1", "I2", "I3", "I4", "I5",
        "/", "J1", "J2", "J3", "J4", "J5"
    )
    private lateinit var sbv: SeatBookView
    var seatCount = 0
    var priceCount = 0
    lateinit var selectedSeat: ArrayList<Int>
    lateinit var seats: String

    fun init(){
        toolbar = findViewById(R.id.appbar)
        seatTotal = findViewById(R.id.seat_total_quantity)
        totalPrice = findViewById(R.id.seat_total_price)
        bookBtn = findViewById(R.id.seat_book_btn)
        sbv = SeatBookView(this)
//        sbv = findViewById(R.id.layoutSeat)
        seatLayout = findViewById(R.id.seatLayout)

        selectedSeat = ArrayList<Int>()
        seats = ""
    }

    fun setSeatBookView(){
        this.sbv = SeatBookView(this)
        seatLayout.removeAllViews()
        sbv.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        val padding = (20 * resources.displayMetrics.density).toInt()
        val hor = (10 * resources.displayMetrics.density).toInt()
        sbv.setPadding(hor, padding, hor, padding)
        sbv.setSeatGaping(15)
        sbv.setSeatTextSize(50f)
//        sbv.setSeatTextSize(resources.getDimension(R.dimen.seat_text_size))
        sbv.setAvailableSeatsTextColor(ContextCompat.getColor(this, R.color.white))
        sbv.setBookedSeatsTextColor(ContextCompat.getColor(this, R.color.white))
        sbv.setReservedSeatsTextColor(ContextCompat.getColor(this, R.color.white))
        sbv.setAvailableSeatsBackground(R.drawable.seat_available)
        sbv.setSelectedSeatsBackground(R.drawable.seat_selected)
        sbv.setBookedSeatsBackground(R.drawable.seat_booked)
        sbv.setReservedSeatsBackground(R.drawable.seat_reserved)
        sbv.setSeatsLayoutString(seats)
            .isCustomTitle(true)
            .setCustomTitle(title)
            .setSeatLayoutPadding(2)
            .setSeatSizeBySeatsColumnAndLayoutWidth(5, -1)
        seatLayout.addView(sbv)

        sbv.show()
        setEvent()
    }

    fun setItem(){
        seatTotal.setText(seatCount.toString())
        totalPrice.setText(priceCount.toString())



        bookBtn.setOnClickListener {
            if(seatCount == 0 && priceCount == 0 && this.selectedSeat.size==0){
                return@setOnClickListener
            }
            val extras = intent.extras
            if(extras!=null){
                val movieName = extras.getString("movieName", "").toString()
                val movieImage = extras.getString("movieImage", "").toString()
                val movieDuration = extras.getInt("movieDuration", 0).toInt()
                val movieGenre = extras.getString("movieGenre", "").toString()
                val movieLanguage = extras.getString("movieLanguage", "").toString()
                val movieDescription = extras.getString("movieDescription", "").toString()
                val moviePrice = extras.getInt("moviePrice", 0).toInt()
                val schedule = extras.getString("movieSchedule", "").toString()
                val time = extras.getString("movieTime", "").toString()
                val db = FirebaseFirestore.getInstance()

                var updatedSeats = seatsFromSelected(2)
                var seats = hashMapOf(
                    "seats" to updatedSeats.toString()
                )
                val doc = db.collection("movies").document(movieName).collection("schedules").document(schedule).collection("time").document(time)

                doc.update("seats", updatedSeats).addOnSuccessListener {

                    val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
                    val username = sharedPreferences.getString("username", "").toString()
                    val id = sharedPreferences.getString("id", "").toString()
                    val movie = hashMapOf(
                        "name" to movieName,
                        "duration" to movieDuration,
                        "genre" to movieGenre,
                        "language" to movieLanguage,
                        "description" to movieDescription,
                        "price" to moviePrice,
                        "image" to movieImage,
                        "date" to schedule,
                        "time" to time
                    )
                    val transaction = hashMapOf(
                        "quantity" to selectedSeat.size.toString(),
                        "date" to getCurrentDate(),
                        "time" to getCurrentTime(),
                        "status" to "request",
                        "seat" to selectedSeat,
                        "username" to username,
                        "id" to id
                    )
                    var uuid = UUID.randomUUID().toString()

                    db.collection("transactions").document(uuid).set(transaction)
                        .addOnSuccessListener {
                            db.collection("transactions").document(uuid).collection("movie").document(movieName).set(movie).addOnSuccessListener { movieDoc ->
                                selectedSeat.clear()
                                seatTotal.setText("0")
                                totalPrice.setText("0")
                                realtime()
                                Toast.makeText(this@UserSeatSelectionActivity, "Success To Book", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, UserNavigationActivity::class.java))
                                finish()
                            }
                        }
                }

            }
        }
    }

    fun setEvent(){
        sbv.setSeatClickListener(object : SeatClickListener {

            override fun onAvailableSeatClick(selectedIdList: List<Int>, view: View) {
                if(view.isSelected){
                    view.isSelected = false
                    seatCount--
                    priceCount--
                    selectedSeat.remove(view.id)
                }
                else{
                    seatsFromSelected(0)
                    view.isSelected = true
                    seatCount++
                    priceCount++
                    selectedSeat.add(view.id)
                }

                val extras = intent.extras
                seatTotal.setText(selectedSeat.size.toString())
                if (extras != null) {
                    totalPrice.setText((selectedSeat.size * extras.getInt("moviePrice")).toString())
                }
            }
            override fun onBookedSeatClick(view: View) {
                Toast.makeText(view.context, "Seat Has Been Booked", Toast.LENGTH_SHORT).show()
            }
            override fun onReservedSeatClick(view: View) {
                Toast.makeText(view.context, "Seat Has Been Reserved", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun setReserved(){
        val extras = intent.extras
        if(extras!=null){
            val movieName = extras.getString("movieName", "").toString()
            val movieImage = extras.getString("movieImage", "").toString()
            val movieDuration = extras.getInt("movieDuration", 0).toInt()
            val movieGenre = extras.getString("movieGenre", "").toString()
            val movieLanguage = extras.getString("movieLanguage", "").toString()
            val movieDescription = extras.getString("movieDescription", "").toString()
            val moviePrice = extras.getInt("moviePrice", 0).toInt()
            val schedule = extras.getString("movieSchedule", "").toString()
            val time = extras.getString("movieTime", "").toString()
            val db = FirebaseFirestore.getInstance()

            var updatedSeats = seatsFromSelected(2)
            var seats = hashMapOf(
                "seats" to updatedSeats.toString()
            )
            val doc = db.collection("movies").document(movieName).collection("schedules").document(schedule).collection("time").document(time)

            doc.update("seats", updatedSeats).addOnSuccessListener {

            }

        }
    }

    fun realtime(){
        val extras = intent.extras
        if (extras != null) {
            val movieName = extras.getString("movieName", "").toString()
            val schedule = extras.getString("movieSchedule", "").toString()
            val time = extras.getString("movieTime", "").toString()
            val db = FirebaseFirestore.getInstance()
            db.collection("movies")
                .document(movieName)
                .collection("schedules")
                .document(schedule)
                .collection("time")
                .document(time)
                .addSnapshotListener { querySnapshot, error ->
                    error?.let {
                        return@addSnapshotListener
                    }
                    querySnapshot?.let {doc ->
                        if (doc.exists()) {
                            val currSeats = doc.getString("seats")
                            seats = ""
                            this.seats = currSeats ?: defaultSeats
                        }
                        setSeatBookView()

                    }
                }
        }
    }
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }
    fun getCurrentTime(): String{
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return currentTime.format(formatter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_user_seat_selection)
        init()
        setItem()
        setEvent()
        realtime()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }


    fun seatsFromSelected(status: Int): String{
        val selectedSet = selectedSeat.toSet()
        val rows = seats.split("/")
        val updatedRows = rows.mapIndexed { rowIdx, row ->
            row.mapIndexed{ seatIdx, seatChar ->

                val seatNum = (rowIdx - 1) * 5  + seatIdx + 1
                println("Row Index = " + rowIdx)
                println("Seat Index = " + seatIdx)
                println("Index Number = " + seatNum)
                println("Index = " + selectedSet)
                if(seatChar == 'A' && selectedSeat.contains(seatNum) && status == 1){
                    'U'
                }
                else if(seatChar == 'A' && selectedSeat.contains(seatNum) && status == 2){
                    'R'
                }
                else{
                    seatChar
                }
            }.joinToString("")
        }
        return updatedRows.joinToString("/")
    }

}