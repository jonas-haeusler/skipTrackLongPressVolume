package com.cilenco.skiptrack

import android.annotation.SuppressLint
import android.media.session.MediaSessionManager
import android.os.Handler
import android.view.KeyEvent
import androidx.annotation.NonNull
import java.lang.reflect.Method
import java.lang.reflect.Proxy

@SuppressLint("PrivateApi")
fun OnVolumeKeyLongPressListener(onVolumeKeyLongPress: (KeyEvent) -> (Unit)): Any {
    val clazz = Class.forName(
        "android.media.session.MediaSessionManager\$OnVolumeKeyLongPressListener"
    )

    return Proxy.newProxyInstance(
        clazz.classLoader,
        arrayOf(clazz)
    ) { _: Any, _: Method, args: Array<Any> ->
        onVolumeKeyLongPress(args[0] as KeyEvent)
    }
}

@SuppressLint("PrivateApi")
fun MediaSessionManager.setOnVolumeKeyLongPressListener(listener: Any?, handler: Handler?) {
    val onVolumeKeyLongPressListenerClass = Class.forName(
        "android.media.session.MediaSessionManager\$OnVolumeKeyLongPressListener"
    )

    val setOnVolumeKeyLongPressListener = MediaSessionManager::class.java.getMethod(
        "setOnVolumeKeyLongPressListener", onVolumeKeyLongPressListenerClass, Handler::class.java
    )
    setOnVolumeKeyLongPressListener.invoke(this, listener, handler)
}

fun MediaSessionManager.dispatchVolumeKeyEvent(
    @NonNull keyEvent: KeyEvent, streamType: Int, musicOnly: Boolean
) {
    MediaSessionManager::class.java.methods.forEach { println(it) }

    MediaSessionManager::class.java.getMethod(
        "dispatchVolumeKeyEvent", KeyEvent::class.java, Integer.TYPE, Boolean::class.java
    ).invoke(this, keyEvent, streamType, musicOnly)
}