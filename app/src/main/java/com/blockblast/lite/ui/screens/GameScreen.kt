package com.blockblast.lite.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blockblast.lite.AppData
import com.blockblast.lite.GameMode
import com.blockblast.lite.R
import com.blockblast.lite.data.DailyEntity
import com.blockblast.lite.data.LevelProgressEntity
import com.blockblast.lite.data.LevelsRepository
import com.blockblast.lite.data.ScoreEntity
import com.blockblast.lite.game.GEORGIAN_LETTERS
import com.blockblast.lite.game.GameEngine
import com.blockblast.lite.game.Shape
import com.blockblast.lite.game.newTray
import com.blockblast.lite.ui.theme.Black
import com.blockblast.lite.ui.theme.DarkBlue
import com.blockblast.lite.ui.theme.GrayText
import com.blockblast.lite.ui.theme.PieceRed
import com.blockblast.lite.ui.theme.White
import com.blockblast.lite.ui.theme.pieceColor
import com.blockblast.lite.util.Feedback
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

private fun todayString(): String =
    java.time.LocalDate.now()
        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))

private fun todaySeed(): Long =
    java.time.LocalDate.now()
        .format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE).toLong()

@Composable
fun GameScreen(mode: GameMode, levelNumber: Int, onExit: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val levels = remember { LevelsRepository.load(context) }

    var currentLevel by remember { mutableStateOf(levelNumber) }
    val level = if (mode == GameMode.LEVELS) {
        levels.firstOrNull { it.number == currentLevel }
    } else {
        null
    }
    val alphabetMode = mode == GameMode.ALPHABET

    val random = remember(mode, currentLevel) {
        if (mode == GameMode.DAILY) Random(todaySeed()) else Random(System.currentTimeMillis())
    }
    val engine = remember(mode, currentLevel) { GameEngine() }

    var tray by remember(mode, currentLevel) {
        mutableStateOf<List<Shape?>>(newTray(random, alphabetMode))
    }
    var selected by remember { mutableStateOf<Int?>(null) }
    var boardVersion by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var best by remember { mutableStateOf(0) }
    var movesLeft by remember { mutableStateOf(level?.moves ?: -1) }
    var gameOver by remember { mutableStateOf(false) }
    var levelWon by remember { mutableStateOf<Boolean?>(null) }
    var showPause by remember { mutableStateOf(false) }
    var letterBanner by remember { mutableStateOf("") }
    var soundOn by remember { mutableStateOf(true) }
    var vibrateOn by remember { mutableStateOf(true) }
    var dragIndex by remember { mutableStateOf<Int?>(null) }
    var dragPos by remember { mutableStateOf(Offset.Zero) }
    var dragHover by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var boardOrigin by remember { mutableStateOf(Offset.Zero) }
    var cellPx by remember { mutableStateOf(0f) }
    val trayOrigins = remember { mutableMapOf<Int, Offset>() }

    LaunchedEffect(mode) {
        best = AppData.db.scoreDao().bestScore(mode.name) ?: 0
        val settings = AppData.db.settingsDao().get()
        soundOn = settings?.sound ?: true
        vibrateOn = settings?.vibration ?: true
    }

    fun cellFromGlobal(global: Offset): Pair<Int, Int>? {
        if (cellPx <= 0f) return null
        val col = ((global.x - boardOrigin.x) / cellPx).roundToInt()
        val row = ((global.y - boardOrigin.y) / cellPx).roundToInt()
        if (row !in 0 until 8 || col !in 0 until 8) return null
        return row to col
    }

    fun saveResult() {
        scope.launch {
            AppData.db.scoreDao().insert(
                ScoreEntity(score = score, mode = mode.name, date = System.currentTimeMillis())
            )
            if (mode == GameMode.DAILY) {
                AppData.db.dailyDao().upsert(DailyEntity(date = todayString(), score = score))
            }
            if (mode == GameMode.LEVELS && level != null) {
                val prev = AppData.db.levelDao().get(level.number)
                val won = score >= level.target
                AppData.db.levelDao().upsert(
                    LevelProgressEntity(
                        number = level.number,
                        completed = (prev?.completed == true) || won,
                        bestScore = maxOf(prev?.bestScore ?: 0, score)
                    )
                )
            }
        }
    }

    fun tryPlace(index: Int, row: Int, col: Int) {
        if (gameOver) return
        val shape = tray.getOrNull(index) ?: return
        val result = engine.place(shape, row, col) ?: return
        score = engine.score
        if (soundOn) {
            Feedback.tone(
                when {
                    result.linesCleared > 1 -> 2
                    result.linesCleared == 1 -> 1
                    else -> 0
                }
            )
        }
        if (vibrateOn && result.linesCleared > 0) Feedback.vibrate(context, 60)
        if (alphabetMode) {
            val letter = shape.letter
            if (letter != null) {
                val name = GEORGIAN_LETTERS.firstOrNull { it.first == letter }?.second ?: ""
                letterBanner = "$letter — $name"
            }
        }
        selected = null
        val updated = tray.toMutableList()
        updated[index] = null
        val nextTray: List<Shape?> = if (updated.all { it == null }) {
            newTray(random, alphabetMode)
        } else {
            updated
        }
        tray = nextTray
        boardVersion++
        if (mode == GameMode.LEVELS && level != null) {
            movesLeft--
            if (score >= level.target) {
                levelWon = true
                gameOver = true
                saveResult()
                return
            }
        }
        val remaining = nextTray.filterNotNull()
        if (engine.isGameOver(remaining)) {
            if (soundOn) Feedback.tone(3)
            gameOver = true
            if (mode == GameMode.LEVELS) levelWon = false
            saveResult()
            return
        }
        if (mode == GameMode.LEVELS && movesLeft <= 0) {
            levelWon = false
            gameOver = true
            saveResult()
        }
    }

    fun resetGame() {
        engine.reset()
        tray = newTray(random, alphabetMode)
        selected = null
        score = 0
        movesLeft = level?.moves ?: -1
        gameOver = false
        levelWon = null
        letterBanner = ""
        boardVersion++
    }

    fun nextLevel() {
        currentLevel++
        score = 0
        gameOver = false
        levelWon = null
        letterBanner = ""
        selected = null
        movesLeft = levels.firstOrNull { it.number == currentLevel }?.moves ?: -1
        boardVersion++
    }

    fun shareDaily() {
        val text = buildString {
            append("Block Blast Lite — ")
            append(context.getString(R.string.menu_daily))
            append("\n")
            append(context.getString(R.string.game_score))
            append(": $score\n")
            for (r in 0 until 8) {
                for (c in 0 until 8) {
                    append(if (engine.board[r][c] != 0) "🟦" else "⬛")
                }
                append("\n")
            }
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(
            Intent.createChooser(intent, context.getString(R.string.game_share))
        )
    }

    val dragShape: Shape? = dragIndex?.let { tray.getOrNull(it) }
    val previewCells: Set<Pair<Int, Int>> = remember(dragHover, dragIndex, tray, boardVersion) {
        val shape = dragShape ?: return@remember emptySet()
        val hover = dragHover ?: return@remember emptySet()
        val r0 = hover.first.coerceIn(0, 8 - shape.height)
        val c0 = hover.second.coerceIn(0, 8 - shape.width)
        shape.cells.map { (dr, dc) -> (r0 + dr) to (c0 + dc) }.toSet()
    }
    val previewValid: Boolean = remember(dragHover, dragIndex, tray, boardVersion) {
        val shape = dragShape ?: return@remember false
        val hover = dragHover ?: return@remember false
        engine.canPlace(
            shape,
            hover.first.coerceIn(0, 8 - shape.height),
            hover.second.coerceIn(0, 8 - shape.width)
        )
    }

    val modeTitle = when (mode) {
        GameMode.ENDLESS -> stringResource(R.string.menu_play_endless)
        GameMode.LEVELS -> "${stringResource(R.string.menu_levels)} $currentLevel"
        GameMode.DAILY -> stringResource(R.string.menu_daily)
        GameMode.ALPHABET -> stringResource(R.string.menu_alphabet)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Black).padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onExit) {
                Text("‹ ${stringResource(R.string.back)}", color = White, fontSize = 16.sp)
            }
            Text(
                modeTitle,
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            TextButton(onClick = { showPause = true }) {
                Text("II", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                "${stringResource(R.string.game_score)}: $score",
                color = White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${stringResource(R.string.game_best)}: ${maxOf(score, best)}",
                color = GrayText,
                fontSize = 18.sp
            )
        }
        if (mode == GameMode.LEVELS && level != null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    "${stringResource(R.string.game_target)}: ${level.target}",
                    color = White,
                    fontSize = 16.sp
                )
                Text(
                    "${stringResource(R.string.game_moves)}: $movesLeft",
                    color = GrayText,
                    fontSize = 16.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .onGloballyPositioned { coords ->
                    boardOrigin = coords.positionInRoot()
                    cellPx = coords.size.width / 8f
                }
        ) {
            key(boardVersion) {
                Column(modifier = Modifier.fillMaxSize()) {
                    for (r in 0 until 8) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (c in 0 until 8) {
                                val filled = engine.board[r][c]
                                val inPreview = (r to c) in previewCells
                                val bg = when {
                                    filled != 0 -> pieceColor(filled)
                                    inPreview && previewValid ->
                                        pieceColor(dragShape!!.colorId).copy(alpha = 0.45f)
                                    inPreview -> PieceRed.copy(alpha = 0.45f)
                                    else -> DarkBlue
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(2.dp)
                                        .background(bg, RoundedCornerShape(4.dp))
                                        .clickable {
                                            selected?.let { tryPlace(it, r, c) }
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        if (letterBanner.isNotEmpty()) {
            Text(
                letterBanner,
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else if (!gameOver) {
            Text(
                stringResource(R.string.game_tap_shape),
                color = GrayText,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tray.forEachIndexed { index, shape ->
                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (shape != null) {
                        val isSelected = selected == index
                        val isDragging = dragIndex == index
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .background(
                                    if (isSelected) DarkBlue else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .border(
                                    if (isSelected) 2.dp else 0.dp,
                                    White,
                                    RoundedCornerShape(10.dp)
                                )
                                .alpha(if (isDragging) 0.4f else 1f)
                                .onGloballyPositioned { coords ->
                                    trayOrigins[index] = coords.positionInRoot()
                                }
                                .clickable {
                                    selected = if (selected == index) null else index
                                }
                                .pointerInput(shape, index) {
                                    detectDragGestures(
                                        onDragStart = { start ->
                                            dragIndex = index
                                            dragPos =
                                                (trayOrigins[index] ?: Offset.Zero) + start
                                            dragHover = cellFromGlobal(dragPos)
                                        },
                                        onDrag = { _, amount ->
                                            dragPos += amount
                                            dragHover = cellFromGlobal(dragPos)
                                        },
                                        onDragEnd = {
                                            val cell = cellFromGlobal(dragPos)
                                            dragIndex = null
                                            dragHover = null
                                            if (cell != null) {
                                                val r = cell.first
                                                    .coerceIn(0, 8 - shape.height)
                                                val c = cell.second
                                                    .coerceIn(0, 8 - shape.width)
                                                tryPlace(index, r, c)
                                            }
                                        },
                                        onDragCancel = {
                                            dragIndex = null
                                            dragHover = null
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            ShapePreview(shape)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        if (gameOver && mode == GameMode.DAILY) {
            Button(
                onClick = { shareDaily() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkBlue,
                    contentColor = White
                )
            ) {
                Text(stringResource(R.string.game_share), fontSize = 16.sp)
            }
        }
    }

    if (showPause) {
        AlertDialog(
            onDismissRequest = { showPause = false },
            title = { Text(stringResource(R.string.game_pause)) },
            text = { Text("${stringResource(R.string.game_score)}: $score") },
            confirmButton = {
                TextButton(onClick = { showPause = false }) {
                    Text(stringResource(R.string.game_resume))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPause = false; onExit() }) {
                    Text(stringResource(R.string.game_menu))
                }
            }
        )
    }

    if (gameOver) {
        val title = when (levelWon) {
            true -> stringResource(R.string.game_level_complete)
            false -> stringResource(R.string.game_level_failed)
            null -> stringResource(R.string.game_over)
        }
        AlertDialog(
            onDismissRequest = {},
            title = { Text(title) },
            text = {
                Text(
                    "${stringResource(R.string.game_score)}: $score" +
                        if (score > best && best > 0 || score > 0 && best == 0) {
                            "\n${stringResource(R.string.game_new_best)}"
                        } else {
                            ""
                        }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (levelWon == true) nextLevel() else resetGame()
                    }
                ) {
                    Text(
                        if (levelWon == true) {
                            stringResource(R.string.game_next_level)
                        } else {
                            stringResource(R.string.game_retry)
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onExit) {
                    Text(stringResource(R.string.game_menu))
                }
            }
        )
    }
}

@Composable
private fun ShapePreview(shape: Shape) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        for (r in 0 until shape.height) {
            Row {
                for (c in 0 until shape.width) {
                    val filled = shape.cells.contains(r to c)
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .padding(1.dp)
                            .background(
                                if (filled) pieceColor(shape.colorId) else Color.Transparent,
                                RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}
