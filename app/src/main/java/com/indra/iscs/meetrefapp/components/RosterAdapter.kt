package com.indra.iscs.meetrefapp.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.indra.iscs.meetrefapp.R
import org.jivesoftware.smack.roster.RosterEntry

class RosterAdapter : RecyclerView.Adapter<RosterAdapter.RosterViewHolder>() {

    private var rosterList: List<RosterEntry> = listOf()

    class RosterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewJid: TextView = view.findViewById(R.id.textViewJid)
        val textViewName: TextView = view.findViewById(R.id.textViewName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.roster_item, parent, false)
        return RosterViewHolder(view)
    }

    override fun onBindViewHolder(holder: RosterViewHolder, position: Int) {
        val rosterEntry = rosterList[position]
        holder.textViewJid.text = rosterEntry.jid.asUnescapedString()
        holder.textViewName.text = rosterEntry.name ?: "No name available"
    }

    override fun getItemCount(): Int = rosterList.size

    fun updateRoster(newRoster: List<RosterEntry>) {
        rosterList = newRoster
        notifyDataSetChanged()
    }
}
