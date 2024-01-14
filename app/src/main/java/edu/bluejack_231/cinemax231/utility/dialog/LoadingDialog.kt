package edu.bluejack_231.cinemax231.utility.dialog

import android.app.Activity
import android.app.AlertDialog
import edu.bluejack_231.cinemax231.R

class LoadingDialog(val activity: Activity) {
    lateinit var alertDialog: AlertDialog

    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_loading_dialog, null))
        builder.setCancelable(true)

        alertDialog = builder.create()
        alertDialog.show()
    }

    fun stop() {
        alertDialog.dismiss()
    }
}