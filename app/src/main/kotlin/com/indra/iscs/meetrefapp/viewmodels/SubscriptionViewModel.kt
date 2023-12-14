package com.indra.iscs.meetrefapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indra.iscs.meetrefapp.models.SimpleStanzaModel

class SubscriptionViewModel : ViewModel() {
    private val _pendingSubscriptions = MutableLiveData<List<SimpleStanzaModel>>()
    val pendingSubscriptions: LiveData<List<SimpleStanzaModel>> = _pendingSubscriptions

    fun updatePendingSubscriptions(listEntries: List<SimpleStanzaModel>) {
        _pendingSubscriptions.postValue(listEntries)
    }

}