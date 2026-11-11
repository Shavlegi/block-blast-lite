package com.blockblast.lite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blockblast.lite.AppData
import com.blockblast.lite.R
import com.blockblast.lite.data.ScoreEntity
import com.blockblast.lite.ui.theme.Black
import com.blockblast.lite.ui.theme.GrayText
import com.blockblast.lite.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecordsScreen(onBack: () -> Unit) {
    var bestEndless by remember { mutableStateOf<Int?>(null) }
    var bestDaily by remember { mutableStateOf<Int?>(null) }
    var levelsDone by remember { mutableStateOf(0) }
    var topScores by remember { mutableStateOf<List<ScoreEntity>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        bestEndless = AppData.db.scoreDao().bestScore("ENDLESS")
        bestDaily = AppData.db.scoreDao().bestScore("DAILY")
        levelsDone = AppData.db.levelDao().getAll().count { it.completed }
        topScores = AppData.db.scoreDao().topScores()
        loaded = true
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Black).padding(16.dp)
    ) {
        ScreenTopBar(title = stringResource(R.string.records_title), onBack = onBack)
        if (!loaded) {
            Text("…", color = GrayText)
            return@Column
        }
        if (topScores.isEmpty()) {
            Text(stringResource(R.string.records_empty), color = GrayText, fontSize = 16.sp)
            return@Column
        }
        RecordRow(stringResource(R.string.records_best_endless), bestEndless ?: 0)
        RecordRow(stringResource(R.string.records_best_daily), bestDaily ?: 0)
        RecordRow(stringResource(R.string.records_levels_done), levelsDone, suffix = "/100")
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(R.string.records_title),
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        LazyColumn {
            items(topScores) { score ->
                val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    .format(Date(score.date))
                Text(
                    text = "${score.score} — ${score.mode} — $date",
                    color = GrayText,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun RecordRow(label: String, value: Int, suffix: String = "") {
    Text(
        text = "$label: $value$suffix",
        color = White,
        fontSize = 17.sp,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
