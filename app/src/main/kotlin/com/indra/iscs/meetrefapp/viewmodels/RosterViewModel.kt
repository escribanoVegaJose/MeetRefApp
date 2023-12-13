package com.indra.iscs.meetrefapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indra.iscs.meetrefapp.managers.XmppClientManager
import org.jivesoftware.smack.roster.RosterEntry

class RosterViewModel : ViewModel() {
    private val _rosterEntries = MutableLiveData<List<RosterEntry>>()
    val rosterEntries: LiveData<List<RosterEntry>> = _rosterEntries

    fun loadRosterEntries() {
        val entries = XmppClientManager.getInstance().getUpdatedRoster()
        _rosterEntries.postValue(entries)
    }
}