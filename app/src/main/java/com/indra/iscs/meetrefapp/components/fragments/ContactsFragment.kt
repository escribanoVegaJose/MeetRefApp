package com.indra.iscs.meetrefapp.components.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.indra.iscs.meetrefapp.R
import com.indra.iscs.meetrefapp.XmppClientManager
import com.indra.iscs.meetrefapp.components.adapters.RosterAdapter

class ContactsFragment : Fragment() {

    private lateinit var rosterAdapter: RosterAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewNoContacts: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        recyclerView = view.findViewById(R.id.recyclerview_roster)
        textViewNoContacts = view.findViewById(R.id.textView_no_contacts)
        recyclerView.layoutManager = LinearLayoutManager(context)
        rosterAdapter = RosterAdapter(XmppClientManager.getInstance(), requireContext())
        recyclerView.adapter = rosterAdapter
        XmppClientManager.getInstance().rosterUpdateListener = { updatedRoster ->
                rosterAdapter.updateRoster(updatedRoster)
                textViewNoContacts.visibility = if (updatedRoster.isEmpty()) View.VISIBLE else View.GONE
        }
        updateContactList()
        return view
    }

    private fun updateContactList() {
        val roster =XmppClientManager.getInstance().getRoster()
        val entries = roster.entries?.toList()
        if (entries.isNullOrEmpty()) {
            textViewNoContacts.visibility = View.VISIBLE
        } else {
            textViewNoContacts.visibility = View.GONE
            rosterAdapter.updateRoster(entries)
        }
    }
}
