package edu.rosehulman.bockkedummitrj.atomicaerobic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.SplashFragment
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.blockouttimes.BlockoutTimesFragment
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.dashboard.DashboardFragment
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity(), SplashFragment.OnLoginButtonPressedListener {

    // Authentication
    private val RC_SIGN_IN = 1
    private val auth = FirebaseAuth.getInstance()
    lateinit var authListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeListeners()

        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_bar)

        navView.setOnNavigationItemSelectedListener { item ->

            val ft = supportFragmentManager.beginTransaction()
            when (item.itemId) {
                R.id.dashboard_icon -> ft.replace(R.id.fragment_container, DashboardFragment())
                R.id.settings_icon -> ft.replace(R.id.fragment_container, SettingsFragment())
                R.id.blockout_icon -> ft.replace(
                    R.id.fragment_container,
                    BlockoutTimesFragment()
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

    override fun onLoginButtonPressed() {
        launchLoginUI()
    }

    private fun initializeListeners() {
        authListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if (user != null) {
                switchToDashboard()
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

    private fun switchToDashboard() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, DashboardFragment())
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