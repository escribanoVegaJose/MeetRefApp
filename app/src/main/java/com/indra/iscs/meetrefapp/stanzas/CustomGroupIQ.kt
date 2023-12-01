package com.indra.iscs.meetrefapp.stanzas

import org.jivesoftware.smack.packet.IQ

class CustomGroupIQ(private val groupId: String) : IQ(ELEMENT, NAMESPACE) {

    companion object {
        const val ELEMENT = "group"
        const val NAMESPACE = "custom:group:create"
    }

    override fun getIQChildElementBuilder(builder: IQChildElementXmlStringBuilder): IQChildElementXmlStringBuilder {
        builder.rightAngleBracket()
        builder.append("<group xmlns='$NAMESPACE' id='$groupId'/>")
        return builder
    }
}