package edu.bluejack_231.cinemax231.user.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.model.Movie
import edu.bluejack_231.cinemax231.user.UserSeatSelectionActivity
import edu.bluejack_231.cinemax231.user.`interface`.OnItemClickListener
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UserMovieTimeSelectionAdapter (var movie: Movie, var schedules: ArrayList<String>): RecyclerView.Adapter<UserMovieTimeSelectionAdapter.ViewHolder>(){
    class ViewHolder (var view: View): RecyclerView.ViewHolder(view){
        lateinit var date: TextView
        lateinit var price: TextView
        lateinit var rcv: RecyclerView
        lateinit var adapter: MovieTimeAdapter
        init{
            date = view.findViewById(R.id.item_schedule_viewer_date)
            price = view.findViewById(R.id.item_movie_price)
            rcv = view.findViewById(R.id.item_schedule_rcv)
        }
        fun bind(movie: Movie){
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.US)

            val oldDate = inputFormat.parse(movie.schedules)
            val newDate = outputFormat.format(oldDate)
            date.setText(newDate.toString())
            price.setText(movie.price.toString())
            val layoutManager = GridLayoutManager(rcv.context, 3)
            rcv.layoutManager = layoutManager

            val time = ArrayList<String>()
            val db = FirebaseFirestore.getInstance()
            db.collection("movies")
                .document(movie.name)
                .collection("schedules")
                .document(movie.schedules).collection("time").get().addOnSuccessListener {documents ->
                    for(doc in documents){
                        val start = doc.getString("start_time").toString()
                        val end = doc.getString("end_time").toString()
                        val t = start + "-" + end
                        time.add(t.toString())
                    }

                    adapter = MovieTimeAdapter(time)
                    adapter.setOnItemClickListener(object : OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            val intent = Intent(view.context, UserSeatSelectionActivity::class.java)
                            intent.putExtra("movieImage", movie.image)
                            intent.putExtra("movieName", movie.name)
                            intent.putExtra("movieDuration", movie.duration)
                            intent.putExtra("movieGenre", movie.genre)
                            intent.putExtra("movieLanguage", movie.language)
                            intent.putExtra("movieDescription", movie.description)
                            intent.putExtra("moviePrice", movie.price)
                            intent.putExtra("movieSchedule", movie.schedules )
                            intent.putExtra("movieTime", time[position])
                            view.context.startActivity(intent)
                        }
                    })
                    rcv.adapter = adapter
                }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule_viewer, parent, false)
        val vh = ViewHolder(view)
        return vh
    }
    override fun getItemCount(): Int {
        return schedules.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currMovie = Movie(movie.name, movie.duration, movie.genre, movie.language, movie.description, movie.price, movie.image, schedules[position])
        holder.bind(currMovie)
    }

}