package com.indra.iscs.meetrefapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.jivesoftware.smack.packet.Stanza

class SubscriptionViewModel : ViewModel() {
    private val _pendingSubscriptions = MutableLiveData<List<Stanza>>()
    val pendingSubscriptions: LiveData<List<Stanza>> = _pendingSubscriptions

    fun updatePendingSubscriptions(listEntries: List<Stanza>) {
        _pendingSubscriptions.postValue(listEntries)
    }

}