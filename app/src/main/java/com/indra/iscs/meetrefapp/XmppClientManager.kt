package com.indra.iscs.meetrefapp

import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.StanzaBuilder
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smack.roster.RosterListener
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.BareJid
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate

class XmppClientManager() {

    private var user: String? = null
    private var password: String? = null
    private lateinit var connection: AbstractXMPPConnection
    private lateinit var roster: Roster
    var rosterUpdateListener: ((List<RosterEntry>) -> Unit)? = null

    companion object {
        @Volatile
        private var instance: XmppClientManager? = null

        fun getInstance(): XmppClientManager {
            return instance ?: synchronized(this) {
                instance ?: XmppClientManager().also { instance = it }
            }
        }
    }
    fun connect(username: String, pwd: String): Boolean {
        user = username
        password = pwd
        val config = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(user, password)
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
                initializeRoster()
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

    fun notifyRosterUpdates() {
        if (connection.isConnected) {
            roster.reloadAndWait()
            val rosterEntries = roster.entries.toList()
            rosterUpdateListener?.invoke(rosterEntries)
        }
    }

    fun getRoster(): Roster {
        return roster
    }

    fun getUserJid(): String? {
        return if (connection.isAuthenticated) {
            connection.user.asEntityBareJidString()
        } else {
            null
        }
    }

    fun getUsername(): String? {
        return user
    }

    fun getPresence(jidString: String): Presence {
        val jid = JidCreate.bareFrom(jidString)
        return roster.getPresence(jid)
    }

    fun addContact(jid: String, name: String) {
        val bareJid: BareJid = JidCreate.bareFrom(jid)
        // Use the new method here
        roster.createItemAndRequestSubscription(bareJid, name, null)
    }


    fun removeContact(jid: String) {
        val bareJid: BareJid = JidCreate.bareFrom(jid)
        val entry = roster.getEntry(bareJid)
        entry?.let {
            roster.removeEntry(it)
        }
    }

    fun acceptSubscription(bareJid: String) {
        val presence = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.subscribed)
            .to(JidCreate.bareFrom(bareJid))
            .build()
        connection.sendStanza(presence)
    }

    fun rejectSubscription(bareJid: String) {
        val presence = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.unsubscribed)
            .to(JidCreate.bareFrom(bareJid))
            .build()
        connection.sendStanza(presence)
    }

    fun createGroupAndAddUser(groupId: String, userSelectedJid: String) {
        val roster = Roster.getInstanceFor(connection)
        try {
            // Convert String JID to BareJid
            val userSelectedBaredJid: BareJid = JidCreate.bareFrom(userSelectedJid)

            // Assuming group creation is done and 'groupName' represents the created group
            val groupEntry = roster.createGroup(groupId)

            // Add user to the group
            val userEntry = roster.getEntry(userSelectedBaredJid)
            userEntry?.let {
                groupEntry.addEntry(it)
            }

            // Push changes to the server
            roster.reload()

        } catch (e: Exception) {
            throw e
        }
    }

    private fun initializeRoster() {
        roster = Roster.getInstanceFor(connection)
        roster.addRosterListener(object : RosterListener {
            override fun entriesAdded(addresses: MutableCollection<Jid>?) {
                notifyRosterUpdates()
            }

            override fun entriesUpdated(addresses: MutableCollection<Jid>?) {
                notifyRosterUpdates()
            }

            override fun entriesDeleted(addresses: MutableCollection<Jid>?) {
                notifyRosterUpdates()
            }

            override fun presenceChanged(presence: Presence?) {
                notifyRosterUpdates()
            }
        })
    }
}
