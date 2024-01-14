package edu.bluejack_231.cinemax231.admin.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack_231.cinemax231.model.AMovie

class MovieViewerViewModel: ViewModel() {
    private val _movies = MutableLiveData<List<AMovie>>()
    val movies: LiveData<List<AMovie>> get() = _movies
    fun fetchMovies() {
        val movies = mutableListOf<AMovie>()
        val db = Firebase.firestore
        db.collection("movies").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val data = document.data!!

                    val name = data["name"].toString()
                    val duration = Integer.parseInt(data["duration"].toString())
                    val genre = data["genre"].toString()
                    val language = data["language"].toString()
                    val description = data["description"].toString()
                    val price = Integer.parseInt(data["price"].toString())
                    val image = data["image"].toString()
                    val censorRating = data["rating"].toString()

                    val movie = AMovie(
                        name = name,
                        duration = duration,
                        genre = genre,
                        language = language,
                        description = description,
                        price = price,
                        image = image,
                        censorRating
                    )

                    movies.add(movie)
                }
                _movies.value = movies
            }
    }
}