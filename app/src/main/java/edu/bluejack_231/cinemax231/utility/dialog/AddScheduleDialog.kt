package edu.bluejack_231.cinemax231.utility.dialog

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.admin.AdminSaveMovieActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddScheduleDialog(val activity: AdminSaveMovieActivity) {
    var minute = 0

    lateinit var alertDialog: AlertDialog
    lateinit var location: Spinner
    lateinit var date: TextInputEditText
    lateinit var startTime: TextInputEditText
    lateinit var endTime: TextInputEditText
    lateinit var addButton: Button
    lateinit var setDateButton: Button

    lateinit var dialogView: View

    val errorDialog = ErrorDialog(activity)
    val loadingDialog = LoadingDialog(activity)

    fun startDialog(min: Int) {
        minute = min
        val builder = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_add_schedule, null)
        builder.setView(dialogView)
        builder.setCancelable(true)

        init()

        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun init() {
        location = dialogView.findViewById<Spinner>(R.id.dialog_add_schedule_location)
        date = dialogView.findViewById<TextInputEditText>(R.id.dialog_add_schedule_date)
        startTime = dialogView.findViewById<TextInputEditText>(R.id.dialog_add_schedule_start_time)
        endTime = dialogView.findViewById<TextInputEditText>(R.id.dialog_add_schedule_end_time)
        addButton = dialogView.findViewById<Button>(R.id.dialog_add_schedule_add)
        setDateButton = dialogView.findViewById<Button>(R.id.dialog_add_schedule_set_date)

        ArrayAdapter.createFromResource(
            activity,
            R.array.locations,
            android.R.layout.simple_spinner_item
        ).also {
                adapter->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            location.adapter = adapter
        }

        startTime.onFocusChangeListener = View.OnFocusChangeListener{ dialogView, hasFocus ->
            if(!hasFocus) {
                calculateEndTime()
            }
        }

        addButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main){
                loadingDialog.startLoadingDialog()
                handleSubmit()
                loadingDialog.stop()
            }
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        setDateButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                activity,
                { view, _year, _month, _day ->
                    date.setText("${_day}/${_month}/${_year}")
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }

    private fun calculateEndTime() {
        var res = ""
        try {
            val parts = startTime.text.toString().split(":")
            val startHours = parts[0].toInt()
            val startMinutes = parts[1].toInt()

            val totalMinutes = startHours * 60 + startMinutes

            val newTotalMinutes = totalMinutes + minute

            val newHours = newTotalMinutes / 60
            val newMinutes = newTotalMinutes % 60

            res = String.format("%02d:%02d", newHours, newMinutes)
        } catch (e: Exception) {

        }

        endTime.setText(res)
    }

    private fun checkDate(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        dateFormat.isLenient = false

        try {
            dateFormat.parse(dateString)
        } catch(e: Exception) {
            return false
        }

        return true
    }

    private fun checkTime(timeString: String): Boolean {
        val pattern = Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")
        return pattern.matches(timeString)
    }

    private fun isTimeBefore(time1: String, time2: String): Boolean {
        val timeFormat = SimpleDateFormat("HH:mm")

        try {
            val parsedTime1 = timeFormat.parse(time1)
            val parsedTime2 = timeFormat.parse(time2)

            return parsedTime1.before(parsedTime2)
        } catch (e: Exception) {
            return false
        }
    }

    private fun convertDate(date: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val localDate = LocalDate.parse(date, inputFormatter)
        return localDate.format(outputFormatter)
    }

    private suspend fun checkConflictingSchedule(date: String, startTime: String, endTime: String): Boolean {
        val db = Firebase.firestore

        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        val checkStart = LocalTime.parse(startTime, formatter)
        val checkEnd = LocalTime.parse(endTime, formatter)
        val convertedDate = convertDate(date)

        val timeSnapshots = db.collection("schedules/${convertedDate}/time").get().await()
        for(snapshot in timeSnapshots) {
            val split = snapshot.id.split("-")

            val startTime = split[0]
            val endTime = split[1]


            val rangeStart = LocalTime.parse(startTime, formatter)
            val rangeEnd = LocalTime.parse(endTime, formatter)

            if(rangeEnd.isAfter(checkStart) && rangeStart.isBefore(checkEnd)) return false
        }
        return true
    }
    private suspend fun validate(): Int {
        if(!checkDate(date.text.toString())) return 0
        if(!checkTime(startTime.text.toString())) return 1
        if(!checkTime(endTime.text.toString())) return 2
        if(!isTimeBefore(startTime.text.toString(), endTime.text.toString())) return 3
        if(!checkConflictingSchedule(date.text.toString(), startTime.text.toString(), endTime.text.toString())) return 4
        return -1
    }
    private suspend fun handleSubmit() {
        val validation = validate()
        if(validation == 0) {
            errorDialog.startDialog("Date must be in day/month/year format")
            return
        }
        if(validation == 1) {
            errorDialog.startDialog("Start Time must be in 24 hour format")
            return
        }
        if(validation == 2) {
            errorDialog.startDialog("End Time must be in 24 hour format")
            return
        }
        if(validation == 3) {
            errorDialog.startDialog("End time must be after start time")
            return
        }
        if(validation == 4) {
            errorDialog.startDialog("Schedule should not conflict with other")
            return
        }

        val db = Firebase.firestore
        val convertedDate = convertDate(date.text.toString())

        val scheduleDocument = db.collection("schedules/${convertedDate}/time")
            .document("${startTime.text.toString()}-${endTime.text.toString()}")

        scheduleDocument.set(hashMapOf(
            "start" to startTime.text.toString(),
            "end" to endTime.text.toString()
        )).await()

        db.document("movies/${activity.movieTitle.text}/schedules/${convertedDate}")
            .set(hashMapOf(
                "date" to convertedDate
            )).await()

        val movieScheduleDocument = db.collection("movies/${activity.movieTitle.text}/schedules/${convertedDate}/time")
            .document("${startTime.text.toString()}-${endTime.text.toString()}")

        movieScheduleDocument.set(hashMapOf(
            "start_time" to startTime.text.toString(),
            "end_time" to endTime.text.toString(),
            "seats" to "/AAAAA/AAAAA/AAAAA/AAAAA/AAAAA/AAAAA/AAAAA/AAAAA/AAAAA/AAAAA"
        )).await()

        alertDialog.dismiss()
        activity.finish()
    }
}
