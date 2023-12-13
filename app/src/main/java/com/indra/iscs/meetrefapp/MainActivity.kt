package com.indra.iscs.meetrefapp

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.indra.iscs.meetrefapp.components.fragments.ContactsFragment
import com.indra.iscs.meetrefapp.components.fragments.PendingRequestsFragment
import com.indra.iscs.meetrefapp.components.fragments.ProfileFragment
import com.indra.iscs.meetrefapp.managers.XmppClientManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val xmppClientManager = XmppClientManager.getInstance()
    private val activityScope = CoroutineScope(Dispatchers.Main)

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBottomNavigation()
        initViews()
        connectToServer()
    }

    private fun initViews() {
        progressBar = findViewById(R.id.progressBar_main)
    }

    private fun initBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_contacts -> showFragment(ContactsFragment())
                R.id.nav_pending_requests -> showFragment(PendingRequestsFragment())
                R.id.nav_profile -> showFragment(ProfileFragment())
            }
            true
        }
    }

    private fun connectToServer() {
        activityScope.launch {
            if (xmppClientManager.connect("jose2", "1234")) {
                showFragment(ContactsFragment())
            } else {
                // Connection failed
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        progressBar.visibility = View.GONE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        xmppClientManager.disconnect()
    }
}