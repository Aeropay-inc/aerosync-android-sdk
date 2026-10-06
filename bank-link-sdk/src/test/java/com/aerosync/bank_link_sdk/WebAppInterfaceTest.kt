package com.aerosync.bank_link_sdk

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WebAppInterfaceTest {

    private class RecordingListener : EventListener {
        var success: PayloadSuccessType? = null
        var event: PayloadEventType? = null
        val errors = mutableListOf<String>()
        var closed = false

        override fun onSuccess(event: PayloadSuccessType) { success = event }
        override fun onEvent(event: PayloadEventType) { this.event = event }
        override fun onError(error: String) { errors += error }
        override fun onClose() { closed = true }
    }

    private val listener = RecordingListener()
    private val bridge = WebAppInterface(listener)

    @Test
    fun parsesSingleAccountSuccess() {
        bridge.streamEvents(
            """{"type":"pageSuccess","payload":{"connectionId":"c1","clientName":"Acme","aeroPassUserUuid":"u1"}}"""
        )
        assertEquals(PayloadSuccessType("c1", "Acme", "u1"), listener.success)
    }

    @Test
    fun parsesMultiAccountSuccess() {
        bridge.streamEvents(
            """{"type":"pageSuccess","payload":{"clientName":"Acme","aeroPassUserUuid":"u1",
              "accounts":[{"connectionId":"c1","accountType":"checking","accountNumberDisplay":"..1234"}]}}"""
        )
        val success = listener.success!!
        assertNull(success.connectionId)
        assertEquals(listOf(PayloadSuccessAccount("c1", "checking", "..1234")), success.accounts)
    }

    @Test
    fun pageLoadedValuesHaveNoQuotes() {
        bridge.streamEvents(
            """{"type":"widgetPageLoaded","payload":{"pageTitle":"Choose bank","onLoadApi":"/verify"}}"""
        )
        assertEquals(PayloadEventType("Choose bank", "/verify"), listener.event)
    }

    @Test
    fun errorPayloadIsPassedAsPlainString() {
        bridge.streamEvents("""{"type":"widgetError","payload":"Code: AC-500"}""")
        assertEquals(listOf("Code: AC-500"), listener.errors)
    }

    @Test
    fun close() {
        bridge.streamEvents("""{"type":"widgetClose","payload":{}}""")
        assertTrue(listener.closed)
    }

    @Test
    fun reportsInvalidJsonAndUnknownType() {
        bridge.streamEvents("not json")
        bridge.streamEvents("""{"type":"somethingElse"}""")
        assertEquals(2, listener.errors.size)
    }

    @Test
    fun reportsMalformedSuccessPayload() {
        bridge.streamEvents("""{"type":"pageSuccess","payload":{"clientName":"Acme"}}""")
        assertNull(listener.success)
        assertEquals(1, listener.errors.size)
    }
}
