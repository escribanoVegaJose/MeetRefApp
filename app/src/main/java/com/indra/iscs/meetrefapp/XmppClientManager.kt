package com.indra.iscs.meetrefapp

import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.pubsub.PubSubManager
import org.jxmpp.jid.impl.JidCreate

class XmppClientManager(private val username: String, private val password: String) {
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

    fun createGroup(groupId: String) {
        val pubSubManager = PubSubManager.getInstanceFor(connection)

        try {
            // Create the node with the specified groupId as the node identifier
            val node = pubSubManager.createNode(groupId)
            // Optional: Configure the node for shared group access

            // Handle success (perhaps update UI or notify user)
        } catch (e: XMPPException.XMPPErrorException) {
            // Handle the error if the node creation failed
        } catch (e: SmackException.NoResponseException) {
            // Handle the error if there was no response
        } catch (e: SmackException.NotConnectedException) {
            // Handle the error if the client is not connected
        } catch (e: InterruptedException) {
            // Handle the error if the thread was interrupted
        }
    }
}
