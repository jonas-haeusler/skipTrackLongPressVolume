package com.cilenco.skiptrack

import android.annotation.SuppressLint
import android.media.session.MediaSessionManager
import android.os.Handler
import android.view.KeyEvent
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * [MediaSessionManager] proxy that exposes hidden methods using reflection, namely [setOnVolumeKeyLongPressListener].
 *
 * Use [MediaSessionManagerProxy.get] to retrieve the actual [MediaSessionManager] instance.
 */
@SuppressLint("PrivateApi")
class MediaSessionManagerProxy(
    private val mediaSessionManager: MediaSessionManager
) {

    private var setOnVolumeKeyLongPressListener: Method

    init {
        val onVolumeKeyLongPressListenerClass = Class.forName(
            "android.media.session.MediaSessionManager\$OnVolumeKeyLongPressListener"
        )

        setOnVolumeKeyLongPressListener = MediaSessionManager::class.java.getMethod(
            "setOnVolumeKeyLongPressListener", onVolumeKeyLongPressListenerClass, Handler::class.java
        )
    }

    fun createOnVolumeKeyLongPressListenerProxy(onVolumeKeyLongPress: (KeyEvent) -> (Unit)): Any {
        val clazz = Class.forName(
            "android.media.session.MediaSessionManager\$OnVolumeKeyLongPressListener"
        )

        return Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz)) { _: Any, _: Method, args: Array<Any> ->
            onVolumeKeyLongPress(args[0] as KeyEvent)
        }
    }

    fun get(): MediaSessionManager {
        return mediaSessionManager
    }

    /**
     * Set the volume key long-press listener. While the listener is set, the listener
     * gets the volume key long-presses instead of changing volume.
     *
     * <p>System can only have a single volume key long-press listener.
     *
     * @param listener The volume key long-press listener. {@code null} to reset.
     * @param handler The handler on which the listener should be invoked, or {@code null}
     *            if the listener should be invoked on the calling thread's looper.
     */
    fun setOnVolumeKeyLongPressListener(listener: Any?, handler: Handler?) {
        setOnVolumeKeyLongPressListener.invoke(mediaSessionManager, listener, handler)
    }
}