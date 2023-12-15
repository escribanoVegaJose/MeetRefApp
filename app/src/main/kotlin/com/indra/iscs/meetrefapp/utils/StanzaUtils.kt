package com.indra.iscs.meetrefapp.utils

import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Stanza

class StanzaUtils {
    companion object {
        fun determineStanzaType(stanza: Stanza): String? {
            return when (stanza) {
                is Message -> "message"
                is IQ -> "iq"
                is Presence -> "presence"
                else -> null
            }
        }
        
    }
}
