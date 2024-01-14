package edu.bluejack_231.cinemax231.model

import java.text.SimpleDateFormat
import java.util.Locale

class Transaction(
    var id: String,
    var date: String,
    var movie: Movie,
    var quantity: Int,
    var seat: List<Int>,
    var status: String,
    var time: String,
    var username: String
) {
    companion object {
        fun createFromDocument(id: String, data: Map<String, Any>, movie: Movie) : Transaction {
            val dateFormater = SimpleDateFormat("EEEE, dd MMMM, yy", Locale.US)

            val date = data["date"].toString()
            val quantity = Integer.parseInt(data["quantity"]!!.toString())
            val seat = data["seat"]!! as List<Int>
            val status = data["status"]!!.toString()
            val time = data["time"]!!.toString()
            val username = data["username"]!!.toString()

            return Transaction(
                id,
                date,
                movie,
                quantity,
                seat,
                status,
                time,
                username,
            )
        }
    }

    fun toMap() : Map<String, Any> {
        return hashMapOf(
            "date" to date.toString(),
            "quantity" to quantity.toString(),
            "seat" to seat,
            "status" to status,
            "time" to time,
            "username" to username
        )
    }

}