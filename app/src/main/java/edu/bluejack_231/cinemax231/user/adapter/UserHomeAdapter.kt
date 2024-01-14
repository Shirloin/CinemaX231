package edu.bluejack_231.cinemax231.user.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.user.`interface`.OnItemClickListener
import com.squareup.picasso.Picasso
import edu.bluejack_231.cinemax231.model.Movie
import edu.bluejack_231.cinemax231.user.fragment.UserHomeFragment

class UserHomeAdapter(private val ctx: Context): RecyclerView.Adapter<UserHomeAdapter.ViewHolder>() {

    private var movies = ArrayList<Movie>()
    public fun setItems(movies: ArrayList<Movie>) {
        this.movies.clear()
        this.movies.addAll(movies)
        notifyDataSetChanged()
    }

    lateinit var listener: OnItemClickListener

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        lateinit var imgv: ImageView
        lateinit var name: TextView
        init{
            imgv = view.findViewById(R.id.imgv)
            name = view.findViewById(R.id.name)
        }
        fun bind(movie: Movie){val url = movie.image
            name.setText(movie.name)
            Picasso.get().load(url).into(imgv)
        }

    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(this.ctx).inflate(R.layout.item_user_movie, parent, false)
        val vh = ViewHolder(view)
        view.setOnClickListener {
            val position = vh.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int {
        return movies.size
    }

}