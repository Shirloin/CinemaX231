package edu.bluejack_231.cinemax231.user.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.model.Movie
import edu.bluejack_231.cinemax231.model.Transaction
import edu.bluejack_231.cinemax231.user.adapter.UserTransactionAdapter
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack_231.cinemax231.user.UserTransactionDetailActivity
import edu.bluejack_231.cinemax231.user.`interface`.OnButtonClickListener
import edu.bluejack_231.cinemax231.user.`interface`.OnItemClickListener

class UserTransactionFragment : Fragment(), OnItemClickListener, OnButtonClickListener {

    lateinit var rcv: RecyclerView
    lateinit var adapter: UserTransactionAdapter
    lateinit var transactions: ArrayList<Transaction>
    var db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_user_transaction, container, false)
        rcv = root.findViewById(R.id.transaction_rcv)
        transactions = ArrayList<Transaction>()
        val sharedPreferences = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "").toString()
        val id = sharedPreferences.getString("id", "").toString()


        db.collection("transactions").whereEqualTo("id", id).get().addOnSuccessListener {documents ->
            var totalTransactions = documents.size()
            var processedTransactions = 0
            for(doc in documents){
                val trid = doc.id
                lateinit var transaction: Transaction
                lateinit var movie: Movie
                db.collection("transactions").document(trid.toString()).collection("movie").get().addOnSuccessListener {movieDoc ->
                    if(!movieDoc.isEmpty){
                        val doc = movieDoc.documents[0]
                        val name = doc.getString("name") ?: ""
                        val duration = doc.getLong("duration")?.toInt() ?: 0
                        val genre = doc.getString("genre") ?: ""
                        val language = doc.getString("l" +
                                "anguage") ?: ""
                        val description = doc.getString("description") ?: ""
                        val image = doc.getString("image") ?: ""
                        val price = doc.getLong("price")?.toInt()?:0
                        val date = doc.getString("date")?:""
                        val time = doc.getString("time")?:""
                        movie = Movie(name, duration, genre, language, description, price, image, date, time)
                    }
                    val date = doc.getString("date")?: ""
                    val qty = doc.getString("quantity")?.toInt()?: 0
                    val seats = doc.get("seat") as List<Int>
                    val status = doc.getString("status")?:""
                    val time = doc.getString("time")?:""
                    val username = doc.getString("username")?:""
                    transaction = Transaction(trid,date, movie, qty, seats, status, time, username)
                    transactions.add(transaction)
                    processedTransactions++
                    if (processedTransactions == totalTransactions) {
                        adapter = UserTransactionAdapter(transactions,  this, this)
                        rcv.adapter = adapter
                        rcv.layoutManager = LinearLayoutManager(context)
                    }
                }
            }
        }
        return root
    }

    override fun onItemClick(position: Int) {
        var intent:Intent = Intent(activity, UserTransactionDetailActivity::class.java)
        var t: Transaction = transactions.get(position)
        var movie: Movie = t.movie
        intent.putExtra("movieImage", movie.image)
        intent.putExtra("movieName", movie.name)
        intent.putExtra("movieSchedule", movie.schedules)
        intent.putExtra("movieTime", movie.time)
        intent.putExtra("moviePrice", movie.price)
        intent.putExtra("date", t.date)
        intent.putExtra("time", t.time)
        intent.putExtra("quantity", t.quantity)
        intent.putExtra("total", t.quantity * movie.price)
        val convertedSeats = convertSeatNumbers(t.seat)
        val seatsString = t.seat?.joinToString(separator = ", ") ?: ""
        intent.putExtra("seats", convertedSeats.joinToString(", "))
        startActivityForResult(intent, 1)
    }

    fun convertSeatNumbers(seatNumbers: List<Int>): List<String> {
        return seatNumbers.map { number ->
            val letter = 'A' + (number - 1) / 5
            val digit = (number - 1) % 5 + 1
            "$letter$digit"
        }
    }


    override fun onDeleteClicked(position: Int) {
        var t: Transaction = transactions.get(position);
        val ref = db.collection("transactions").document(t.id).delete().addOnSuccessListener {
            transactions.remove(t)
            adapter.notifyDataSetChanged()
        }
    }
}