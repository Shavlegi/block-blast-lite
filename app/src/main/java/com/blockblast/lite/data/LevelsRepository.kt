package com.blockblast.lite.data

import android.content.Context
import org.json.JSONArray

data class Level(
    val number: Int,
    val target: Int,
    val moves: Int
)

/** Loads the 100 predefined levels from assets/levels.json (uses org.json, no extra dependency). */
object LevelsRepository {
    fun load(context: Context): List<Level> {
        val text = context.assets.open("levels.json").bufferedReader().use { it.readText() }
        val arr = JSONArray(text)
        val out = ArrayList<Level>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            out.add(Level(o.getInt("number"), o.getInt("target"), o.getInt("moves")))
        }
        return out.sortedBy { it.number }
    }
}
