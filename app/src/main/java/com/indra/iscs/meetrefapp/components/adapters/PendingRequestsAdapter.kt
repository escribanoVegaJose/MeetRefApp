package com.indra.iscs.meetrefapp.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.indra.iscs.meetrefapp.R
import org.jivesoftware.smack.roster.RosterEntry

class PendingRequestsAdapter(private var pendingRequests: List<RosterEntry>) :
    RecyclerView.Adapter<PendingRequestsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewJid: TextView = view.findViewById(R.id.textView_jid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pending_request_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rosterEntry = pendingRequests[position]
        holder.textViewJid.text = rosterEntry.jid.asUnescapedString()
    }

    override fun getItemCount(): Int = pendingRequests.size

    fun updateRequests(newRequests: List<RosterEntry>) {
        pendingRequests = newRequests
        notifyDataSetChanged()
    }
}
