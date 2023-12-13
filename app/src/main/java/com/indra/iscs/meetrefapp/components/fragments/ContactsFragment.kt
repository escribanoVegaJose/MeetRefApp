package com.indra.iscs.meetrefapp.components.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.indra.iscs.meetrefapp.R
import com.indra.iscs.meetrefapp.managers.XmppClientManager
import com.indra.iscs.meetrefapp.components.adapters.RosterAdapter
import org.jivesoftware.smack.roster.RosterEntry

class ContactsFragment : Fragment() {

    private lateinit var rosterAdapter: RosterAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewNoContacts: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        initViews(view)
        return view
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerview_roster)
        textViewNoContacts = view.findViewById(R.id.textView_no_contacts)
        progressBar = view.findViewById(R.id.progressBar_contacts)
        recyclerView.layoutManager = LinearLayoutManager(context)
        rosterAdapter = RosterAdapter(XmppClientManager.getInstance(), requireContext())
        recyclerView.adapter = rosterAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRosterListener()
    }

    private fun setupRosterListener() {
        XmppClientManager.getInstance().rosterUpdateListener = { updatedRoster ->
            activity?.runOnUiThread {
                updateContactList(updatedRoster)
                progressBar.visibility = View.GONE
            }
        }
        updateContactList(getCurrentUser())
    }

    private fun getCurrentUser(): List<RosterEntry>? {
        return XmppClientManager.getInstance().getRoster().entries?.toList()
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
        progressBar.visibility = View.GONE
    }
}