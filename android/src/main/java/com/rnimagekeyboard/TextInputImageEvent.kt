package com.rnimagekeyboard

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.RCTEventEmitter

data class TextInputImageEvent(
        val viewID : Int,
        val uri : String,
        val linkUri : String?,
        val data : String?,
        val mime : String?
) : Event<TextInputImageEvent>(viewID) {

    companion object {
        const val EVENT = "topImageChangeEvent"
    }

    override fun getEventName() = EVENT

    override fun dispatch(rctEventEmitter: RCTEventEmitter?) {
        rctEventEmitter?.receiveEvent(viewTag, eventName, serializeEventData())
    }

    private fun serializeEventData(): WritableMap? {
        val eventData = Arguments.createMap()
        eventData.putString("uri", uri)
        eventData.putString("linkUri", linkUri)
        eventData.putString("data", data)
        eventData.putString("mime", mime)
        eventData.putInt("target", viewTag)
        return eventData
    }
}
