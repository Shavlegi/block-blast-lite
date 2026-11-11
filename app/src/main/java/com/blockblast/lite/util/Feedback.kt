package com.blockblast.lite.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Tiny built-in feedback: simple tones (no audio files) and vibration.
 * Everything is wrapped in try/catch so a device without a tone generator
 * or vibrator can never crash the game.
 */
object Feedback {
    private var toneGen: ToneGenerator? = null

    /** kind: 0 = place, 1 = line clear, 2 = combo, 3 = game over. */
    fun tone(kind: Int) {
        try {
            if (toneGen == null) {
                toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 60)
            }
            val (tone, duration) = when (kind) {
                0 -> ToneGenerator.TONE_PROP_BEEP to 80
                1 -> ToneGenerator.TONE_PROP_BEEP2 to 120
                2 -> ToneGenerator.TONE_CDMA_ANSWER to 150
                else -> ToneGenerator.TONE_CDMA_ABBR_ALERT to 300
            }
            toneGen?.startTone(tone, duration)
        } catch (_: Exception) {
            // No tone generator on this device — ignore.
        }
    }

    fun vibrate(context: Context, ms: Long) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
                    .defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(ms)
            }
        } catch (_: Exception) {
            // No vibrator on this device — ignore.
        }
    }
}
