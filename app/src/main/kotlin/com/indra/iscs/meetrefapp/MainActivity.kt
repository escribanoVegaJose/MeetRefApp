package com.indra.iscs.meetrefapp

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.indra.iscs.meetrefapp.components.fragments.ContactsFragment
import com.indra.iscs.meetrefapp.components.fragments.PendingRequestsFragment
import com.indra.iscs.meetrefapp.components.fragments.ProfileFragment
import com.indra.iscs.meetrefapp.managers.AppPreferencesManager
import com.indra.iscs.meetrefapp.managers.ConnectionType
import com.indra.iscs.meetrefapp.managers.XmppManager
import com.indra.iscs.meetrefapp.viewmodels.RosterViewModel
import com.indra.iscs.meetrefapp.viewmodels.SubscriptionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val xmppManager = XmppManager.getInstance()
    private val activityScope = CoroutineScope(Dispatchers.Main)

    private lateinit var progressBar: ProgressBar
    private lateinit var rosterViewModel: RosterViewModel
    private lateinit var subscriptionViewModel: SubscriptionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBottomNavigation()
        initViews()
        connectToServer()
        rosterViewModel = ViewModelProvider(this).get()
        xmppManager.rosterUpdateListener = {
            rosterViewModel.loadRosterEntries()
            progressBar.visibility = View.GONE

        }
        subscriptionViewModel = ViewModelProvider(this).get()
        xmppManager.subscriptionUpdateListener = {
            subscriptionViewModel.updatePendingSubscriptions(it)

            AppPreferencesManager.getInstance(this)
                .savePendingSubscriptions(it)
            progressBar.visibility = View.GONE

        }
    }

    private fun initViews() {
        progressBar = findViewById(R.id.progressBar_main)
        progressBar.visibility = View.VISIBLE
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
            if (xmppManager.connect("jose2", "1234", ConnectionType.TCP)) {
                showFragment(ContactsFragment())
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        xmppManager.disconnect()
    }
}