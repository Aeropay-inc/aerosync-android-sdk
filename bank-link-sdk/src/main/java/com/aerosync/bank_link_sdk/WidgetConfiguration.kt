package com.aerosync.bank_link_sdk

import java.net.URLEncoder

/**
 * Options for a widget session, passed to [Widget.open].
 */
data class WidgetConfiguration @JvmOverloads constructor(
    val token: String,
    val environment: EnvironmentType = EnvironmentType.PROD,
    val aeroPassUserUuid: String? = null,
    val configurationId: String? = null,
    val handleMFA: Boolean = false,
    val manualLinkOnly: Boolean = false,
    val jobId: String? = null,
    val connectionId: String? = null,
    val defaultTheme: Theme = Theme.LIGHT,
)

/**
 * Builds the widget URL for [configuration]. Empty values are left out and
 * every value is URL-encoded (the deeplink contains ':' and '/').
 */
internal fun buildWidgetUrl(configuration: WidgetConfiguration, deeplink: String): String {
    val params = linkedMapOf(
        "token" to configuration.token,
        "aeroPassUserUuid" to configuration.aeroPassUserUuid,
        "deeplink" to deeplink,
        "configurationId" to configuration.configurationId,
        "handleMFA" to configuration.handleMFA.toString(),
        "manualLinkOnly" to configuration.manualLinkOnly.toString(),
        "connectionId" to configuration.connectionId,
        "jobId" to configuration.jobId,
        "version" to SYNC_VERSION,
        "defaultTheme" to configuration.defaultTheme.toString(),
    )
    val query = params
        .filterValues { !it.isNullOrEmpty() }
        .map { (key, value) -> "$key=${URLEncoder.encode(value, "UTF-8")}" }
        .joinToString("&")
    return "${configuration.environment.value}?$query"
}
