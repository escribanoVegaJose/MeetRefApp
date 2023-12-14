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
import com.indra.iscs.meetrefapp.managers.XmppClientManager
import com.indra.iscs.meetrefapp.viewmodels.RosterViewModel
import com.indra.iscs.meetrefapp.viewmodels.SubscriptionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val xmppClientManager = XmppClientManager.getInstance()
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
        xmppClientManager.rosterUpdateListener = {
            rosterViewModel.loadRosterEntries()
        }
        subscriptionViewModel = ViewModelProvider(this).get()
        xmppClientManager.subscriptionUpdateListener={
            subscriptionViewModel.updatePendingSubscriptions(it)
        }
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
            if (xmppClientManager.connect("jose2", "1234", applicationContext)) {
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