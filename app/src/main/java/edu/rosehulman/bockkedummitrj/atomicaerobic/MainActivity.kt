package edu.rosehulman.bockkedummitrj.atomicaerobic

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.SplashFragment
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.blockouttimes.BlockoutTimeAdapter
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.blockouttimes.BlockoutTimesFragment
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.dashboard.DashboardFragment
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity(), SplashFragment.OnLoginButtonPressedListener {

    // Authentication
    private val RC_SIGN_IN = 1
    private val auth = FirebaseAuth.getInstance()
    lateinit var authListener: FirebaseAuth.AuthStateListener
    lateinit var adapter: BlockoutTimeAdapter
    lateinit var workoutManager: WorkoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeListeners()

        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_bar)

        navView.setOnNavigationItemSelectedListener { item ->

            val ft = supportFragmentManager.beginTransaction()
            adapter = BlockoutTimeAdapter(this, auth.currentUser!!.uid)
            when (item.itemId) {
                R.id.dashboard_icon -> ft.replace(R.id.fragment_container, DashboardFragment(workoutManager))
                R.id.settings_icon -> ft.replace(R.id.fragment_container, SettingsFragment(adapter.userId))
                R.id.blockout_icon -> ft.replace(
                    R.id.fragment_container,
                    BlockoutTimesFragment(adapter)
                )
                else -> super.onOptionsItemSelected(item)
            }
            ft.commit()
            true
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
                workoutManager = WorkoutManager(user.uid)
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
                //.setLogo(R.drawable.ic_launcher_custom)
                .build()
        startActivityForResult(loginIntent, RC_SIGN_IN)

    }
}
