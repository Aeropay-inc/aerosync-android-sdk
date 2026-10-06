package com.aerosync.bank_link_sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Opens the Aerosync widget and reports its events to [EventListener].
 *
 * Create it as a field or in onCreate, not in a click handler.
 *
 * ```
 * private val widget = Widget(this, listener)
 * ...
 * widget.open(WidgetConfiguration(token = token))
 * ```
 */
class Widget private constructor(
    caller: ActivityResultCaller,
    owner: LifecycleOwner,
    private val contextProvider: () -> Context,
    private val listener: EventListener,
) {

    constructor(activity: ComponentActivity, listener: EventListener) :
        this(activity, activity, { activity }, listener)

    constructor(fragment: Fragment, listener: EventListener) :
        this(fragment, fragment, { fragment.requireContext() }, listener)

    private val launcher = caller.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> onResult(result) }

    init {
        // Screen recreated while its widget is open: take over the live events
        if (sessionOpen) liveListener = listener
        // Never hold on to the listener of a destroyed screen
        owner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY && liveListener === listener) {
                liveListener = null
            }
        })
    }

    fun open(configuration: WidgetConfiguration) {
        try {
            val context = contextProvider()
            val url = buildWidgetUrl(configuration, syncDeeplink(context))
            launcher.launch(
                Intent(context, WidgetActivity::class.java)
                    .putExtra(WidgetActivity.EXTRA_URL, url)
            )
            liveListener = listener
            sessionOpen = true
        } catch (e: Exception) {
            listener.onError("Error | $ERROR_WIDGET_LOAD | ${e.message}")
        }
    }

    private fun onResult(result: ActivityResult) {
        sessionOpen = false
        liveListener = null
        @Suppress("DEPRECATION")
        val success = result.data?.getParcelableExtra<PayloadSuccessType>(WidgetActivity.EXTRA_SUCCESS)
        if (result.resultCode == Activity.RESULT_OK && success != null) {
            listener.onSuccess(success)
        } else {
            // closed by the user, or the widget could not continue
            listener.onClose()
        }
    }

    internal companion object {
        // Listener for the live events (onEvent / onError) of the open widget
        @Volatile
        var liveListener: EventListener? = null

        // A widget is open and its result has not been delivered yet
        @Volatile
        var sessionOpen = false
    }
}
