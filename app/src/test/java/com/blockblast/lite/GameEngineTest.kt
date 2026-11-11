package com.blockblast.lite

import com.blockblast.lite.game.GameEngine
import com.blockblast.lite.game.Shape
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {

    private fun singleCell(colorId: Int = 1) = Shape(listOf(0 to 0), colorId)

    @Test
    fun placementRejectsOverlap() {
        val engine = GameEngine()
        assertTrue(engine.place(singleCell(), 0, 0) != null)
        assertNull(engine.place(singleCell(), 0, 0))
    }

    @Test
    fun placementRejectsOutOfBounds() {
        val engine = GameEngine()
        val wide = Shape(listOf(0 to 0, 0 to 1, 0 to 2), 1)
        assertFalse(engine.canPlace(wide, 0, 6))
        assertFalse(engine.canPlace(wide, 7, 7))
        assertTrue(engine.canPlace(wide, 0, 5))
    }

    @Test
    fun singleLineClearScores() {
        val engine = GameEngine()
        // Fill row 0, columns 0..6; the last cell completes the row.
        for (c in 0..6) engine.board[0][c] = 1
        val result = engine.place(singleCell(), 0, 7)
        assertEquals(1, result!!.linesCleared)
        // 1 point for the cell + 10 for the line = 11.
        assertEquals(11, result.points)
        assertEquals(11, engine.score)
        // The row is empty again.
        for (c in 0 until 8) assertEquals(0, engine.board[0][c])
    }

    @Test
    fun doubleLineComboScores() {
        val engine = GameEngine()
        // Fill row 0 (cols 1..7) and column 0 (rows 1..7); (0,0) completes both.
        for (c in 1..7) engine.board[0][c] = 1
        for (r in 1..7) engine.board[r][0] = 1
        val result = engine.place(singleCell(), 0, 0)
        assertEquals(2, result!!.linesCleared)
        // 1 point for the cell + 10 * 2 lines * x2 combo = 41.
        assertEquals(41, result.points)
    }

    @Test
    fun streakBonusOnConsecutiveClears() {
        val engine = GameEngine()
        for (c in 0..6) engine.board[0][c] = 1
        engine.place(singleCell(), 0, 7) // 11 points, streak = 1
        for (c in 0..6) engine.board[1][c] = 1
        val second = engine.place(singleCell(), 1, 7)
        // 1 + 10 + 5 streak bonus = 16; total 27.
        assertEquals(16, second!!.points)
        assertEquals(27, engine.score)
    }

    @Test
    fun gameOverOnFullBoard() {
        val engine = GameEngine()
        for (r in 0 until 8) {
            for (c in 0 until 8) engine.board[r][c] = 1
        }
        assertTrue(engine.isGameOver(listOf(singleCell())))
        assertFalse(engine.anyMovePossible(listOf(singleCell())))
    }

    @Test
    fun movePossibleOnEmptyBoard() {
        val engine = GameEngine()
        val big = Shape(
            listOf(0 to 0, 0 to 1, 0 to 2, 1 to 0, 1 to 1, 1 to 2, 2 to 0, 2 to 1, 2 to 2),
            1
        )
        assertTrue(engine.anyMovePossible(listOf(big)))
        assertFalse(engine.isGameOver(listOf(big)))
    }
}
