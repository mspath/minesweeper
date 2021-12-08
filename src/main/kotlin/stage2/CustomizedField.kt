package stage2

import kotlin.random.Random

/*
Objective stage 2

Your program should ask the player to define the number of mines to add to a 9x9 field with the message
"How many mines do you want on the field?". It should then use the input to initialize the field and
display it with the mines. At this point, the mines are still visible to the player;
you will hide them later.
 */

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
            val symbol = if (mines[row * cols + col]) 'X' else '.'
            print(symbol)
        }
        println()
    }
}