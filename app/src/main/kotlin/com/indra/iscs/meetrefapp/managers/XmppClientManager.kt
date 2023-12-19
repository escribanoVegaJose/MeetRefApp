package com.indra.iscs.meetrefapp.managers

import android.content.Context
import com.indra.iscs.meetrefapp.models.SimpleStanzaModel
import com.indra.iscs.meetrefapp.utils.Constants
import com.indra.iscs.meetrefapp.utils.StanzaUtils
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.filter.StanzaTypeFilter
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Stanza
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
    private var isWaitingToEntriesSubscribe = false
    var rosterUpdateListener: (() -> Unit)? = null
    var subscriptionUpdateListener: ((List<SimpleStanzaModel>) -> Unit)? = null
    val pendingRosterEntries = mutableListOf<SimpleStanzaModel>()

    companion object {
        @Volatile
        private var instance: XmppClientManager? = null

        fun getInstance(): XmppClientManager {
            return instance ?: synchronized(this) {
                instance ?: XmppClientManager().also { instance = it }
            }
        }
    }

    fun connect(username: String, pwd: String, context: Context): Boolean {
        user = username
        password = pwd
        val config = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(user, password)
            .setXmppDomain(Constants.DOMAIN)
            .setHost("192.168.56.245")
            .setPort(5222)
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .build()

        connection = XMPPTCPConnection(config)
        return try {
            connection.connect().login()
            if (!this::roster.isInitialized) {
                roster = Roster.getInstanceFor(connection)
                roster.subscriptionMode = Roster.SubscriptionMode.manual
                initializeRoster(context)
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
            rosterUpdateListener?.invoke()
        }
    }

    fun getUpdatedRoster(): List<RosterEntry> {
        if (connection.isConnected) {
            roster.reloadAndWait()
            return roster.entries.toList()
        }
        return listOf()
    }

    fun getPendingRequests(): List<RosterEntry> {
        return if (this::roster.isInitialized) {
            roster.unfiledEntries.filter {
                it.isSubscriptionPending
            }
        } else {
            emptyList()
        }
    }

    fun getUserJid(): String? {
        return if (connection.isAuthenticated) {
            connection.user.asEntityBareJidString()
        } else {
            null
        }
    }

    fun setIsWaitingToEntriesSubscribe(value: Boolean) {
        isWaitingToEntriesSubscribe = value
    }

    fun getUsername(): String? {
        return user
    }

    fun getPresence(jidString: String): Presence {
        val jid = JidCreate.bareFrom(jidString)
        return roster.getPresence(jid)
    }

    fun removeContact(jid: String?) {
        val bareJid: BareJid = JidCreate.bareFrom(jid)
        val entry = roster.getEntry(bareJid)
        entry?.let {
            roster.removeEntry(it)
        }
    }

    fun requestSubscription(jid: String?) {
        try {
            val bareJid = JidCreate.bareFrom(jid)
            val presence = StanzaBuilder.buildPresence()
                .ofType(Presence.Type.subscribe)
                .to(bareJid)
                .build()
            connection.sendStanza(presence)
        } catch (e: Exception) {
            throw e
        }
    }

    fun requestSubscription(jidTo: String?, jidFrom: String?) {
        try {
            val jidToBare = JidCreate.bareFrom(jidTo)
            val jidFromBareJid = JidCreate.bareFrom(jidFrom)
            val presence = StanzaBuilder.buildPresence()
                .ofType(Presence.Type.subscribe)
                .to(jidToBare)
                .from(jidFromBareJid)
                .build()
            connection.sendStanza(presence)
        } catch (e: Exception) {
            throw e
        }
    }

    fun acceptSubscription(jidTo: String?) {
        try {
            val bareJid: BareJid = JidCreate.bareFrom(jidTo)
            val presence = StanzaBuilder.buildPresence()
                .ofType(Presence.Type.subscribed)
                .to(JidCreate.bareFrom(bareJid))
                .build()
            connection.sendStanza(presence)
            pendingRosterEntries.removeAll { it.from == jidTo }
            subscriptionUpdateListener?.invoke(pendingRosterEntries)
        } catch (e: Exception) {
            throw e
        }
    }

    fun acceptSubscription(jidTo: String?, jidFrom: String?) {
        try {
            val bareJid: BareJid = JidCreate.bareFrom(jidTo)
            val bareJidFrom: BareJid = JidCreate.bareFrom(jidFrom)
            val presence = StanzaBuilder.buildPresence()
                .ofType(Presence.Type.subscribed)
                .from(bareJidFrom)
                .to(JidCreate.bareFrom(bareJid))
                .build()
            connection.sendStanza(presence)
            pendingRosterEntries.removeAll { it.from == jidTo }
            subscriptionUpdateListener?.invoke(pendingRosterEntries)
        } catch (e: Exception) {
            throw e
        }
    }

    fun cancelSubscription(jidTo: String?) {
        val bareJid: BareJid = JidCreate.bareFrom(jidTo)
        val presence = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.unsubscribe)
            .to(bareJid)
            .build()
        pendingRosterEntries.removeAll { it.from == jidTo }
        subscriptionUpdateListener?.invoke(pendingRosterEntries)
        connection.sendStanza(presence)
    }

    fun cancelSubscription(jidTo: String?, jidFrom: String?) {
        val bareJid: BareJid = JidCreate.bareFrom(jidTo)
        val bareJidFrom: BareJid = JidCreate.bareFrom(jidFrom)
        val presence = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.unsubscribe)
            .to(bareJid)
            .from(bareJidFrom)
            .build()
        pendingRosterEntries.removeAll { it.from == jidTo }
        subscriptionUpdateListener?.invoke(pendingRosterEntries)
        connection.sendStanza(presence)
    }

    fun rejectSubscription(jidTo: String?, jidFrom: String?) {
        val bareJidFrom: BareJid = JidCreate.bareFrom(jidFrom)
        val bareJidTo: BareJid = JidCreate.bareFrom(jidTo)
        val presence = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.unsubscribed)
            .to(bareJidTo)
            .from(bareJidFrom)
            .build()
        connection.sendStanza(presence)

        // Eliminar la entrada correspondiente de la lista de suscripciones pendientes
