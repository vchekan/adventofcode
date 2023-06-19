package aoc.area.y2016.d13

val input = 1358

data class PointRC(val row: Int, val col: Int) {
    val isOpen get(): Boolean = (col*col + 3*col + 2*col*row + row + row*row + input).countOneBits() and 1 == 0
    fun around4(): List<PointRC> = listOf(
        copy(row = row - 1),
        copy(row = row + 1),
        copy(col = col - 1),
        copy(col = col + 1)
    ).filter {
        it.row >= 0 && it.col >= 0 && it.isOpen
    }
}

fun main() {
    val target = PointRC(39, 31)
    val walk = Walk()
    val part1 = generateSequence(walk::step).indexOfFirst { target in walk.current } + 1
    println(part1)

    val walk2 = Walk()
    (1..50).forEach { walk2.step() }
    val part2 = walk2.visited.count()
    println(part2)
}

class Walk {
    val start = PointRC(1,1)
    var current = setOf(start)
    val visited = mutableSetOf(start)

    fun step() {
        current = current.flatMap(PointRC::around4).filter(visited::add).toSet()
    }
}
