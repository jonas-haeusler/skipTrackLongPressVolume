package com.cilenco.skiptrack

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.session.MediaSessionManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import timber.log.Timber

class LongPressSkipTrackService : Service() {

    private val mediaSessionManager: MediaSessionManager by lazy { getSystemService()!! }
    private val audioManager: AudioManager by lazy { getSystemService()!! }

    private var running: Boolean = false

    private val screenStateReceiver by lazy {
        ScreenStateReceiver(this)
    }

    override fun onBind(p0: Intent?): IBinder? {
        Timber.w("onBind() was called but binding is not supported by this service!")
        return null
    }

    override fun onCreate() {
        Timber.i("Initial service start was requested! Performing one-time initialization...")

        startAsForegroundService()
        startScreenStateReceiver()

        Timber.i("Service successfully initialized!")
    }

    override fun onDestroy() {
        Timber.i("Service is shutting down, cleaning up...")
        stopListenForLongPress()
        stopScreenStateReceiver()
    }

    private fun startScreenStateReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)

        Timber.i("Registering screen state change receiver")
        registerReceiver(screenStateReceiver, intentFilter)
    }

    private fun stopScreenStateReceiver() {
        Timber.i("Stopping screen state change receiver")
        unregisterReceiver(screenStateReceiver)
    }

    fun startListenForLongPress() {
        if (running) {
            Timber.i("Start listen was requested but service is already listening. This is a no-op.")
            return
        }
        running = true

        val handler = Handler(Looper.getMainLooper()) // TODO: verify handler usage
        val listener = OnVolumeKeyLongPressListener(::handleVolumeLongPress)

        Timber.d("Starting to listen for volume key long-presses...")
        mediaSessionManager.setOnVolumeKeyLongPressListener(listener, handler)
    }

    fun stopListenForLongPress() {
        Timber.d("Stopping to listen for volume key long-presses...")
        mediaSessionManager.setOnVolumeKeyLongPressListener(null, null)
        running = false
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