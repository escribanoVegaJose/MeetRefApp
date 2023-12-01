package com.indra.iscs.meetrefapp.stanzas

import org.jivesoftware.smack.packet.IQ

class AddUserToGroupIQ(private val userJid: String, private val groupId: String) : IQ(
    CustomGroupIQ.ELEMENT,
    CustomGroupIQ.NAMESPACE
) {

    companion object {
        private const val ELEMENT = "group"
        private const val NAMESPACE = "custom:group:add"
    }

    override fun getIQChildElementBuilder(
        iqChildElementXmlStringBuilder: IQChildElementXmlStringBuilder
    ): IQChildElementXmlStringBuilder {
        iqChildElementXmlStringBuilder.rightAngleBracket()
        iqChildElementXmlStringBuilder.append("<")
        iqChildElementXmlStringBuilder.append(ELEMENT)
        iqChildElementXmlStringBuilder.append(" xmlns='")
        iqChildElementXmlStringBuilder.append(NAMESPACE)
        iqChildElementXmlStringBuilder.append("' jid='")
        iqChildElementXmlStringBuilder.escape(userJid)
        iqChildElementXmlStringBuilder.append("' group-id='")
        iqChildElementXmlStringBuilder.escape(groupId)
        iqChildElementXmlStringBuilder.append("'/>")
        return iqChildElementXmlStringBuilder
    }
}
