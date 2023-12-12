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
import org.jivesoftware.smack.roster.RosterEntry

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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRosterListener()
    }

    private fun setupRosterListener() {
        XmppClientManager.getInstance().rosterUpdateListener = { updatedRoster ->
            updateContactList(updatedRoster)  // Pasar directamente la lista de RosterEntry
        }

        // Cargar los datos actuales del roster
        val currentRoster = XmppClientManager.getInstance().getRoster().entries?.toList()
        updateContactList(currentRoster)
    }

    private fun updateContactList(entries: List<RosterEntry>?) {
        if (entries.isNullOrEmpty()) {
            textViewNoContacts.visibility = View.VISIBLE
        } else {
            textViewNoContacts.visibility = View.GONE
            rosterAdapter.updateRoster(entries)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
