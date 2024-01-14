package edu.bluejack_231.cinemax231.user

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.model.Movie
import edu.bluejack_231.cinemax231.user.adapter.UserMovieTimeSelectionAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class UserMovieTimeSelectionActivity : AppCompatActivity() {

    lateinit var imgv: ImageView
    lateinit var name: TextView
    lateinit var toolbar: Toolbar
    lateinit var rcv: RecyclerView
    lateinit var adapter: UserMovieTimeSelectionAdapter
    lateinit var schedules: ArrayList<String>
    lateinit var movie: Movie

    fun init(){
        imgv = findViewById(R.id.user_movie_time_selection_imgv)
        name = findViewById(R.id.user_movie_time_selection_name)
        toolbar = findViewById(R.id.appbar)
        rcv = findViewById(R.id.user_movie_time_selection_rcv)
        schedules = ArrayList<String>()
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcv.layoutManager = layoutManager
    }

    fun getSchedule(){
        val sharedPreferences = getSharedPreferences("MovieDetails", Context.MODE_PRIVATE)
        val db = FirebaseFirestore.getInstance()
        val movieName = sharedPreferences.getString("movieName", "")
        db.collection("movies")
            .document(movieName.toString())
            .collection("schedules")
            .get()
            .addOnSuccessListener {document ->
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                schedules.clear()
                for(doc in document){
                    val schedule = doc.getString("date")
                    Log.d("Date", schedule.toString())
                    schedules.add(schedule.toString())
                }
                schedules.sortWith(Comparator { date1, date2 ->
                    try {
                        val parsedDate1 = dateFormat.parse(date1)
                        val parsedDate2 = dateFormat.parse(date2)
                        if (parsedDate1 != null && parsedDate2 != null) {
                            return@Comparator parsedDate1.compareTo(parsedDate2)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return@Comparator 0
                })
                adapter = UserMovieTimeSelectionAdapter(movie, schedules)
                rcv.adapter = adapter
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_user_movie_time_selection)
        init()
        val extras = intent.extras
        if (extras != null) {
            saveToSharedPreferences(extras)
        }
        loadFromSharedPreferences()
        getSchedule()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }

    private fun saveToSharedPreferences(extras: Bundle) {
        val sharedPreferences = getSharedPreferences("MovieDetails", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("movieImage", extras.getString("movieImage"))
            putString("movieName", extras.getString("movieName"))
            putInt("movieDuration", extras.getInt("movieDuration"))
            putString("movieGenre", extras.getString("movieGenre"))
            putString("movieLanguage", extras.getString("movieLanguage"))
            putString("movieDescription", extras.getString("movieDescription"))
            putInt("moviePrice", extras.getInt("moviePrice"))
            apply()
        }
    }

    private fun loadFromSharedPreferences() {
        val sharedPreferences = getSharedPreferences("MovieDetails", Context.MODE_PRIVATE)
        Picasso.get().load(sharedPreferences.getString("movieImage", "")).into(imgv)
        name.setText(sharedPreferences.getString("movieName", ""))

        movie = Movie(
            sharedPreferences.getString("movieName", "").toString(),
            sharedPreferences.getInt("movieDuration", 0).toInt(),
            sharedPreferences.getString("movieGenre", "").toString(),
            sharedPreferences.getString("movieLanguage", "").toString(),
            sharedPreferences.getString("movieDescription", "").toString(),
            sharedPreferences.getInt("moviePrice", 0).toInt(),
            sharedPreferences.getString("movieImage", "").toString(),
            sharedPreferences.getString("movieRating", "").toString()
        )

    }
}