package com.blockblast.lite.game

/**
 * Result of placing a shape: points earned, how many lines cleared,
 * and which rows/columns were cleared.
 */
data class PlaceResult(
    val points: Int,
    val linesCleared: Int,
    val clearedRows: List<Int>,
    val clearedCols: List<Int>
)

/**
 * Pure game logic — no Android imports, so it is unit-testable.
 * Board is 8x8; a cell holds 0 (empty) or a colorId (1..5).
 *
 * Scoring (per plan):
 * - 1 point per placed cell
 * - 10 points per cleared line, multiplied by the combo:
 *   1 line = x1, 2 lines at once = x2, 3+ lines at once = x3
 * - +5 streak bonus when a line is cleared on consecutive moves
 */
class GameEngine {
    val board: Array<IntArray> = Array(8) { IntArray(8) }

    var score: Int = 0
        private set

    /** Consecutive moves in which at least one line was cleared. */
    var streak: Int = 0
        private set

    fun reset() {
        for (r in 0 until 8) {
            for (c in 0 until 8) board[r][c] = 0
        }
        score = 0
        streak = 0
    }

    fun canPlace(shape: Shape, row: Int, col: Int): Boolean {
        for ((dr, dc) in shape.cells) {
            val r = row + dr
            val c = col + dc
            if (r !in 0 until 8 || c !in 0 until 8) return false
            if (board[r][c] != 0) return false
        }
        return true
    }

    /** Places the shape or returns null when the placement is illegal. */
    fun place(shape: Shape, row: Int, col: Int): PlaceResult? {
        if (!canPlace(shape, row, col)) return null
        for ((dr, dc) in shape.cells) {
            board[row + dr][col + dc] = shape.colorId
        }
        val fullRows = (0 until 8).filter { r -> (0 until 8).all { c -> board[r][c] != 0 } }
        val fullCols = (0 until 8).filter { c -> (0 until 8).all { r -> board[r][c] != 0 } }
        val lines = fullRows.size + fullCols.size
        var points = shape.size
        if (lines > 0) {
            val multiplier = minOf(lines, 3)
            points += 10 * lines * multiplier
            if (streak >= 1) points += 5
            streak++
        } else {
            streak = 0
        }
        for (r in fullRows) {
            for (c in 0 until 8) board[r][c] = 0
        }
        for (c in fullCols) {
            for (r in 0 until 8) board[r][c] = 0
        }
        score += points
        return PlaceResult(points, lines, fullRows, fullCols)
    }

    fun anyMovePossible(shapes: List<Shape>): Boolean {
        for (shape in shapes) {
            for (r in 0 until 8) {
                for (c in 0 until 8) {
                    if (canPlace(shape, r, c)) return true
                }
            }
        }
        return false
    }

    fun isGameOver(shapes: List<Shape>): Boolean = !anyMovePossible(shapes)
}
