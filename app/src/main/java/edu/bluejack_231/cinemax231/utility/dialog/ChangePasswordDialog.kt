package edu.bluejack_231.cinemax231.utility.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.auth.LoginActivity
import edu.bluejack_231.cinemax231.model.User

class ChangePasswordDialog(val activity: Activity) {
    lateinit var alertDialog: ErrorDialog
    lateinit var loadingDialog: LoadingDialog
    lateinit var passwordField: TextInputEditText
    lateinit var saveButton: Button

    val errorDialog = ErrorDialog(activity)

    fun startDialog(user: User) {
        loadingDialog = LoadingDialog(activity)
        val builder = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_change_password, null)
        builder.setView(dialogView)
        builder.setCancelable(true)

        alertDialog = ErrorDialog(activity)
        passwordField = dialogView.findViewById<TextInputEditText>(R.id.change_password_password)
        saveButton = dialogView.findViewById(R.id.change_password_save)

        val alert = builder.create()
        saveButton.setOnClickListener {
            if(passwordField.text.toString().isEmpty()) {
                errorDialog.startDialog("Password cannot be empty")
                return@setOnClickListener
            }
            if(!passwordField.text.toString().contains(Regex("[0-9]"))){
                errorDialog.startDialog("Password must contain at least 1 number")
            }

            val db = Firebase.firestore

            loadingDialog.startLoadingDialog()

            val userCollection = db.collection("users")
            userCollection.whereEqualTo("username", user.username).get().addOnSuccessListener {
                val documentSnapshot = it.documents[0]
                val documentReference = userCollection.document(documentSnapshot.id)

                documentReference.update(
                    mapOf(
                        "password" to BCrypt.withDefaults().hashToString(12, passwordField.text.toString().toCharArray())
                    )
                ).addOnSuccessListener {
                    loadingDialog.stop()
                    alert.dismiss()
                    logOut()
                }
            }
        }

        alert.show()
    }

    private fun logOut() {
        activity.getSharedPreferences("User", Context.MODE_PRIVATE).edit().remove("User").apply()
        activity.startActivity(Intent(activity, LoginActivity::class.java))
        activity.finish()
    }
}
