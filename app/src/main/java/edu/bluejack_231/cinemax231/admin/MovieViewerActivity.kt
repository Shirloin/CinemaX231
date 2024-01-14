package edu.bluejack_231.cinemax231.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack_231.cinemax231.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack_231.cinemax231.admin.adapter.MovieViewerAdapter
import edu.bluejack_231.cinemax231.admin.view_model.MovieViewerViewModel
import edu.bluejack_231.cinemax231.model.AMovie
import edu.bluejack_231.cinemax231.model.Movie

class MovieViewerActivity : AppCompatActivity() {
    private lateinit var viewModel: MovieViewerViewModel
    private lateinit var createMovieButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_viewer)

        viewModel = ViewModelProvider(this).get(MovieViewerViewModel::class.java)

        createMovieButton = findViewById(R.id.admin_movie_viewer_create_movie)

        val adapter = MovieViewerAdapter(mutableListOf())
        val recyclerView = findViewById<RecyclerView>(R.id.admin_movie_viewer_recycler)
        recyclerView.adapter = adapter

        viewModel.movies.observe(this, { movies ->
            adapter.updateMovies(movies)
        })

        // Fetch movies when the activity is created
        viewModel.fetchMovies()

        createMovieButton.setOnClickListener {
            val intent = Intent(this, AdminSaveMovieActivity::class.java)
            val movie = AMovie.createEmpty()
            movie.passIntent(intent)

            startActivity(intent)
        }
    }
}