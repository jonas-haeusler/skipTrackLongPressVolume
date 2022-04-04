package com.cilenco.skiptrack.ui.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.cilenco.skiptrack.R;

import net.grandcentrix.tray.AppPreferences;

import static com.cilenco.skiptrack.utils.Constants.PREF_PERMISSION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class MainActivity extends Activity implements DialogInterface.OnClickListener {
    private static final String PERMISSION = "android.permission.SET_VOLUME_KEY_LONG_PRESS_LISTENER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        int permission = checkSelfPermission(PERMISSION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{PERMISSION}, 1);
        } else {
            AppPreferences prefs = new AppPreferences(this);
            prefs.put(PREF_PERMISSION, true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            AppPreferences prefs = new AppPreferences(this);
            prefs.put(PREF_PERMISSION, true);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setPositiveButton(android.R.string.ok, this);

            builder.setView(R.layout.dialog_permission);
            builder.setMessage(R.string.permission_description);
            builder.setTitle(R.string.permission_title);
            builder.setCancelable(false);

            builder.show();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        finishAndRemoveTask();
    }
}
