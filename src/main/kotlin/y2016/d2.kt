package aoc.area.y2016

import java.io.File
import kotlin.math.abs

data class Pos(val col: Int, val row: Int) {
    fun move(move: Char): Pos =
        when(move) {
            'U' -> this.copy(row = row.dec().coerceAtLeast(0))
            'R' -> this.copy(col = col.inc().coerceAtMost(2))
            'D' -> this.copy(row = row.inc().coerceAtMost(2))
            'L' -> this.copy(col = col.dec().coerceAtLeast(0))
            else -> throw Exception("Invalid move: $move")
        }

    fun key() = col + 1 + row * 3

    private fun radius7() = abs(col - 2) + abs(row - 2)
    fun key7() = pad7[row][col]

    fun move7(move: Char): Pos {
        val pos2 = when(move) {
            'U' -> this.copy(row = row.dec())
            'R' -> this.copy(col = col.inc())
            'D' -> this.copy(row = row.inc())
            'L' -> this.copy(col = col.dec())
            else -> throw Exception("Invalid move: $move")
        }
        return if(pos2.radius7() > 2)
            this
        else
            pos2
    }
}

fun part1(lines: List<String>) {
    var pos = Pos(1, 1)
    val code = lines.map { line ->
        for (move in line) {
            pos = pos.move(move)
        }
        pos.key()
    }

    println(code.joinToString(""))
}

val pad7 = """  1
 234
56789
 ABC
  D""".lines()

fun part2(lines: List<String>) {
    var pos = Pos(0, 2)
    val code = lines.map { line ->
        for (move in line) {
            pos = pos.move7(move)
        }
        pos.key7()
    }

    println(code.joinToString(""))
}

fun main() {
    val lines = File("data/2016/day2.txt").readLines().filter(String::isNotEmpty)
    part1(lines)
    part2(lines)

}