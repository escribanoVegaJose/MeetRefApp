package com.indra.iscs.meetrefapp.managers

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.indra.iscs.meetrefapp.models.SimpleStanzaModel

class AppPreferencesManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val pendingSubscriptionsKey = "PENDING_SUBSCRIPTIONS"

    companion object {
        @Volatile
        private var INSTANCE: AppPreferencesManager? = null

        fun getInstance(context: Context): AppPreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppPreferencesManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    fun savePendingSubscriptions(stanzas: List<SimpleStanzaModel>) {
        val json = gson.toJson(stanzas)
        sharedPreferences.edit().putString(pendingSubscriptionsKey, json).apply()
    }

    fun loadPendingSubscriptions(): List<SimpleStanzaModel> {
        val json = sharedPreferences.getString(pendingSubscriptionsKey, null)
        return if (json != null) {
            val type = object : TypeToken<List<SimpleStanzaModel>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

}
