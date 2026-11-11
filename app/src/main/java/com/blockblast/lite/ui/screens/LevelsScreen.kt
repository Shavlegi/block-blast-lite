package com.blockblast.lite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blockblast.lite.AppData
import com.blockblast.lite.R
import com.blockblast.lite.data.LevelProgressEntity
import com.blockblast.lite.data.LevelsRepository
import com.blockblast.lite.ui.theme.Black
import com.blockblast.lite.ui.theme.DarkBlue
import com.blockblast.lite.ui.theme.GrayText
import com.blockblast.lite.ui.theme.White

@Composable
fun LevelsScreen(onPlayLevel: (Int) -> Unit, onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val levels = remember { LevelsRepository.load(context) }
    var progress by remember { mutableStateOf<Map<Int, LevelProgressEntity>>(emptyMap()) }

    LaunchedEffect(Unit) {
        progress = AppData.db.levelDao().getAll().associateBy { it.number }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Black).padding(16.dp)
    ) {
        ScreenTopBar(title = stringResource(R.string.levels_title), onBack = onBack)
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(levels) { level ->
                val done = progress[level.number]?.completed == true
                val unlocked = level.number == 1 ||
                    progress[level.number - 1]?.completed == true
                Button(
                    onClick = { if (unlocked) onPlayLevel(level.number) },
                    enabled = unlocked,
                    modifier = Modifier.padding(4.dp).aspectRatio(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (done) com.blockblast.lite.ui.theme.PieceGreen else DarkBlue,
                        contentColor = White,
                        disabledContainerColor = DarkBlue,
                        disabledContentColor = GrayText
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (done) "✓${level.number}" else level.number.toString(),
                        fontSize = 15.sp
                    )
                }
            }
        }
        if (levels.isNotEmpty()) {
            Text(
                text = "${stringResource(R.string.records_levels_done)}: " +
                    "${progress.values.count { it.completed }}/100",
                color = GrayText,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
