package edu.bluejack_231.cinemax231.admin.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack_231.cinemax231.R
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import edu.bluejack_231.cinemax231.admin.AdminSaveMovieActivity
import edu.bluejack_231.cinemax231.model.AMovie
import edu.bluejack_231.cinemax231.model.Movie
import edu.bluejack_231.cinemax231.utility.dialog.LoadingDialog


class MovieViewerAdapter(private var dataSet: MutableList<AMovie>) :
    RecyclerView.Adapter<MovieViewerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    fun updateMovies(newMovies: List<AMovie>) {
        dataSet = newMovies.toMutableList()
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView
        val title: TextView
        val editButton: LinearLayout
        val deleteButton: LinearLayout

        init {
            // Define click listener for the ViewHolder's View.
            poster = view.findViewById<ImageView>(R.id.item_movie_viewer_imageView)
            title = view.findViewById<TextView>(R.id.item_movie_viewer_movieTitle)
            editButton = view.findViewById<LinearLayout>(R.id.item_movie_viewer_editButton)
            deleteButton = view.findViewById<LinearLayout>(R.id.item_movie_viewer_deleteButton)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_movie_viewer, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val movie = dataSet[position]

        Picasso.get().load(movie.image).into(viewHolder.poster)
        viewHolder.title.text = movie.name

        viewHolder.editButton.setOnClickListener {
            val intent = Intent(viewHolder.itemView.context, AdminSaveMovieActivity::class.java)
            movie.passIntent(intent)

            viewHolder.itemView.context.startActivity(intent)
        }

        viewHolder.deleteButton.setOnClickListener {
            val db = Firebase.firestore
            val docRef = db.collection("movies").document(movie.name).delete().addOnSuccessListener {
                dataSet.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, dataSet.size)
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
