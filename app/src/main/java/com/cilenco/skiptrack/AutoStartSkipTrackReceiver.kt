package com.cilenco.skiptrack

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class AutoStartSkipTrackReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            Timber.w("Discarding broadcast for auto-start receiver to to mismatched intent-action. Expected '${Intent.ACTION_BOOT_COMPLETED}' but was '${intent.action}'")
            return
        }

        Timber.i("Starting foreground service due to received ${intent.action}")
        context.startLongPressSkipTrackService()
    }

}