package y2021.d2

import java.io.File

fun main() {
    var x = 0
    var y = 0
    val moves = File("data/2021/d2.txt").readLines()
    moves.map {
        val (dir, n) = it.split(' ')
        val nn = n.toInt()
        when(dir) {
            "forward" -> x += nn
            "down" -> y += nn
            "up" -> y -= nn
            else -> throw Exception()
        }
    }

    val part1 = x * y
    println(part1)

    x = 0
    y = 0
    var aim = 0
    moves.map {
        val (dir, n) = it.split(' ')
        val nn = n.toInt()
        when(dir) {
            "forward" -> {
                x += nn
                y += aim * nn
            }
            "down" -> aim += nn
            "up" -> aim -= nn
            else -> throw Exception()
        }
    }
    val part2 = x * y
    println(part2)
}
