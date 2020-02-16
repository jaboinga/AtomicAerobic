package edu.rosehulman.bockkedummitrj.atomicaerobic

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
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
    lateinit var authListener: FirebaseAuth.AuthStateListener
    lateinit var adapter: BlockoutTimeAdapter
    lateinit var workoutManager: WorkoutManager
    lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()

        initializeListeners()

        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_bar)

        navView.setOnNavigationItemSelectedListener { item ->

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

        val fragment = intent.getStringExtra(Constants.FRAGMENT_TAG)
        if (fragment != null) {
            if (fragment == Constants.WORKOUT_TIMER_TAG) {
                val ft = supportFragmentManager.beginTransaction()
                //TODO change to workout
                Log.d(Constants.TAG, "workout manager is: ${workoutManager.toString()}")
                ft.replace(R.id.fragment_container, WorkoutTimerFragment(Interval("Windmills", 3, 45, 450), workoutManager))
                ft.commit()
            }
        }

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {

            R.id.action_logout -> {
                logoutUser()
                true
            }
            R.id.action_add_blockout_time -> {
                adapter.showAddDialog()
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
                //TODO is this going to be a problem later? we need the workouts to persist
                workoutManager = WorkoutManager(user.uid, this)
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
        val channel = NotificationChannel(Constants.CHANNEL_ID, getString(R.string.app_name), importance)
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
