package com.indra.iscs.meetrefapp

import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.impl.JidCreate

class XmppConnectionManager(private val username: String, private val password: String) {
    private lateinit var connection: AbstractXMPPConnection
    private lateinit var roster: Roster

    fun connect(): Boolean {
        val config = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(username, password)
            .setXmppDomain("localhost")
            .setHost("192.168.56.245")
            .setPort(5222)
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .build()

        connection = XMPPTCPConnection(config)
        return try {
            connection.connect().login()
            if (!this::roster.isInitialized) {
                roster = Roster.getInstanceFor(connection)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun disconnect() {
        connection.disconnect()
    }

    fun getRoster(): Roster? {
        return if (connection.isConnected) {
            roster.reloadAndWait()
            roster
        } else {
            null
        }
    }

    fun getUserJid(): String? {
        return if (connection.isAuthenticated) {
            connection.user.asEntityBareJidString()
        } else {
            null
        }
    }

    fun getUsername(): String {
        return username
    }

    fun getPresence(jidString: String): Presence {
        val jid = JidCreate.bareFrom(jidString)
        return roster.getPresence(jid)
    }

}
