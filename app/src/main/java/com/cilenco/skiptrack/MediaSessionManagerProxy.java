package com.cilenco.skiptrack;

import android.annotation.SuppressLint;
import android.media.session.MediaSessionManager;
import android.os.Handler;
import android.view.KeyEvent;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("JavaReflectionMemberAccess")
@SuppressLint("PrivateApi")
public class MediaSessionManagerProxy {

    private final MediaSessionManager mediaSessionManager;

    private final Method setOnVolumeKeyLongPressListener;
    private final Method dispatchVolumeKeyEvent;

    public MediaSessionManagerProxy(MediaSessionManager mediaSessionManager) {
        this.mediaSessionManager = mediaSessionManager;
        try {
            final Class<?> onVolumeKeyLongPressListenerClass = Class.forName("android.media.session.MediaSessionManager$OnVolumeKeyLongPressListener");
            setOnVolumeKeyLongPressListener = MediaSessionManager.class.getMethod("setOnVolumeKeyLongPressListener", onVolumeKeyLongPressListenerClass, Handler.class);

            dispatchVolumeKeyEvent = MediaSessionManager.class.getMethod("dispatchVolumeKeyEvent", KeyEvent.class, Integer.class, Boolean.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public MediaSessionManager get() {
        return mediaSessionManager;
    }

    public void setOnVolumeKeyLongPressListener(Object listener, Handler handler) {
        try {
            setOnVolumeKeyLongPressListener.invoke(mediaSessionManager, listener, handler);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void dispatchVolumeKeyEvent(@NonNull KeyEvent keyEvent, int streamType, boolean musicOnly) {
        try {
            dispatchVolumeKeyEvent.invoke(mediaSessionManager, keyEvent, streamType, musicOnly);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
