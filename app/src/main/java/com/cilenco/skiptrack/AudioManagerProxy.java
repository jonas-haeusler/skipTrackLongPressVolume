package com.cilenco.skiptrack;

import android.annotation.SuppressLint;
import android.media.AudioManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("JavaReflectionMemberAccess")
@SuppressLint("PrivateApi")
public class AudioManagerProxy {

    private final AudioManager audioManager;

    private final Method getUiSoundsStreamType;

    public AudioManagerProxy(AudioManager audioManager) {
        this.audioManager = audioManager;

        try {
            getUiSoundsStreamType = AudioManager.class.getMethod("getUiSoundsStreamType");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public AudioManager get() {
        return audioManager;
    }

    @SuppressWarnings("ConstantConditions")
    public int getUiSoundsStreamType() {
        try {
            return (int) getUiSoundsStreamType.invoke(audioManager);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
