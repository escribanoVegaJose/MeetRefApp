package com.indra.iscs.meetrefapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.indra.iscs.meetrefapp.components.fragments.ContactsFragment
import com.indra.iscs.meetrefapp.components.fragments.PendingRequestsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.roster.Roster

class MainActivity : AppCompatActivity() {

    private val xmppClientManager = XmppClientManager.getInstance()
    private val activityScope = CoroutineScope(Dispatchers.Main)
    private lateinit var imageViewProfile: ImageView
    private lateinit var textViewJid: TextView
    private lateinit var textViewUsername: TextView
    private lateinit var roster: Roster

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connectToServer()
        initBottomNavigation()
        initViews()
    }

    private fun initViews() {
        imageViewProfile = findViewById(R.id.imageView_profile)
        textViewJid = findViewById(R.id.textView_jid)
        textViewUsername = findViewById(R.id.textView_username)
    }

    private fun initBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_contacts -> showContactsFragment()
                R.id.nav_pending_requests -> showPendingRequestsFragment()
            }
            true
        }
    }

    private fun connectToServer() {
        activityScope.launch {
            if (xmppClientManager.connect("jose2", "1234")) {
                val jid = xmppClientManager.getUserJid()
                val username = xmppClientManager.getUsername()

                withContext(Dispatchers.Main) {
                    roster = xmppClientManager.getRoster()
                    textViewJid.text = jid
                    textViewUsername.text = username
                    showContactsFragment()
                }
            } else {
                // Manejar la falla de conexión
            }
        }
    }

    private fun showContactsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ContactsFragment())
            .commit()
    }

    private fun showPendingRequestsFragment() {
        val fragment = PendingRequestsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        xmppClientManager.disconnect()
    }
}
