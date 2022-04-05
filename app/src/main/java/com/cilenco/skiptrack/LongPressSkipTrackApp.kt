package com.cilenco.skiptrack

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import timber.log.Timber

@Suppress("unused")
class LongPressSkipTrackApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // TODO: plant a tree for release builds :-)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        createNotificationChannel()
    }

    /**
     * Creates the needed notification channel for the persistent notification.
     *
     * Repeated calls to this after the channel has been created are a no-op, so it can be safely called on each application start.
     *
     * No api version check necessary because min sdk version is Oreo where notification channels were introduced, yay :-)
     */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(getString(R.string.channel_foreground_service), "Foreground notification", NotificationManager.IMPORTANCE_NONE)
        channel.description = "Foreground notification necessary to keep the service alive due to Androids background service restrictions. You may want to disable this notification."

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}