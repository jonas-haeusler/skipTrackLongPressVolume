package com.cilenco.skiptrack

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

/**
 * Start the foreground service if either [Intent.ACTION_BOOT_COMPLETED] or [Intent.ACTION_MY_PACKAGE_REPLACED] is received.
 */
class AutoStartSkipTrackReceiver : BroadcastReceiver() {

    private val allowedIntentActions = listOf(Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in allowedIntentActions) {
            Timber.w("Discarding broadcast for auto-start receiver due to mismatched intent-action. Expected one of $allowedIntentActions but was '${intent.action}'")
            return
        }

        if (Config(context).serviceEnabled) {
            Timber.i("Starting foreground service due to received '${intent.action}'")
            context.startLongPressSkipTrackService()
        } else {
            Timber.i("Received '${intent.action}' but service is disabled. Discarding action.")
        }
    }

}