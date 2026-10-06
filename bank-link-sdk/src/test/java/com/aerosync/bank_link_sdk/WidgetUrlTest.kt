package com.aerosync.bank_link_sdk

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetUrlTest {

    private val deeplink = "aerosync://bank-link/com.client.app"

    @Test
    fun buildsUrlWithEncodedDeeplink() {
        val url = buildWidgetUrl(
            WidgetConfiguration(token = "abc.def", environment = EnvironmentType.SANDBOX),
            deeplink
        )
        assertEquals(
            "https://sandbox-sync.aero.inc/?token=abc.def" +
                "&deeplink=aerosync%3A%2F%2Fbank-link%2Fcom.client.app" +
                "&handleMFA=false&manualLinkOnly=false" +
                "&version=$SYNC_VERSION&defaultTheme=light",
            url
        )
    }

    @Test
    fun leavesOutEmptyValues() {
        val url = buildWidgetUrl(
            WidgetConfiguration(token = "t", configurationId = "", jobId = null),
            deeplink
        )
        assertFalse(url.contains("configurationId"))
        assertFalse(url.contains("jobId"))
    }

    @Test
    fun includesOptionalValues() {
        val url = buildWidgetUrl(
            WidgetConfiguration(
                token = "t",
                aeroPassUserUuid = "uuid-1",
                configurationId = "cfg",
                handleMFA = true,
                jobId = "job",
                connectionId = "conn",
                defaultTheme = Theme.DARK,
            ),
            deeplink
        )
        assertTrue(url.startsWith(EnvironmentType.PROD.value + "?"))
        assertTrue(url.contains("aeroPassUserUuid=uuid-1"))
        assertTrue(url.contains("configurationId=cfg"))
        assertTrue(url.contains("handleMFA=true"))
        assertTrue(url.contains("jobId=job"))
        assertTrue(url.contains("connectionId=conn"))
        assertTrue(url.contains("defaultTheme=dark"))
    }
}
