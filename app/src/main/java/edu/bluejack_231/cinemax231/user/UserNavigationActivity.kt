package edu.bluejack_231.cinemax231.user

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.user.fragment.UserHomeFragment
import edu.bluejack_231.cinemax231.user.fragment.UserProfileFragment
import edu.bluejack_231.cinemax231.user.fragment.UserTransactionFragment
import edu.bluejack_231.cinemax231.utility.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import edu.bluejack_231.cinemax231.auth.LoginActivity
import edu.bluejack_231.cinemax231.utility.service.NotificationService
import java.util.*

class UserNavigationActivity : BaseActivity() {

    lateinit var bnv: BottomNavigationView
    lateinit var home: UserHomeFragment
    lateinit var transaction: UserTransactionFragment
    lateinit var profile: UserProfileFragment
    lateinit var toolbar: Toolbar

    fun init(){
        bnv = findViewById(R.id.bottomnav)
        home = UserHomeFragment()
        transaction = UserTransactionFragment()
        profile = UserProfileFragment()
        toolbar = findViewById(R.id.appbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_user_navigation)
        init()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        scheduleDailyNotification()
        supportFragmentManager.beginTransaction().replace(R.id.container,home).commit()
        bnv.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when(item.itemId){
                R.id.home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, UserHomeFragment()).commit()
                    return@OnItemSelectedListener true
                }
                R.id.transaction -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, UserTransactionFragment()).commit()
                    return@OnItemSelectedListener true
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, UserProfileFragment()).commit()
                    return@OnItemSelectedListener true
                }
            }
            false
        })

    }
    fun scheduleDailyNotification() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationService::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent,  PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(System.currentTimeMillis())
        calendar.set(Calendar.HOUR_OF_DAY, 18)
        calendar.set(Calendar.MINUTE, 29)
        calendar.set(Calendar.SECOND, 30)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
            AlarmManager.INTERVAL_DAY, pendingIntent
        )

    }

}