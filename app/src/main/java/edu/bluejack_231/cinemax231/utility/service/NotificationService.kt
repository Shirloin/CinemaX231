package edu.bluejack_231.cinemax231.utility.service

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edu.bluejack_231.cinemax231.R
import java.util.*
import kotlin.collections.HashMap

class NotificationService: FirebaseMessagingService() {
    private val CHANNEL_ID = "channel_id"
    private val CHANNEL_NAME = "channel_name"
    private val NOTIFICATION_ID = 1

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val tokenData: MutableMap<String, Any> = HashMap()
        tokenData.put("token", token);
        var db = FirebaseFirestore.getInstance()
        db.collection("deviceTokens").document().set(tokenData)
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.notification != null) {
            val title = message.notification!!.title
            val body = message.notification!!.body
            send(title, body)
            showAlert(System.currentTimeMillis(), title.toString(),body.toString())
        }
    }
    fun send(title: String?, body: String?) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val notificationChannel = NotificationChannel(
                this.CHANNEL_ID,
                this.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, this.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.baseline_notifications_24)
        notificationManager.notify(this.NOTIFICATION_ID, notificationBuilder.build())
    }
    private fun showAlert(time: Long, title: String, message: String)
    {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title: " + title +
                        "\nMessage: " + message +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
            .setPositiveButton("Okay"){_,_ ->}
            .show()
    }
}