package com.indra.iscs.meetrefapp.components

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.indra.iscs.meetrefapp.R
import com.indra.iscs.meetrefapp.XmppClientManager
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.RosterEntry
import java.util.UUID

class RosterAdapter(
    private val xmppClientManager: XmppClientManager,
    private val context: Context
) : RecyclerView.Adapter<RosterAdapter.RosterViewHolder>() {

    private var rosterList: List<RosterEntry> = listOf()

    class RosterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewJid: TextView = view.findViewById(R.id.textViewJid)
        val textViewName: TextView = view.findViewById(R.id.textViewName)
        val presenceIndicator: View = view.findViewById(R.id.presence_indicator)
        val optionsMenu: ImageButton = view.findViewById(R.id.options_menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.roster_item, parent, false)
        return RosterViewHolder(view)
    }

    override fun onBindViewHolder(holder: RosterViewHolder, position: Int) {
        val rosterEntry = rosterList[position]
        holder.textViewJid.text = rosterEntry.jid.asUnescapedString()
        holder.textViewName.text = rosterEntry.name ?: context.getString(R.string.no_name_available)
        val presence = xmppClientManager.getPresence(rosterEntry.jid.asUnescapedString())
        when {
            presence.isAvailable && presence.mode == Presence.Mode.available -> holder.presenceIndicator.setBackgroundResource(
                R.drawable.presence_online
            )

            presence.isAvailable && presence.mode == Presence.Mode.away -> holder.presenceIndicator.setBackgroundResource(
                R.drawable.presence_away
            )

            presence.isAvailable && presence.mode == Presence.Mode.dnd -> holder.presenceIndicator.setBackgroundResource(
                R.drawable.presence_busy
            )

            else -> holder.presenceIndicator.setBackgroundResource(R.drawable.presence_offline)
        }
        holder.optionsMenu.setOnClickListener { view ->
            showPopupMenu(view, position)
        }
    }

    private fun showPopupMenu(view: View, position: Int) {
        val popup = PopupMenu(context, view)
        popup.menuInflater.inflate(R.menu.roster_item_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_create_group -> {
                    showCreateGroupDialog(position)
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

    private fun showCreateGroupDialog(position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_group, null)
        val editTextGroupName = dialogView.findViewById<EditText>(R.id.editTextGroupName)

        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.create_group))
            .setView(dialogView)
            .setPositiveButton(context.getString(R.string.create)) { dialog, _ ->
                val groupName = editTextGroupName.text.toString().trim()
                if (groupName.isNotEmpty()) {
                    val selectedEntry = rosterList[position]
                    val userSelectedJid = selectedEntry.jid.asUnescapedString()
//                    val currentUserJid = xmppClientManager.getUserJid()
                    val uniqueId = UUID.randomUUID().toString()
                    val groupId =
                        "${groupName}/${uniqueId}"
                    userSelectedJid
                        ?.let { xmppClientManager.createGroupAndAddUser(groupName,groupId, it) }
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.group_name_cannot_be_empty), Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    override fun getItemCount(): Int = rosterList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateRoster(newRoster: List<RosterEntry>) {
        rosterList = newRoster
        notifyDataSetChanged()
    }
}
