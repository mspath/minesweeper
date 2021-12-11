package stage4

import kotlin.random.Random

/*
Objective stage 4

After initializing the field, all the numbers are shown to the player, but not the positions of the mines.
The player sees the message “Set/delete mine marks (x and y coordinates):” and enters two numbers
as coordinates on the field.
The user input is treated according to the rules:
If the player enters the coordinates of a non-marked cell, the program marks the cell, which
means that the player thinks a mine is located there.
If the player enters the coordinates of a cell with a number, the program should print the
message “There is a number here!” and ask the player again without printing the minefield,
since cells with numbers are guaranteed to be free of mines.
If the player enters the coordinates of a marked cell, the cell becomes unmarked.
This is necessary because the game ends only if all the marks are correct, but the player
can mark more cells than there are mines.
After successfully marking or unmarking a cell, the new minefield state is printed.
The symbol . is used to represent non-marked cells, and * is for marked ones. The prompt for the
player's next move is printed until the game is finished.
When the player marks all the mines correctly without marking any empty cells, they win and the game ends.
If the player has marked extra cells that do not contain mines, they continue playing. After clearing
all the excess mine marks, the player wins. Print the message “Congratulations! You found all the mines!”
after the final field state.
 */

fun Int.getIndicesOfNeighbors(rows: Int = 9, cols: Int = 9): List<Int> {
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

    return all
}

class Cell(var isMine: Boolean = false, var isMarked: Boolean = false)

// calculate the number of adjacent mines
fun calculateMines(mines: List<Boolean>, position: Int, rows: Int = Field.rows, cols: Int = Field.cols): Char {
    val neighbors = position.getIndicesOfNeighbors()
    val danger = mines.filterIndexed { index, _ -> index in neighbors }.count { it }
    if (danger > 0) return danger.toString().first()
    else return '.'
}

object Field {
    val rows = 9
    val cols = 9
    val cells: MutableList<Cell> = mutableListOf()

    fun displayField() {
        println(" │123456789│")
        println("—│—————————│")
        for (row in 0 until rows) {
            print("${row + 1}│")
            for (col in 0 until cols) {
                val index = row * cols + col
                val cell = cells[index]
                val symbol = if (cell.isMine && !cell.isMarked) '.'
                else if (cell.isMarked) '*'
                else calculateMines(cells.map { it.isMine }, index)
                print(symbol)
            }
            print("│\n")
        }
        println("—│—————————│\n")
    }

    fun toggleMine(index: Int) {
        val cell = cells[index]
        if (cell.isMine) {
            Field.cells[index].isMarked = !Field.cells[index].isMarked
            return
        }
        val neighbors = index.getIndicesOfNeighbors()
        val danger = cells.map { it.isMine }.filterIndexed { i, _ -> i in neighbors }.count { it }
        if (danger > 0) {
            println("There is a number here!")
            return
        }
        Field.cells[index].isMarked = !Field.cells[index].isMarked
    }

    fun checkSolved(): Boolean {
        return Field.cells.all { it.isMine == it.isMarked }
    }
}

fun main() {
    print("How many mines do you want on the field? ")
    val numberOfMines = readLine()!!.toInt()
    (0 until Field.rows * Field.cols).forEach {
        Field.cells.add(Cell())
    }
    while (Field.cells.count { it.isMine } < numberOfMines) {
        val next = Random.nextInt(Field.rows * Field.cols)
        Field.cells[next].isMine = true
    }
    Field.displayField()
    while (true) {
        print("Set/delete mine marks (x and y coordinates): ")
        val (x, y) = readLine()!!.split(" ").map { it.toInt() }
        println(x)
        println(y)
        val index = (y - 1) * Field.cols + (x - 1)
        Field.toggleMine(index)
        Field.displayField()
        if(Field.checkSolved()) {
            println("Congratulations! You found all the mines!")
            break
        }
    }
}