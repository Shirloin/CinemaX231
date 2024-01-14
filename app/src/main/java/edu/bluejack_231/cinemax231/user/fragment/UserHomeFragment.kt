package edu.bluejack_231.cinemax231.user.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.model.Movie
import edu.bluejack_231.cinemax231.user.UserMovieDetailActivity
import edu.bluejack_231.cinemax231.user.adapter.UserHomeAdapter
import edu.bluejack_231.cinemax231.user.`interface`.OnItemClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserHomeFragment : Fragment() {

    lateinit var rcv: RecyclerView
    lateinit var homeAdapter: UserHomeAdapter
    lateinit var movies: ArrayList<Movie>
    lateinit var allMovies: ArrayList<Movie>
    lateinit var swip: SwipeRefreshLayout
    var LIMIT:Long = 6
    private var lastDocumentSnapshot: DocumentSnapshot? = null
    private var search: String = ""
    private val db = FirebaseFirestore.getInstance()


    private var isLoading = false
    var currentPage = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_user_home, container, false)
        rcv = root.findViewById(R.id.rcv)
        swip = root.findViewById(R.id.swip)
        movies = ArrayList<Movie>()
        allMovies = ArrayList<Movie>()
        homeAdapter = UserHomeAdapter(this.requireContext())
        rcv.adapter = homeAdapter
        action()
        fetchMovies()
        setupScrollListener()
        val searchView: SearchView = root.findViewById(R.id.searchView)
        setupSearchView(searchView)
        return root
    }


    private fun fetchMovies() {
        GlobalScope.launch(Dispatchers.Main) {
            swip.isRefreshing = true
            var query = db.collection("movies").orderBy("name").limit(LIMIT)
            if (lastDocumentSnapshot != null) {
                query = query.startAfter(lastDocumentSnapshot!!)
            }
            val documents = query.get().await()
            if(!documents.isEmpty){

                lastDocumentSnapshot = documents.documents[documents.size()-1]
                for (doc in documents) {
                    val name = doc.getString("name") ?: ""
                    val duration = doc.getString("duration")?: ""
                    val genre = doc.getString("genre") ?: ""
                    val language = doc.getString("language") ?: ""
                    val description = doc.getString("description") ?: ""
                    val image = doc.getString("image") ?: ""
                    val price = doc.getString("price")?.toInt()?:0
                    val censorRating = doc.getString("rating")?.toString()?:""

                    val movie = Movie(name, duration.toInt(), genre, language, description, price, image, censorRating)
                    movies.add(movie)
                    allMovies.add(movie)
                }

                homeAdapter.setItems(movies)
                isLoading = false
                swip.isRefreshing = false
            }
            else{
                isLoading = false
                swip.isRefreshing = false
            }
        }


    }

    private fun setupSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                search = newText.orEmpty()
                println("Debug Search " + search)
                if(search.isNotEmpty()){
                    movies.clear()
                    allMovies.clear()
                    filterMovies()
                }
                else{
                    movies.clear()
                    allMovies.clear()
                    fetchMovies()
                }
                return true
            }
        })
    }
    private fun filterMovies() {
        GlobalScope.launch(Dispatchers.Main) {
            isLoading = true
            swip.isRefreshing = true
            movies.clear()
            allMovies.clear()
            lastDocumentSnapshot = null
            println("Debug movies" + movies)
            var query = db.collection("movies").orderBy("name")
            val documents = query.get().await()
            if(!documents.isEmpty){
                for (doc in documents) {
                    val name = doc.getString("name") ?: ""
                    val duration = doc.getString("duration")?: ""
                    val genre = doc.getString("genre") ?: ""
                    val language = doc.getString("language") ?: ""
                    val description = doc.getString("description") ?: ""
                    val image = doc.getString("image") ?: ""
                    val price = doc.getString("price")?.toInt()?:0
                    val censorRating = doc.getString("rating")?.toString()?:""

                    val movie = Movie(name, duration.toInt(), genre, language, description, price, image, censorRating)
                    movies.add(movie)
                    allMovies.add(movie)
                }
                val filteredMovies = if (search.isNotEmpty()) {
                    movies.filter { movie ->
                        movie.name.contains(search, ignoreCase = true)
                    }
                } else {
                    movies
                }
                println("Debug filtered" + filteredMovies)
                homeAdapter.setItems(filteredMovies as ArrayList<Movie>)
                isLoading = false
                swip.isRefreshing = false
            }
            else{
                isLoading = false
                swip.isRefreshing = false
            }

        }
    }
    fun suspend(){

    }
    private fun action(){
        homeAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val currMovie: Movie = movies[position]
                val intent = Intent(activity, UserMovieDetailActivity::class.java)
                intent.putExtra("movieImage", currMovie.image)
                intent.putExtra("movieName", currMovie.name)
                intent.putExtra("movieDuration", currMovie.duration)
                intent.putExtra("movieGenre", currMovie.genre)
                intent.putExtra("movieLanguage", currMovie.language)
                intent.putExtra("movieDescription", currMovie.description)
                intent.putExtra("moviePrice", currMovie.price)
                startActivityForResult(intent, 1)
            }
        })
    }

    private fun setupScrollListener() {
        rcv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (totalItemCount == (lastVisibleItem + 1) && search.isEmpty()) {
                    Log.d("debug", "scroll " + search)
                    if(!isLoading){
                        isLoading = true
                        fetchMovies()
                    }
                }
            }
        })
    }
}