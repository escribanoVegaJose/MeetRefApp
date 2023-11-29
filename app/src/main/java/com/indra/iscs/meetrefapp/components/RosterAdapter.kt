package com.indra.iscs.meetrefapp.components

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.indra.iscs.meetrefapp.R
import com.indra.iscs.meetrefapp.XmppConnectionManager
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.RosterEntry

class RosterAdapter(private val xmppConnectionManager: XmppConnectionManager, private val context: Context) : RecyclerView.Adapter<RosterAdapter.RosterViewHolder>() {

    private var rosterList: List<RosterEntry> = listOf()

    class RosterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewJid: TextView = view.findViewById(R.id.textViewJid)
        val textViewName: TextView = view.findViewById(R.id.textViewName)
        val presenceIndicator: View = view.findViewById(R.id.presence_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.roster_item, parent, false)
        return RosterViewHolder(view)
    }

    override fun onBindViewHolder(holder: RosterViewHolder, position: Int) {
        val rosterEntry = rosterList[position]
        holder.textViewJid.text = rosterEntry.jid.asUnescapedString()
        holder.textViewName.text = rosterEntry.name ?: context.getString(R.string.no_name_available)
        val presence = xmppConnectionManager.getPresence(rosterEntry.jid.asUnescapedString())
        when {
            presence.isAvailable && presence.mode == Presence.Mode.available -> holder.presenceIndicator.setBackgroundResource(R.drawable.presence_online)
            presence.isAvailable && presence.mode == Presence.Mode.away -> holder.presenceIndicator.setBackgroundResource(R.drawable.presence_away)
            presence.isAvailable && presence.mode == Presence.Mode.dnd -> holder.presenceIndicator.setBackgroundResource(R.drawable.presence_busy)
            else -> holder.presenceIndicator.setBackgroundResource(R.drawable.presence_offline)
        }
    }

    override fun getItemCount(): Int = rosterList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateRoster(newRoster: List<RosterEntry>) {
        rosterList = newRoster
        notifyDataSetChanged()
    }
}
