package com.cilenco.skiptrack

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import timber.log.Timber

class LongPressSkipTrackService : Service() {

    private lateinit var mediaSessionManager: MediaSessionManagerProxy
    private lateinit var audioManager: AudioManager

    override fun onBind(p0: Intent?): IBinder? {
        Timber.w("onBind() was called but binding is not supported by this service!")
        return null
    }

    override fun onCreate() {
        Timber.i("Initial service start was requested! Performing one-time initialization...")

        mediaSessionManager = MediaSessionManagerProxy(mediaSessionManager = getSystemService()!!)
        audioManager = getSystemService()!!

        startAsForegroundService()
        startListenForLongPress()

        Timber.i("Service successfully initialized!")
    }

    override fun onDestroy() {
        Timber.i("Service is shutting down...")
        stopListenForLongPress()
    }

    private fun startListenForLongPress() {
        val handler = Handler(Looper.getMainLooper()) // TODO: verify handler usage
        val listener = mediaSessionManager.createOnVolumeKeyLongPressListenerProxy(::handleVolumeLongPress)
        mediaSessionManager.setOnVolumeKeyLongPressListener(listener, handler)
    }

    private fun handleVolumeLongPress(keyEvent: KeyEvent) {
        if (keyEvent.action != KeyEvent.ACTION_DOWN
            || keyEvent.flags and KeyEvent.FLAG_LONG_PRESS != KeyEvent.FLAG_LONG_PRESS
            || keyEvent.repeatCount > 1
        ) {
            Timber.d("Discarding key-event due to not fulfilling requirements: $keyEvent")
            // return early if:
            //  * its not a key press (i.e. its a key release)
            //  * its not a long press
            //  * the long press has been registered more than once -> we already handled the long press
            return
        }

        val event = KeyEvent(keyEvent.action, if (keyEvent.keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                KeyEvent.KEYCODE_MEDIA_NEXT
            } else {
                KeyEvent.KEYCODE_MEDIA_PREVIOUS
            }
        )

        Timber.i("Dispatching key-event: '$event' to AudioManager in response to key-event: '$keyEvent'")
        audioManager.dispatchMediaKeyEvent(event)
    }

    private fun stopListenForLongPress() {
        mediaSessionManager.setOnVolumeKeyLongPressListener(null, null)
    }

    private fun startAsForegroundService() {
        Timber.i("Promoting to foreground service with attached notification...")
        startForeground(1, createForegroundNotification())
    }

    private fun createForegroundNotification(): Notification {
        // TODO: content intent into app system settings to hide notification (or just start application?)
        // TODO: Extract string resources
        return NotificationCompat.Builder(this, getString(R.string.channel_foreground_service))
            .setContentTitle("LongPressSkipTrack is up and running")
            .setContentText("Long press this notification to hide it")
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }
}

fun Context.startLongPressSkipTrackService() {
    startForegroundService(Intent(this, LongPressSkipTrackService::class.java))
}

fun Context.stopLongPressSkipTrackService() {
    stopService(Intent(this, LongPressSkipTrackService::class.java))
}