package com.indra.iscs.meetrefapp.managers

interface XmppClientCallback {
    fun onSuccess()
    fun onFailure() {
    }
}
