package edu.bluejack_231.cinemax231.utility.dialog

import android.app.Activity
import android.app.AlertDialog
import android.widget.TextView
import edu.bluejack_231.cinemax231.R

class ErrorDialog(val activity: Activity) {
    lateinit var alertDialog: AlertDialog
    lateinit var errorMessage: TextView
    lateinit var confirmButton: TextView

    fun startDialog(error: String) {
        val builder = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_error_dialog, null)
        builder.setView(dialogView)
        builder.setCancelable(true)

        errorMessage = dialogView.findViewById<TextView>(R.id.custom_error_dialog_message)
        errorMessage.text = error;

        confirmButton = dialogView.findViewById<TextView>(R.id.custom_error_dialog_confirm)
        confirmButton.setOnClickListener {
            stop()
        }

        alertDialog = builder.create()
        alertDialog.show()
    }

    fun stop() {
        alertDialog.dismiss()
    }
}
