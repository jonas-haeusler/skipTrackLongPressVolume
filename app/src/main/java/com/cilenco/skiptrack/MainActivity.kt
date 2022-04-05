package com.cilenco.skiptrack

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val LONG_PRESS_VOLUME_PERMISSION = "android.permission.SET_VOLUME_KEY_LONG_PRESS_LISTENER"

class MainActivity : AppCompatActivity() {

    private lateinit var config: Config

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        config = Config(this)

        setContent {
            SettingsScreen()
        }
    }

    private fun longPressVolumePermissionGranted(): Boolean {
        return checkSelfPermission(LONG_PRESS_VOLUME_PERMISSION) == PackageManager.PERMISSION_GRANTED
    }

    private fun isServiceEnabled(): Boolean {
        return config.serviceEnabled
    }

    private fun setServiceEnabled(newState: Boolean) {
        config.serviceEnabled = newState

        if (newState) {
            startLongPressSkipTrackService()
        } else {
            stopLongPressSkipTrackService()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun SettingsScreen() {
        MaterialTheme {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Long Press Skip Track",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(
                        top = 64.dp,
                        bottom = 32.dp,
                        start = 8.dp
                    )
                )
                PermissionInfo(longPressVolumePermissionGranted())
                Spacer(modifier = Modifier.height(16.dp))
                ServiceToggle(
                    serviceState= isServiceEnabled(),
                    onChange = { state -> setServiceEnabled(state)}
                )
            }
        }
    }

    @Composable
    @Preview
    private fun SettingsScreenPreview() {
        SettingsScreen()
    }

    @ExperimentalMaterial3Api
    @Composable
    private fun PermissionInfo(granted: Boolean) {
        if (granted) return

        Card(
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {}
        ) {
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = "Hint",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text("Set up permission", style = MaterialTheme.typography.headlineSmall)
                    Text("Click to find out how to set up required permissions for Long Press Skip Track", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    @ExperimentalMaterial3Api
    @Composable
    private fun ServiceToggle(serviceState: Boolean, onChange: (Boolean) -> Unit) {
        val state = remember { mutableStateOf(serviceState) }

        Card(
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                state.value = state.value.not()
                onChange(state.value)
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 16.dp,
                    start = 24.dp,
                    end = 24.dp
                )
            ) {
                Text(
                    text = "Activate Long Press Skip Track",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = state.value,
                    onCheckedChange = null,
                    modifier = Modifier.padding(
                        end = 4.dp
                    )
                )
            }
        }
    }
}