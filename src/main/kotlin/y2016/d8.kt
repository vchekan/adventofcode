package aoc.area.y2016

import aoc.area.display
import java.io.File

fun main() {
    // UPOJFLBCEZ 114: low
    val w = 50
    val h = 6
    val lines =
//        """rect 3x2
//        |rotate column x=1 by 1
//        |rotate row y=0 by 4
//        |rotate column x=1 by 1
//    """.trimMargin().lines()
        File("data/2016/day8.txt").readLines()
    val rxRect = Regex("""rect (\d+)x(\d+)""")
    val rxRotate = Regex("""rotate (.*) (x|y)=(\d+) by (\d+)""")
    val area = Array(h) { Array(w) { '.' }.toMutableList() }.toList()
    for (line in lines) {
        when {
            line.startsWith("rect") -> {
                val (width, height) = rxRect.matchEntire(line)?.destructured ?: throw Exception("Invalid 'rect' command: $line")
                for(col in 0 until width.toInt()) {
                    for(row in 0 until height.toInt()) {
                        area[row][col] = '#'
                    }
                }
            }
            line.startsWith("rotate") -> {
                // rotate row y=0 by 3
                val (axis, xOrY, xyValue, by) = rxRotate.matchEntire(line)?.destructured ?: throw Exception("Invalid 'rotate' command: $line")
                val byNormalized = by.toInt()

                when(axis) {
                    "column" -> {
                        val col = xyValue.toInt()
                        rightRotate({ row -> area[row][col]}, { row, v -> area[row][col] = v}, area.size, byNormalized)
                    }
                    "row" -> {
                        val row = xyValue.toInt()
                        rightRotate({ col -> area[row][col]}, { col, v -> area[row][col] = v}, area[0].size, byNormalized)
                    }
                }
            }
        }
        println(area.display())
        println()
    }

    val part1 = area.sumOf { it.count { it == '#' } }
    println(part1)
}

fun gcd(a: Int, b: Int): Int =
    when(b) {
        0 -> a
        else -> gcd(b, a % b)
    }

/** Juggling shifting */
fun <T> leftRotate(getter: (i: Int) -> T, setter: (i: Int, T) -> Unit, n: Int, d: Int) {
    for(i in 0 until gcd(d, n)) {
        val tmp = getter(i)
        var j = i
        while(true) {
            var k = j + d
            if(k >= n)
                k -= n
            if(k == i)
                break
            setter(j, getter(k))
            j = k
        }
        setter(j, tmp)
    }
}

fun <T> rightRotate(getter: (i: Int) -> T, setter: (i: Int, T) -> Unit, n: Int, d: Int) =
    leftRotate(getter, setter, n, n - d)

