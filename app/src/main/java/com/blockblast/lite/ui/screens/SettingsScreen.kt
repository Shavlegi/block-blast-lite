package com.blockblast.lite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blockblast.lite.AppData
import com.blockblast.lite.LangPrefs
import com.blockblast.lite.R
import com.blockblast.lite.data.SettingsEntity
import com.blockblast.lite.ui.theme.Black
import com.blockblast.lite.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onBack: () -> Unit, onLanguageChanged: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var soundOn by remember { mutableStateOf(true) }
    var vibrateOn by remember { mutableStateOf(true) }
    var language by remember { mutableStateOf(LangPrefs.get(context)) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val settings = AppData.db.settingsDao().get()
        soundOn = settings?.sound ?: true
        vibrateOn = settings?.vibration ?: true
        loaded = true
    }

    fun saveSettings() {
        scope.launch {
            AppData.db.settingsDao().upsert(
                SettingsEntity(sound = soundOn, vibration = vibrateOn)
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Black).padding(16.dp)
    ) {
        ScreenTopBar(title = stringResource(R.string.settings_title), onBack = onBack)
        if (!loaded) return@Column

        SettingRow(
            label = stringResource(R.string.settings_sound),
            checked = soundOn,
            onCheckedChange = { soundOn = it; saveSettings() }
        )
        SettingRow(
            label = stringResource(R.string.settings_vibration),
            checked = vibrateOn,
            onCheckedChange = { vibrateOn = it; saveSettings() }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(R.string.settings_language),
            color = White,
            fontSize = 17.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            LanguageButton("ქართული", language == "ka") {
                language = "ka"
                LangPrefs.set(context, "ka")
                onLanguageChanged()
            }
            LanguageButton("English", language == "en") {
                language = "en"
                LangPrefs.set(context, "en")
                onLanguageChanged()
            }
            LanguageButton("Русский", language == "ru") {
                language = "ru"
                LangPrefs.set(context, "ru")
                onLanguageChanged()
            }
        }
    }
}

@Composable
private fun SettingRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = White, fontSize = 17.sp, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun LanguageButton(text: String, selected: Boolean, onClick: () -> Unit) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = Modifier.padding(end = 8.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = if (selected) {
                com.blockblast.lite.ui.theme.PieceBlue
            } else {
                com.blockblast.lite.ui.theme.DarkBlue
            },
            contentColor = White
        )
    ) {
        Text(text)
    }
}
