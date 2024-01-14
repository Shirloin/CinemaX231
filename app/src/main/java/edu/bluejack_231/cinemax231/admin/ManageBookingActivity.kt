package edu.bluejack_231.cinemax231.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.Query
import edu.bluejack_231.cinemax231.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack_231.cinemax231.admin.adapter.ManageBookingAdapter
import edu.bluejack_231.cinemax231.model.Movie
import edu.bluejack_231.cinemax231.model.Transaction
import edu.bluejack_231.cinemax231.utility.dialog.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.util.Calendar
import kotlin.text.StringBuilder


class ManageBookingActivity : AppCompatActivity() {
    val loadingDialog = LoadingDialog(this)
    lateinit var data: List<Transaction>
    lateinit var currentData: List<Transaction>
    lateinit var adapter: ManageBookingAdapter
    lateinit var searchBar: TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_booking)

        searchBar = findViewById<TextInputEditText>(R.id.manage_booking_search)

        loadingDialog.startLoadingDialog()

        val activity = this
        GlobalScope.launch(Dispatchers.Main) {
            data = fetchAsync()
            currentData = data.toList()
            adapter = ManageBookingAdapter(data.toMutableList(), activity)

            val recyclerView = findViewById<RecyclerView>(R.id.manage_booking_recycler_view)
            recyclerView.adapter = adapter

            searchBar.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val newData = data.filter {
                        it.id.contains(s.toString())
                    }
                    adapter.update(newData)
                    currentData = newData.toList()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            loadingDialog.stop()
        }
    }

    suspend fun fetch(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()

        val db = Firebase.firestore

        val colRef = db.collection("transactions")
        val query = colRef.whereEqualTo("status", "request").orderBy("date", Query.Direction.ASCENDING).orderBy("time", Query.Direction.ASCENDING)
        val docRef = query.get().await()

        for (document in docRef.documents) {
            val movieColRef = document.reference.collection("movie").get().await()
            val movieData = movieColRef.documents[0].data!!
            val movie = Movie.createFromDocument(movieData)

            val data = document.data!!
            val transaction = Transaction.createFromDocument(document.id, data, movie)

            val calendar = Calendar.getInstance()
            val date = String.format(
                "%s-%s-%s",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) - 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            if(date > movieData["date"].toString()) {
                reject(transaction)
                continue
            }

            val time = String.format(
                "%s:%s",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            )
            val endTime = movie.time.split("-")[1]
            if(date == transaction.date && time > endTime) {
                reject(transaction)
                continue
            }

            transactions.add(
                Transaction.createFromDocument(document.id, data, movie)
            )
        }

        return transactions
    }

    suspend fun fetchAsync() : List<Transaction> {
        return withContext(Dispatchers.IO) {
            fetch()
        }
    }

    fun updateSeat(seats: String, seatNumber: Int, char: Char): String {
        val sb = StringBuilder(seats)
        val index = (seatNumber - 1) / 5 + seatNumber

        sb.setCharAt(index, char)

        return sb.toString()
    }


    suspend fun reject(transaction: Transaction) {
        transaction.status = "rejected"

        val db = Firebase.firestore
        val colRef = db.collection("transactions")

        val movieSnap = db.collection("transactions/${transaction.id}/movie").get().await()
        val movie = movieSnap.documents[0].data!!
        val movieDate = movie["date"].toString()
        val movieSchedule = movie["time"].toString()

        val movieCol = db.document(
            "movies/${movie["name"].toString()}/schedules/${movieDate}/time/${movieSchedule}"
        )
        val movieReference = movieCol.get().await()

        var seats = movieReference["seats"].toString()
        for(seat in transaction.seat)  {
            seats = updateSeat(seats, seat, 'A')
        }

        movieCol.update(
            mapOf(
                "seats" to seats
            )
        ).await()

        colRef.document(transaction.id).set(transaction.toMap()).await()
    }

    fun onRejectButtonClick(position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val temp = currentData[position]

            GlobalScope.launch(Dispatchers.Main) {
                reject(temp)
                data = data.filter {
                    it.id != temp.id
                }
                currentData = currentData.filter {
                    it.id != temp.id
                }
                adapter.update(currentData)
                loadingDialog.stop()
            }
        }
        loadingDialog.startLoadingDialog()


    }

    fun onApproveButtonClick(position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val temp = currentData[position]
            temp.status = "approved"

            loadingDialog.startLoadingDialog()

            val db = Firebase.firestore
            val colRef = db.collection("transactions")


            GlobalScope.launch(Dispatchers.Main) {
                val movieSnap = db.collection("transactions/${temp.id}/movie").get().await()
                val movie = movieSnap.documents[0].data!!
                val movieDate = movie["date"].toString()
                val movieSchedule = movie["time"].toString()

                val movieCol = db.document(
                    "movies/${movie["name"].toString()}/schedules/${movieDate}/time/${movieSchedule}"
                )
                val movieReference = movieCol.get().await()

                var seats = movieReference["seats"].toString()
                for(seat in temp.seat)  {
                    seats = updateSeat(seats, seat, 'U')
                }

                movieCol.update(
                    mapOf(
                        "seats" to seats
                    )
                ).await()

                colRef.document(temp.id).set(temp.toMap()).await()
                data = data.filter {
                    it.id != temp.id
                }
                currentData = currentData.filter {
                    it.id != temp.id
                }
                adapter.update(currentData)
                loadingDialog.stop()
            }
        }
    }
}