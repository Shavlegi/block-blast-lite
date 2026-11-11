package com.blockblast.lite.game

import kotlin.random.Random

/**
 * One block piece. [cells] are (row, col) offsets from the piece's top-left cell.
 * [colorId] is 1..5. [letter] is a Georgian letter in Alphabet mode, null otherwise.
 */
data class Shape(
    val cells: List<Pair<Int, Int>>,
    val colorId: Int,
    val letter: String? = null
) {
    val width: Int get() = (cells.maxOfOrNull { it.second } ?: 0) + 1
    val height: Int get() = (cells.maxOfOrNull { it.first } ?: 0) + 1
    val size: Int get() = cells.size
}

/** Georgian alphabet: letter to its name (ა — ანი, ბ — ბანი, ...). */
val GEORGIAN_LETTERS: List<Pair<String, String>> = listOf(
    "ა" to "ანი", "ბ" to "ბანი", "გ" to "განი", "დ" to "დონი", "ე" to "ენი",
    "ვ" to "ვინი", "ზ" to "ზენი", "თ" to "თანი", "ი" to "ინი", "კ" to "კანი",
    "ლ" to "ლასი", "მ" to "მანი", "ნ" to "ნარი", "ო" to "ონი", "პ" to "პარი",
    "ჟ" to "ჟანი", "რ" to "რაე", "ს" to "სანი", "ტ" to "ტარი", "უ" to "უნი",
    "ფ" to "ფარი", "ქ" to "ქანი", "ღ" to "ღანი", "ყ" to "ყარი", "შ" to "შინი",
    "ჩ" to "ჩინი", "ც" to "ცანი", "ძ" to "ძილი", "წ" to "წილი", "ჭ" to "ჭარი",
    "ხ" to "ხარი", "ჯ" to "ჯანი", "ჰ" to "ჰაე"
)

private val SHAPE_TEMPLATES: List<List<Pair<Int, Int>>> = listOf(
    listOf(0 to 0),
    listOf(0 to 0, 0 to 1),
    listOf(0 to 0, 1 to 0),
    listOf(0 to 0, 0 to 1, 0 to 2),
    listOf(0 to 0, 1 to 0, 2 to 0),
    listOf(0 to 0, 0 to 1, 0 to 2, 0 to 3),
    listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0),
    listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1),
    listOf(0 to 0, 0 to 1, 0 to 2, 1 to 0, 1 to 1, 1 to 2),
    listOf(0 to 0, 0 to 1, 0 to 2, 1 to 0, 1 to 1, 1 to 2, 2 to 0, 2 to 1, 2 to 2),
    listOf(0 to 0, 1 to 0, 1 to 1),
    listOf(0 to 0, 0 to 1, 1 to 1),
    listOf(0 to 1, 1 to 0, 1 to 1),
    listOf(0 to 0, 1 to 0, 1 to 1),
    listOf(0 to 0, 0 to 1, 1 to 0),
    listOf(0 to 1, 0 to 2, 1 to 0, 1 to 1),
    listOf(0 to 0, 0 to 1, 1 to 1, 1 to 2)
)

fun randomShape(random: Random, alphabetMode: Boolean): Shape {
    val cells = SHAPE_TEMPLATES[random.nextInt(SHAPE_TEMPLATES.size)]
    val colorId = 1 + random.nextInt(5)
    val letter = if (alphabetMode) {
        GEORGIAN_LETTERS[random.nextInt(GEORGIAN_LETTERS.size)].first
    } else {
        null
    }
    return Shape(cells, colorId, letter)
}

fun newTray(random: Random, alphabetMode: Boolean): List<Shape> =
    List(3) { randomShape(random, alphabetMode) }
