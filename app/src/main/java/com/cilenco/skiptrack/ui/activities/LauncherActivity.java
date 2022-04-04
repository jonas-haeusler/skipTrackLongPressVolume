package com.cilenco.skiptrack.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

// Must be splitted from MainActivity because that can be hidden
public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent settingsIntent = new Intent(this, MainActivity.class);
        startActivity(settingsIntent); // Start MainActivity

        finish();
    }

}
