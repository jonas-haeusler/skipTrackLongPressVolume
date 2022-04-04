package com.cilenco.skiptrack.services;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.session.MediaSessionManager;
import android.os.Handler;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.cilenco.skiptrack.AudioManagerProxy;
import com.cilenco.skiptrack.MediaSessionManagerProxy;
import com.cilenco.skiptrack.R;

import java.lang.reflect.Proxy;
import java.util.Arrays;

import static android.view.KeyEvent.FLAG_FROM_SYSTEM;
import static android.view.KeyEvent.FLAG_LONG_PRESS;
import static android.view.KeyEvent.KEYCODE_MEDIA_NEXT;
import static android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;
import static com.cilenco.skiptrack.utils.Constants.PREF_DEBUG;
import static com.cilenco.skiptrack.utils.Constants.PREF_ENABLED;
import static com.cilenco.skiptrack.utils.Constants.PREF_NO_MEDIA;
import static com.cilenco.skiptrack.utils.Constants.PREF_PERMISSION;
import static com.cilenco.skiptrack.utils.Constants.PREF_SCREEN_ON;

import androidx.preference.PreferenceManager;

public class VolumeKeyService extends NotificationListenerService implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences preferences;

    private MediaSessionManagerProxy mediaSessionManager;
    private PowerManager powerManager;
    private AudioManagerProxy audioManager;

    private Handler mHandler;

    private boolean debugEnabled;
    private boolean prefScreenOn;
    private boolean prefNoMedia;

    private Object volumeKeyLongPressListener;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("cilenco", "VolumeKeyService started");
        volumeKeyLongPressListener = createVolumeKeyLongPressListener();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        audioManager = new AudioManagerProxy(getSystemService(AudioManager.class));
        powerManager = getSystemService(PowerManager.class);

        mediaSessionManager = new MediaSessionManagerProxy(getSystemService(MediaSessionManager.class));
        mHandler = new Handler();

        mediaSessionManager.setOnVolumeKeyLongPressListener(volumeKeyLongPressListener, mHandler);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d("test", "onSharedPreferenceChanged");

        boolean permission = preferences.getBoolean(PREF_PERMISSION, false);
        boolean serviceEnabled = preferences.getBoolean(PREF_ENABLED, false);

        debugEnabled = preferences.getBoolean(PREF_DEBUG, false);
        prefScreenOn = preferences.getBoolean(PREF_SCREEN_ON, false);
        prefNoMedia = preferences.getBoolean(PREF_NO_MEDIA, false);

        if (serviceEnabled && permission) {
            mediaSessionManager.setOnVolumeKeyLongPressListener(volumeKeyLongPressListener, mHandler);
            Log.d("cilenco", "Registered VolumeKeyListener");
        } else {
            mediaSessionManager.setOnVolumeKeyLongPressListener(null, null);
            Log.d("cilenco", "Unregistered VolumeKeyListener");
        }
    }

    @SuppressLint("PrivateApi")
    private Object createVolumeKeyLongPressListener() {
        try {
            final Class<?> clazz = Class.forName("android.media.session.MediaSessionManager$OnVolumeKeyLongPressListener");
            return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (proxy, method, args) -> {
                final KeyEvent keyEvent = (KeyEvent) args[0];
                onVolumeKeyLongPress(keyEvent);
                return null;
            });
        } catch (NullPointerException | ClassNotFoundException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        preferences.unregisterOnSharedPreferenceChangeListener(this);
        mediaSessionManager.setOnVolumeKeyLongPressListener(null, null);
    }

    private void onVolumeKeyLongPress(KeyEvent keyEvent) {
        System.out.println(keyEvent);
        Log.d("TAG", "onVolumeKeyLongPress");
        boolean screenOn = powerManager.isInteractive();
        boolean musicPlaying = audioManager.get().isMusicActive();

        int flags = keyEvent.getFlags();

        //if(keyEvent.getFlags() != FLAG_FROM_SYSTEM) return;
        if (!(flags == FLAG_FROM_SYSTEM || flags == FLAG_LONG_PRESS)) return;

        if ((musicPlaying || prefNoMedia) && (!screenOn || prefScreenOn)) {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getRepeatCount() <= 1) {
                int keyCode = keyEvent.getKeyCode();

                int event = (keyCode == KEYCODE_VOLUME_UP) ? KEYCODE_MEDIA_NEXT : KEYCODE_MEDIA_PREVIOUS;
                int msgRes = (keyCode == KEYCODE_VOLUME_UP) ? R.string.msg_media_next : R.string.msg_media_pre;

                KeyEvent skipEvent = new KeyEvent(keyEvent.getAction(), event);
                audioManager.get().dispatchMediaKeyEvent(skipEvent);

                if (debugEnabled)
                    Toast.makeText(this, getString(msgRes), Toast.LENGTH_SHORT).show();
            }

            return;
        }

        // Let the MediaSessionManager deal with the event

        mediaSessionManager.setOnVolumeKeyLongPressListener(null, null);
        mediaSessionManager.dispatchVolumeKeyEvent(keyEvent, audioManager.getUiSoundsStreamType(), false);
        mediaSessionManager.setOnVolumeKeyLongPressListener(volumeKeyLongPressListener, mHandler);
    }
}