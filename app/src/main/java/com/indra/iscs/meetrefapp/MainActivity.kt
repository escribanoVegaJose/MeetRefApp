package com.indra.iscs.meetrefapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.jivesoftware.smack.*
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smack.roster.Roster

class MainActivity : AppCompatActivity() {

    private val activityScope = CoroutineScope(Dispatchers.Main)
    private lateinit var rosterAdapter: RosterAdapter
    private lateinit var textViewNoContacts: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerview_roster)
        textViewNoContacts = findViewById(R.id.textView_no_contacts)

        recyclerView.layoutManager = LinearLayoutManager(this)
        rosterAdapter = RosterAdapter()
        recyclerView.adapter = rosterAdapter
        activityScope.launch {
            connectAndGetRoster()
        }
    }

    private suspend fun connectAndGetRoster() = withContext(Dispatchers.IO) {
        val config = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword("jose2", "1234")
            .setXmppDomain("localhost")
            .setHost("192.168.56.245")
            .setPort(5222)
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .build()

        val connection: AbstractXMPPConnection = XMPPTCPConnection(config)

        try {
            connection.connect().login()
            val roster = Roster.getInstanceFor(connection)
            roster.reloadAndWait()

            withContext(Dispatchers.Main) {
                val entries = roster.entries.toList()
                if (entries.isEmpty()) {
                    textViewNoContacts.visibility = View.VISIBLE
                } else {
                    textViewNoContacts.visibility = View.GONE
                    rosterAdapter.updateRoster(entries)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                textViewNoContacts.visibility = View.VISIBLE
                textViewNoContacts.text = getString(R.string.failed_to_load_contacts)
            }
        } finally {
            connection.disconnect()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }
}