//        pendingRosterEntries.removeAll { it.from == jidFrom }
//        subscriptionUpdateListener?.invoke(pendingRosterEntries)
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

    private fun initializeRoster(context: Context) {
        roster = Roster.getInstanceFor(connection)

        roster.addRosterListener(object : RosterListener {
            override fun entriesAdded(addresses: MutableCollection<Jid>?) {
                notifyRosterUpdates()
            }

            override fun entriesUpdated(addresses: MutableCollection<Jid>?) {
                if (isWaitingToEntriesSubscribe && !addresses.isNullOrEmpty()) {
                    val firstAddress = addresses.first()
                    acceptSubscription(firstAddress.toString(), getUserJid())
                    isWaitingToEntriesSubscribe = false
                }
                notifyRosterUpdates()
            }

            override fun entriesDeleted(addresses: MutableCollection<Jid>?) {
                notifyRosterUpdates()
            }

            override fun presenceChanged(presence: Presence?) {
                notifyRosterUpdates()
            }

        })
        val presenceFilter = StanzaTypeFilter(Presence::class.java)
        connection.addAsyncStanzaListener({ stanza ->
            if (stanza is Presence && stanza.type == Presence.Type.subscribe) {
                addPendingSubscriptionRequest(context, stanza)
            }
        }, presenceFilter)
    }

    private fun addPendingSubscriptionRequest(context: Context, stanza: Stanza) {
        val fromJid = stanza.from.asBareJid()
        if (!pendingRosterEntries.any { it.from == fromJid.toString() }) {
            val simpleStanza = SimpleStanzaModel(
                id = stanza.stanzaId,
                to = stanza.to?.toString(),
                from = fromJid.toString(),
                type = StanzaUtils.determineStanzaType(stanza)
            )
            pendingRosterEntries.add(simpleStanza)
            AppPreferencesManager.getInstance(context)
                .savePendingSubscriptions(pendingRosterEntries)
            subscriptionUpdateListener?.invoke(pendingRosterEntries)
        }
    }

}
