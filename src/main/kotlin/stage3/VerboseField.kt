package stage3

import kotlin.math.min
import kotlin.random.Random

/*
Objective stage 3

If there are mines around the cell, display the number of mines (from 1 to 8) instead of the symbol
representing an empty cell. The symbols for empty cells and mines stay the same.
 */

// since we learned about extension functions I might make use of them here
// this function assumes to be an index in a list representing a field
// it will return a set of all neighboring cells
fun Int.getIndicesOfNeighbors(rows: Int = 9, cols: Int = 9): Set<Int> {
    val nw = this - cols - 1
    val n = this - cols
    val ne = this - cols + 1
    val w = this -1
    val e = this + 1
    val sw = this + cols - 1
    val s = this + cols
    val se = this + cols + 1

    val colLeft = setOf(nw, w, sw)
    val colRight = setOf(ne, e, se)

    var all = setOf(nw, n, ne, w, e, sw, s, se).filter { it in 0 until rows * cols }
    if (this % cols == 0) all = all.filterNot { it in colLeft }
    if (this % cols == cols -1) all = all.filterNot { it in colRight }

    return all.toSet()
}

// calculate the number of adjacent mines
fun calculateMines(mines: List<Boolean>, position: Int, rows: Int = 9, cols: Int = 9): Char {
    val neighbors = position.getIndicesOfNeighbors()
    val danger = mines.filterIndexed { index, _ -> index in neighbors }.count { it }
    if (danger > 0) return danger.toString().first()
    else return '.'
}

fun main() {
    print("How many mines do you want on the field? ")
    val numberOfMines = readLine()!!.toInt()
    val rows = 9
    val cols = 9
    val mines: MutableList<Boolean> = mutableListOf()
    (0 until rows * cols).forEach {
        mines.add(false)
    }
    while (mines.count { it } < numberOfMines) {
        val next = Random.nextInt(rows * cols)
        mines[next] = true
    }
    for (row in 0 until rows) {
        for (col in 0 until cols) {
            val symbol = if (mines[row * cols + col]) 'X' else calculateMines(mines, row * cols + col)
            print(symbol)
        }
        println()
    }
}