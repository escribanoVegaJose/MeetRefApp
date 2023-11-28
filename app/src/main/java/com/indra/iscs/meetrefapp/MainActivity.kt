package com.indra.iscs.meetrefapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerview_roster)
        recyclerView.layoutManager = LinearLayoutManager(this)
        rosterAdapter = RosterAdapter()
        recyclerView.adapter = rosterAdapter

        val button: Button = findViewById(R.id.button_roster)
        button.setOnClickListener {
            activityScope.launch {
                connectAndGetRoster()
            }
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
                rosterAdapter.updateRoster(roster.entries.toList())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.disconnect()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }
}
