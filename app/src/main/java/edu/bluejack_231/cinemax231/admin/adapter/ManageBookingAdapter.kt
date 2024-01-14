package edu.bluejack_231.cinemax231.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack_231.cinemax231.R
import com.squareup.picasso.Picasso
import edu.bluejack_231.cinemax231.admin.ManageBookingActivity
import edu.bluejack_231.cinemax231.model.Transaction
import java.text.SimpleDateFormat
import java.util.Locale


class ManageBookingAdapter(private var dataSet: MutableList<Transaction>, private val activity: ManageBookingActivity) :
    RecyclerView.Adapter<ManageBookingAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    fun update(transaction: List<Transaction>) {
        dataSet.clear()
        dataSet.addAll(transaction)
        notifyDataSetChanged()
    }
    class ViewHolder(view: View, activity: ManageBookingActivity) : RecyclerView.ViewHolder(view) {
        val moviePoster: ImageView
        val movieTitle: TextView
        val username: TextView
        val ticketCount: TextView
        val time: TextView
        val dateLocation: TextView
        val hashCode: TextView
        val approveButton: Button
        val rejectButton: Button

        init {
            // Define click listener for the ViewHolder's View
            moviePoster = view.findViewById(R.id.item_manage_booking_image)
            movieTitle = view.findViewById(R.id.item_manage_booking_title)
            username = view.findViewById(R.id.item_manage_booking_user)
            ticketCount = view.findViewById(R.id.item_manage_booking_ticket)
            time = view.findViewById(R.id.item_manage_booking_time)
            dateLocation = view.findViewById(R.id.item_manage_booking_date_location)
            hashCode = view.findViewById(R.id.item_manage_booking_hash)
            approveButton = view.findViewById(R.id.manage_booking_approve)
            rejectButton = view.findViewById(R.id.manage_booking_reject)

            rejectButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    activity.onRejectButtonClick(position)
                }
            }

            approveButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    activity.onApproveButtonClick(position)
                }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_manage_booking, viewGroup, false)
        return ManageBookingAdapter.ViewHolder(view, activity)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val transaction = dataSet[position]

        Picasso.get().load(transaction.movie.image).into(viewHolder.moviePoster)
        viewHolder.movieTitle.text = transaction.movie.name
        viewHolder.ticketCount.text = "Ticket x" + transaction.quantity.toString()
        viewHolder.username.text = transaction.username
        viewHolder.time.text = transaction.time
        viewHolder.dateLocation.text = transaction.date
        viewHolder.hashCode.text = transaction.id

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
