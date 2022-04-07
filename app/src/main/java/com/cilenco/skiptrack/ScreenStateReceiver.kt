package com.cilenco.skiptrack

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class ScreenStateReceiver(
    private val longPressSkipTrackService: LongPressSkipTrackService
) : BroadcastReceiver() {

    private val config: Config by lazy { Config(longPressSkipTrackService) }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_ON, Intent.ACTION_SCREEN_OFF -> {
                Timber.d("Received ${intent.action}")
                notifyService(intent.action == Intent.ACTION_SCREEN_ON)
            }
            else -> {
                Timber.w("ScreenStateReceiver received unexpected action '${intent.action}'")
            }
        }
    }

    private fun notifyService(screenOn: Boolean) {
        val activationState = config.activationState
        if ((activationState == Config.ActivationState.BOTH)
            || (screenOn && activationState == Config.ActivationState.SCREEN_ON)
            || (screenOn.not() && activationState == Config.ActivationState.SCREEN_OFF)
        ) {
            longPressSkipTrackService.startListenForLongPress()
        } else {
            longPressSkipTrackService.stopListenForLongPress()
        }
    }
}