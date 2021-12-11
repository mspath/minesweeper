package minesweeper

import kotlin.random.Random

fun Int.getIndicesOfNeighbors(): List<Int> {
    val nw = this - Field.cols - 1
    val n = this - Field.cols
    val ne = this - Field.cols + 1
    val w = this -1
    val e = this + 1
    val sw = this + Field.cols - 1
    val s = this + Field.cols
    val se = this + Field.cols + 1
    val colLeft = setOf(nw, w, sw)
    val colRight = setOf(ne, e, se)
    var all = listOf(nw, n, ne, w, e, sw, s, se)
        .filter { it in 0 until Field.rows * Field.cols }
    if (this % Field.cols == 0) all = all.filterNot { it in colLeft }
    if (this % Field.cols == Field.cols -1) all = all.filterNot { it in colRight }
    return all
}

data class Cell(val id: Int,
                var isMine: Boolean = false,
                var isMarked: Boolean = false,
                var isClaimed: Boolean = false) {

    fun countMines(): Int {
        val neighbors = id.getIndicesOfNeighbors()
        val mines = Field.cells.map { it.isMine }
        val danger = mines.filterIndexed { index, _ -> index in neighbors }.count { it }
        return danger
    }

    fun getSymbol(): Char {
        val symbol = if (isMarked && !isClaimed) '*'
            else if (isMine && Field.cells.filter { it.isMine && it.isClaimed }.isNotEmpty()) 'X'
            // else if (isMine && !isMarked) 'o' // dev
            // else if (isMine && isMarked) '*'
            // else if (isMarked) 'x'
            else if (isClaimed && countMines() > 0) countMines().toString().first()
            else if (isClaimed) '/'
            else '.'
        return symbol
    }
}

object Field {
    val rows = 9
    val cols = 9
    var mines = 0
    val cells: List<Cell> = buildList { (0 until rows * cols).forEach { this.add(Cell(it)) } }

    fun buildField(numberOfMines: Int) {
        mines = numberOfMines
        while (cells.filter { it.isMine }.size < mines) {
            cells[Random.nextInt(rows * cols)].isMine = true
        }
    }

    fun displayField() {
        println("\n │123456789│")
        println("—│—————————│")
        for (row in 0 until rows) {
            print("${row + 1}│")
            for (col in 0 until cols) {
                val index = row * cols + col
                val cell = cells[index]
                val symbol = cell.getSymbol()
                print(symbol)
            }
            print("│\n")
        }
        println("—│—————————│\n")
    }

    fun isGameOver(): Boolean {
        // (1) user steps on a mine
        if (cells.filter { it.isMine && it.isClaimed }.isNotEmpty()) {
            println("You stepped on a mine and failed!")
            return true
        }
        // (2) all mines are marked as mines
        if (cells.filter { it.isMine && it.isMarked }.size == mines && cells.count { it.isMarked } == mines) {
            println("Congratulations! You found all the mines!")
            return true
        }
        // (3) all non mine cells have been claimed
        if (cells.count { !it.isMine && it.isClaimed } == cells.size - mines) {
            println("Congratulations! You found all the mines!")
            return true
        }
        return false
    }

    // this just can be set once for each cell
    // todo first claim must not be a mine, i.e. rebuild mine if necessary
    fun claim(index: Int) {
        // println("claiming $index")
        val cell = cells[index]
        cell.isMarked = false

        // enabling recursive calls, just return without doing anything
        if (cell.isClaimed) return
        cell.isClaimed = true

        // (1) cell is mine, just return, the game will be over
        if (cell.isMine) return

        // (2) cell is direct neighbor, just return the number will be shown from now on
        if (cell.countMines() > 0) return

        // (3) cell is free; recursively expand basin
        val neighbors = cell.id.getIndicesOfNeighbors().map { cells[it] }.filterNot { it.isMine }
        val neighborBorders = neighbors.filter { it.countMines() > 0 }
        neighborBorders.forEach { it.isClaimed = true }
        val neighborFree = neighbors.filter { it.countMines() == 0 }
        return neighborFree.forEach { claim(it.id) }
    }

    // marks can be toggled on/off if a cell has not been claimed yet and that's about it
    fun mark(index: Int) {
        val cell = cells[index]
        if (cell.isClaimed) return
        cell.isMarked = !cell.isMarked
    }
}

fun main(args: Array<String>) {
    initField()
    while (true) {
        print("Set/unset mine marks or claim a cell as free: ")
        val (x, y, action) = readLine()!!.split(" ")
        val index = (x.toInt() - 1) + (y.toInt() - 1) * Field.cols
        when(action) {
            "free" -> Field.claim(index)
            "mine" -> Field.mark(index)
            else -> println()
        }
        Field.displayField()
        if (Field.isGameOver()) break
    }
}

fun initField() {
    print("How many mines do you want on the field? ")
    val numberOfMines = readLine()!!.toInt()
    Field.buildField(numberOfMines)
    Field.displayField()
}