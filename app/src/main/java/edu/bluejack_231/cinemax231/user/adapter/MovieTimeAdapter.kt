package edu.bluejack_231.cinemax231.user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.user.`interface`.OnItemClickListener

class MovieTimeAdapter (var time: ArrayList<String>): RecyclerView.Adapter<MovieTimeAdapter.ViewHolder>(){

    lateinit var listener: OnItemClickListener
    class ViewHolder (view: View): RecyclerView.ViewHolder(view){
        lateinit var time: TextView
        init{
            time = view.findViewById(R.id.item_movie_schedule_textView)
        }
        fun bind(t: String){
            time.setText(t)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_schedule, parent, false)
        val vh = ViewHolder(view)
        view.setOnClickListener {
            val position = vh.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
        return vh
    }

    override fun getItemCount(): Int {
        return time.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(time[position])
    }

}