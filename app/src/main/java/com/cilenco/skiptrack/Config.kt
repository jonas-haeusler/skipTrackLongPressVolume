package com.cilenco.skiptrack

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class Config(
    context: Context
) {

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var serviceEnabled: Boolean
        get() = preferences.getBoolean("service_enabled", false)
        set(value) = preferences.edit().putBoolean("service_enabled", value).apply()

    var activationState: ActivationState
        get() = ActivationState.valueOf(preferences.getString("activation_state", ActivationState.SCREEN_OFF.name)!!)
        set(value) = preferences.edit().putString("activation_state", value.name).apply()

    enum class ActivationState {
        SCREEN_ON, SCREEN_OFF, BOTH
    }
}