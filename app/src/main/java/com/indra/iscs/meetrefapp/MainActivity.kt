package com.indra.iscs.meetrefapp

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.indra.iscs.meetrefapp.components.RosterAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val activityScope = CoroutineScope(Dispatchers.Main)
    private lateinit var rosterAdapter: RosterAdapter
    private lateinit var textViewNoContacts: TextView
    private lateinit var imageViewProfile: ImageView
    private lateinit var textViewJid: TextView
    private lateinit var textViewUsername: TextView

    private val xmppConnectionManager = XmppConnectionManager("jose2", "1234")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        activityScope.launch {
            connectAndGetRoster()
        }
    }

    private fun initViews() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview_roster)
        recyclerView.layoutManager = LinearLayoutManager(this)
        rosterAdapter = RosterAdapter()
        recyclerView.adapter = rosterAdapter
        imageViewProfile = findViewById(R.id.imageView_profile)
        textViewJid = findViewById(R.id.textView_jid)
        textViewUsername = findViewById(R.id.textView_username)
        textViewNoContacts = findViewById(R.id.textView_no_contacts)
    }

    private suspend fun connectAndGetRoster() {
        if (xmppConnectionManager.connect()) {
            val roster = xmppConnectionManager.getRoster()
            val jid = xmppConnectionManager.getUserJid()
            val username = xmppConnectionManager.getUsername()

            withContext(Dispatchers.Main) {
                textViewJid.text = jid
                textViewUsername.text = username

                roster?.let {
                    val entries = it.entries.toList()
                    if (entries.isEmpty()) {
                        textViewNoContacts.visibility = View.VISIBLE
                    } else {
                        textViewNoContacts.visibility = View.GONE
                        rosterAdapter.updateRoster(entries)
                    }
                } ?: run {
                    textViewNoContacts.visibility = View.VISIBLE
                    textViewNoContacts.text = getString(R.string.failed_to_load_contacts)
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                textViewNoContacts.visibility = View.VISIBLE
                textViewNoContacts.text = getString(R.string.failed_to_load_contacts)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        xmppConnectionManager.disconnect()
        activityScope.cancel()
    }
}
