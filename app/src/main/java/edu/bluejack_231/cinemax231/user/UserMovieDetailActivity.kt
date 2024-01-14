package edu.bluejack_231.cinemax231.user

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import edu.bluejack_231.cinemax231.R
import com.squareup.picasso.Picasso

class UserMovieDetailActivity : AppCompatActivity() {

    lateinit var imgv: ImageView
    lateinit var name: TextView
    lateinit var duration: TextView
    lateinit var genre: TextView
    lateinit var language: TextView
    lateinit var description: TextView
    lateinit var toolbar: Toolbar
    lateinit var btn: Button
    fun init(){
        imgv = findViewById(R.id.imgv)
        name = findViewById(R.id.name)
        duration = findViewById(R.id.duration)
        genre = findViewById(R.id.genre)
        language = findViewById(R.id.language)
        description = findViewById(R.id.desc)
        toolbar = findViewById(R.id.appbar)
        btn = findViewById(R.id.user_movie_detail_btn)

    }

    fun bind(){
//      var url = "https://firebasestorage.googleapis.com/v0/b/" + "tpa-mobile-eb1ff.appspot.com" +"/o/"  + "snowdrop.jpeg" + "?alt=media&token=" + "f2ac11f9-9d4d-469f-84b6-0cd6186d1efa"

        btn.setOnClickListener {
            val extras = intent.extras
            val intent = Intent(this, UserMovieTimeSelectionActivity::class.java)
            putSharedPreferencesDataIntoIntent(intent)
            startActivityForResult(intent, 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_user_movie_detail)

        init()
        val extras = intent.extras
        if (extras != null) {
            saveToSharedPreferences(extras)
        }

        loadFromSharedPreferences()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        bind()
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
        duration.setText("Duration: " + sharedPreferences.getInt("movieDuration", 0) + " duration")
        genre.setText("Genre: " + sharedPreferences.getString("movieGenre", ""))
        language.setText("Language: " + sharedPreferences.getString("movieLanguage", ""))
        description.setText(sharedPreferences.getString("movieDescription", ""))
    }

    private fun putSharedPreferencesDataIntoIntent(intent: Intent) {
        val sharedPreferences = getSharedPreferences("MovieDetails", Context.MODE_PRIVATE)
        intent.putExtra("movieImage", sharedPreferences.getString("movieImage", ""))
        intent.putExtra("movieName", sharedPreferences.getString("movieName", ""))
        intent.putExtra("movieDuration", sharedPreferences.getInt("movieDuration", 0))
        intent.putExtra("movieGenre", sharedPreferences.getString("movieGenre", ""))
        intent.putExtra("movieLanguage", sharedPreferences.getString("movieLanguage", ""))
        intent.putExtra("movieDescription", sharedPreferences.getString("movieDescription", ""))
        intent.putExtra("moviePrice", sharedPreferences.getInt("moviePrice", 0))

    }
}