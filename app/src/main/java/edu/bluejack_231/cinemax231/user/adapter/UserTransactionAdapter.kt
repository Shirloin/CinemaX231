package edu.bluejack_231.cinemax231.user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.model.Transaction
import edu.bluejack_231.cinemax231.user.`interface`.OnButtonClickListener
import edu.bluejack_231.cinemax231.user.`interface`.OnItemClickListener
import java.text.SimpleDateFormat
import java.util.*

class UserTransactionAdapter(var transactions: ArrayList<Transaction>,
                             var itemClickListener: OnItemClickListener,
                             var onButtonClickListener: OnButtonClickListener): RecyclerView.Adapter<UserTransactionAdapter.ViewHolder>() {


    class ViewHolder (view: View, val itemClickListener: OnItemClickListener, val onButtonClickListener: OnButtonClickListener): RecyclerView.ViewHolder(view){
        lateinit var imgv: ImageView
        lateinit var name: TextView
        lateinit var qty: TextView
        lateinit var date: TextView
        lateinit var status: TextView
        lateinit var btn: ImageButton

        init{
            imgv = view.findViewById(R.id.transaction_imgv)
            name = view.findViewById(R.id.transaction_name)
            qty = view.findViewById(R.id.transaction_qty)
            date = view.findViewById(R.id.transaction_date)
            status = view.findViewById(R.id.transaction_status)
            btn = view.findViewById(R.id.del_btn)

        }
        fun bind(t: Transaction){
            name.setText(t.movie.name)
            println(t.movie.name)
            qty.setText(t.quantity.toString())
            date.setText(changeDate(t.date))
            status.setText(t.status)
            Picasso.get().load(t.movie.image).into(imgv)
            btn.setTag(adapterPosition)

            if (t.status.equals("request", ignoreCase = true)) {
                btn.visibility = View.GONE
            }
            else{
                btn.visibility = View.VISIBLE
                btn.setOnClickListener {
                    var position: Int = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onButtonClickListener.onDeleteClicked(position)
                    }
                }
            }
            itemView.setOnClickListener {
                if (itemClickListener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(position)
                    }
                }
            }
        }
        fun changeDate(date: String): String{
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.US)

            val oldDate = inputFormat.parse(date)
            val newDate = outputFormat.format(oldDate)
            return newDate.toString()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserTransactionAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_transaction, parent, false)
        val vh = ViewHolder(view, this.itemClickListener,this.onButtonClickListener)
        return vh
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int {
        return transactions.size
    }
}