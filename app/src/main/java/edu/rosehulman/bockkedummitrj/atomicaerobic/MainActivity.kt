package edu.rosehulman.bockkedummitrj.atomicaerobic

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.HomeFragment
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.SplashFragment
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.WorkoutTimerFragment
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.blockouttimes.BlockoutTimeAdapter
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.blockouttimes.BlockoutTimesFragment
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.dashboard.DashboardFragment
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.settings.SettingsFragment
import java.util.*


class MainActivity : AppCompatActivity(), SplashFragment.OnLoginButtonPressedListener {

    // Authentication
    private val RC_SIGN_IN = 1
    private val auth = FirebaseAuth.getInstance()
    private lateinit var authListener: FirebaseAuth.AuthStateListener
    private lateinit var adapter: BlockoutTimeAdapter
    lateinit var workoutManager: WorkoutManager
    lateinit var notificationManager: NotificationManager
    private lateinit var navigationView: BottomNavigationView
    private var fragment: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeListeners()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()

        navigationView = findViewById(R.id.bottom_nav_bar)
        fragment = intent.getStringExtra(Constants.FRAGMENT_TAG)
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authListener)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_logout -> {
                logoutUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logoutUser() {
        auth.signOut()
    }

    override fun onLoginButtonPressed() {
        launchLoginUI()
    }

    private fun initializeListeners() {
        authListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if (user != null) {
                startApplication(user.uid)
                //After init code, make sure the fragment is null to reset
                fragment = null
            } else {
                switchToSplashFragment()
            }
        }
    }

    private fun switchToSplashFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, SplashFragment())
        ft.commit()
    }

    private fun startApplication(uid: String) {
        //Initialize things
        workoutManager = WorkoutManager(uid, this)

        navigationView.setOnNavigationItemSelectedListener { item ->
            val ft = supportFragmentManager.beginTransaction()
            adapter = BlockoutTimeAdapter(this, auth.currentUser!!.uid)
            when (item.itemId) {
                R.id.dashboard_icon -> ft.replace(
                    R.id.fragment_container,
                    DashboardFragment(workoutManager)
                )
                R.id.settings_icon -> ft.replace(
                    R.id.fragment_container,
                    SettingsFragment(adapter.userId)
                )
                R.id.blockout_icon -> ft.replace(
                    R.id.fragment_container,
                    BlockoutTimesFragment(adapter)
                )
                else -> super.onOptionsItemSelected(item)
            }
            ft.commit()
            //TODO remove this
            notifyNow()
            true
        }


        //Switch to home fragment if we're not in a notification
        if (fragment != null) {
            if (fragment == Constants.WORKOUT_TIMER_TAG) {
                val ft = supportFragmentManager.beginTransaction()
                //TODO change to workout
                ft.replace(
                    R.id.fragment_container,
                    WorkoutTimerFragment(Interval("Windmills", 3, 45, 15), workoutManager)
                )
                ft.commit()
            }
            //Reset
            fragment = null
        } else {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, HomeFragment())
            ft.commit()
        }

        //Schedule the alarm
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 23) //23
            set(Calendar.MINUTE, 59) //59
            set(Calendar.SECOND, 59) //59
        }

        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, WorkoutResetReciever::class.java)
        alarmIntent.putExtra(Constants.UID_TAG, uid)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, alarmIntent, 0)
//        alarm.setExact(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            pendingIntent
//        )
        alarm.setRepeating(
            AlarmManager.RTC,
            calendar.getTimeInMillis(),
            1000 * 60 * 60 * 24,
            pendingIntent
        )
    }

    private fun launchLoginUI() {
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())
        val loginIntent =
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
        startActivityForResult(loginIntent, RC_SIGN_IN)

    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel =
            NotificationChannel(Constants.CHANNEL_ID, getString(R.string.app_name), importance)
        channel.enableLights(true)
        channel.lightColor = Color.BLUE
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager.createNotificationChannel(channel)
    }

    private fun notifyNow() {
        val displayIntent = Intent(this, MainActivity::class.java)
        displayIntent.putExtra(Constants.FRAGMENT_TAG, Constants.WORKOUT_TIMER_TAG)
        val notification = workoutManager.getNotification(displayIntent)
        notificationManager.notify(101, notification)
    }


}
