package edu.bluejack_231.cinemax231.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.admin.adapter.BookingHistoryAdapter
import edu.bluejack_231.cinemax231.admin.adapter.ManageBookingAdapter
import edu.bluejack_231.cinemax231.model.Movie
import edu.bluejack_231.cinemax231.model.Transaction
import edu.bluejack_231.cinemax231.utility.dialog.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar

class BookingHistoryActivity : AppCompatActivity() {
    val loadingDialog = LoadingDialog(this)
    lateinit var adapter: BookingHistoryAdapter
    lateinit var searchBar: TextInputEditText
    lateinit var data: MutableList<Transaction>
    lateinit var currentData: MutableList<Transaction>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_history)

        searchBar = findViewById<TextInputEditText>(R.id.booking_history_search)

        searchBar.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentData = data.filter {
                    it.id.contains(s.toString())
                }.toMutableList()

                adapter.update(currentData)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        loadingDialog.startLoadingDialog()

        val context = this
        GlobalScope.launch(Dispatchers.Main) {
            data = fetchAsync()
            currentData = data.toMutableList()
            adapter = BookingHistoryAdapter(context, data.toMutableList())

            val recyclerView = findViewById<RecyclerView>(R.id.booking_history_recyclerView)
            recyclerView.adapter = adapter
            loadingDialog.stop()
        }
    }

    suspend fun fetch(): MutableList<Transaction> {
        val transactions = mutableListOf<Transaction>()

        val db = Firebase.firestore

        val colRef = db.collection("transactions")
        val query = colRef.whereIn("status", listOf("approved", "rejected"))
        val docRef = query.get().await()

        for (document in docRef.documents) {
            val movieColRef = document.reference.collection("movie").get().await()
            val movieData = movieColRef.documents[0].data!!
            val movie = Movie.createFromDocument(movieData)

            val data = document.data!!

            transactions.add(
                Transaction.createFromDocument(document.id, data, movie)
            )
        }

        return transactions
    }

    suspend fun fetchAsync() : MutableList<Transaction> {
        return withContext(Dispatchers.IO) {
            fetch()
        }
    }
}