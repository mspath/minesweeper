package stage1

import kotlin.random.Random

/*
Objective

Your first step is easy: you need to output some state of the minefield.
Set the minefield size and place any number of mines you want on it.
At this point, all the mines are there in plain sight – we are not going to hide them from the player just yet.
You can use any character you want to represent mines and safe cells at this step.
Later on, we will use X for mines and . for safe cells.
 */

fun main() {
    val rows = 9
    val cols = 9
    for (row in 0 until rows) {
        for (col in 0 until cols) {
            val isMine = Random.nextInt(100) % 5 == 0
            val symbol = if (isMine) 'X' else '.'
            print(symbol)
        }
        println()
    }
}