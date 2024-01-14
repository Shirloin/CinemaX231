package edu.bluejack_231.cinemax231.admin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.model.Transaction

class BookingHistoryAdapter (private val context: Context, private val dataset: MutableList<Transaction>): RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder>() {
    fun update(data: MutableList<Transaction>) {
        dataset.clear()
        dataset.addAll(data)
        notifyDataSetChanged()
    }
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        lateinit var imageView: ImageView
        lateinit var movieTitle: TextView
        lateinit var ticket: TextView
        lateinit var username: TextView
        lateinit var date: TextView
        lateinit var code: TextView
        lateinit var status: TextView

        init{
            imageView = view.findViewById(R.id.item_booking_history_imageView)
            movieTitle = view.findViewById(R.id.item_booking_history_movieTitle)
            ticket = view.findViewById(R.id.item_booking_history_ticket)
            username = view.findViewById(R.id.item_booking_history_username)
            date = view.findViewById(R.id.item_booking_history_date)
            code = view.findViewById(R.id.item_booking_history_code)
            status = view.findViewById(R.id.item_booking_history_status)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingHistoryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking_history, parent, false)
        return BookingHistoryAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = dataset[position]

        Picasso.get().load(transaction.movie.image).into(holder.imageView)
        holder.movieTitle.text = transaction.movie.name
        holder.ticket.text = String.format("x%d", transaction.quantity)
        holder.username.text = transaction.username
        holder.date.text = transaction.date
        holder.code.text = transaction.id
        holder.status.text = transaction.status

        val textColor: Int
        if(transaction.status == "rejected") {
            textColor = ContextCompat.getColor(context, R.color.danger)
        } else {
            textColor = ContextCompat.getColor(context, R.color.safe)
        }

        holder.status.setTextColor(textColor)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}