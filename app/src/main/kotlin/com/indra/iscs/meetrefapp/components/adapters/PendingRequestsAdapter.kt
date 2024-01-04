package com.indra.iscs.meetrefapp.components.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.indra.iscs.meetrefapp.R
import com.indra.iscs.meetrefapp.managers.XmppManager
import com.indra.iscs.meetrefapp.models.SimpleStanzaModel

class PendingRequestsAdapter(private var pendingRequests: List<SimpleStanzaModel>) :
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
        val stanza = pendingRequests[position]
        holder.textViewJid.text = stanza.from
        holder.itemView.setOnClickListener {
            showFriendRequestDialog(holder.itemView.context, stanza)
        }
    }

    private fun showFriendRequestDialog(context: Context, stanza: SimpleStanzaModel) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.friend_request))
            .setMessage(
                context.getString(
                    R.string.tittle_friendly_request,
                    stanza.from
                )
            )
            .setPositiveButton(context.getString(R.string.accept)) { dialog, _ ->
                try {
                    XmppManager.getInstance()
                        .acceptSubscription(stanza.from)
                    XmppManager.getInstance().requestSubscription(
                        stanza.from,
                        XmppManager.getInstance().getUserJid()
                    )
                    XmppManager.getInstance().setIsWaitingToEntriesSubscribe(true)
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_adding_user), Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.reject)) { dialog, _ ->
                XmppManager.getInstance()
                    .rejectSubscription(stanza.from, XmppManager.getInstance().getUserJid())
                dialog.dismiss()
            }
            .show()
    }

    override fun getItemCount(): Int = pendingRequests.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateRequests(newRequests: List<SimpleStanzaModel>) {
        pendingRequests = newRequests
        notifyDataSetChanged()
    }
}