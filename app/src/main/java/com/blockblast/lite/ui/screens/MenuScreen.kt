package com.blockblast.lite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blockblast.lite.GameMode
import com.blockblast.lite.R
import com.blockblast.lite.ui.theme.Black
import com.blockblast.lite.ui.theme.DarkBlue
import com.blockblast.lite.ui.theme.GrayText
import com.blockblast.lite.ui.theme.White

@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkBlue,
            contentColor = White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, fontSize = 18.sp)
    }
}

@Composable
fun ScreenTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onBack) {
            Text("‹ ${stringResource(R.string.back)}", color = White, fontSize = 16.sp)
        }
        Text(
            title,
            color = White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun MenuScreen(
    onPlay: (GameMode, Int) -> Unit,
    onLevels: () -> Unit,
    onRecords: () -> Unit,
    onSettings: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Black).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(R.string.app_name),
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = White
        )
        Text(
            stringResource(R.string.menu_subtitle),
            fontSize = 16.sp,
            color = GrayText
        )
        Spacer(modifier = Modifier.height(32.dp))
        MenuButton(stringResource(R.string.menu_play_endless)) { onPlay(GameMode.ENDLESS, 1) }
        MenuButton(stringResource(R.string.menu_levels)) { onLevels() }
        MenuButton(stringResource(R.string.menu_daily)) { onPlay(GameMode.DAILY, 1) }
        MenuButton(stringResource(R.string.menu_alphabet)) { onPlay(GameMode.ALPHABET, 1) }
        MenuButton(stringResource(R.string.menu_records)) { onRecords() }
        MenuButton(stringResource(R.string.menu_settings)) { onSettings() }
    }
}
