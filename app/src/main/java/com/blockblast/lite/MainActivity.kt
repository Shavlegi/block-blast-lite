package com.blockblast.lite

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.blockblast.lite.data.AppDatabase
import com.blockblast.lite.ui.screens.GameScreen
import com.blockblast.lite.ui.screens.LevelsScreen
import com.blockblast.lite.ui.screens.MenuScreen
import com.blockblast.lite.ui.screens.RecordsScreen
import com.blockblast.lite.ui.screens.SettingsScreen
import com.blockblast.lite.ui.theme.BlockBlastLiteTheme
import java.util.Locale

enum class GameMode { ENDLESS, LEVELS, DAILY, ALPHABET }

enum class Screen { MENU, GAME, LEVELS, RECORDS, SETTINGS }

object AppData {
    lateinit var db: AppDatabase
}

/**
 * Language is kept in SharedPreferences (not Room) on purpose:
 * attachBaseContext runs before the database exists, so the language
 * must be readable synchronously at startup.
 */
object LangPrefs {
    private const val NAME = "block-blast-lite-prefs"
    private const val KEY = "language"

    fun get(context: Context): String =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .getString(KEY, "ka") ?: "ka"

    fun set(context: Context, lang: String) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY, lang).apply()
    }
}

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = LangPrefs.get(newBase)
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppData.db = AppDatabase.get(this)
        setContent {
            BlockBlastLiteTheme {
                var screen by remember { mutableStateOf(Screen.MENU) }
                var gameMode by remember { mutableStateOf(GameMode.ENDLESS) }
                var levelNumber by remember { mutableStateOf(1) }
                when (screen) {
                    Screen.MENU -> MenuScreen(
                        onPlay = { mode, level ->
                            gameMode = mode
                            levelNumber = level
                            screen = Screen.GAME
                        },
                        onLevels = { screen = Screen.LEVELS },
                        onRecords = { screen = Screen.RECORDS },
                        onSettings = { screen = Screen.SETTINGS }
                    )
                    Screen.GAME -> GameScreen(
                        mode = gameMode,
                        levelNumber = levelNumber,
                        onExit = { screen = Screen.MENU }
                    )
                    Screen.LEVELS -> LevelsScreen(
                        onPlayLevel = { level ->
                            gameMode = GameMode.LEVELS
                            levelNumber = level
                            screen = Screen.GAME
                        },
                        onBack = { screen = Screen.MENU }
                    )
                    Screen.RECORDS -> RecordsScreen(onBack = { screen = Screen.MENU })
                    Screen.SETTINGS -> SettingsScreen(
                        onBack = { screen = Screen.MENU },
                        onLanguageChanged = { recreate() }
                    )
                }
            }
        }
    }
}
