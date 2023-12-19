package aoc.area.y2023.d17

import aoc.area.around4ClockwiseWithDir
import java.io.File
import java.util.PriorityQueue
import kotlin.math.abs

val test = """2413432311323
3215453535623
3255245654254
3446585845452
4546657867536
1438598798454
4457876987766
3637877979653
4654967986887
4564679986453
1224686865563
2546548887735
4322674655533
"""

data class Point(val row: Int, val col: Int, val dir: Int = 0, val cost: Int, val prev: Point?, var run: Int = 1) {
    init {
        if (row < 0)
            throw Exception()
    }
}

fun main() {
    val data = File("data/2023/day17.txt").readText()
    val map = data.lines().filter(String::isNotEmpty).map {line ->
        line.map(Char::digitToInt)
    }
    val part1 = map.path(0, 3)
    println(part1)

    println(map.path(4, 10))
}

fun List<List<Int>>.path(min: Int, max: Int): Int {
    val queue = PriorityQueue<Point>(this.size * 2) { p1, p2 -> p1.cost - p2.cost }
    val start = Point(0, 0, 1, 0, null)
    val visited = HashSet<Triple<Int,Int,Int>>()
    queue.add(start)
    queue.add(start.copy(dir = 2))
    while(true) {
        val p1 = queue.poll()!!
        if(p1.row == this.size - 1 && p1.col == this[0].size - 1) {
            printBacktrace(this.size to this[0].size, p1)
            return p1.cost
        }
        this.around4ClockwiseWithDir(p1.row, p1.col)
            .forEach { (r, c, dir) ->
                if(abs(p1.dir - dir) == 2)  // disable turning back
                    return@forEach
                val run = if(p1.dir == dir) p1.run + 1 else 1
                val enable = run <= max && // do not exceed max run
                        !(dir != p1.dir && p1.run < min)    // do not turn until min run
                if(enable && visited.add(Triple(r,c, run.shl(2).or(dir)))) {
                    val p = Point(r, c, dir, p1.cost + this[r][c], p1, run)
                    queue.add(p)
                }
            }
    }
}

fun printBacktrace(dims: Pair<Int,Int>, end: Point) {
    val (rows, cols) = dims
    var p: Point? = end
    val map = Array(rows) {CharArray(cols) {'.'} }
    while(p != null) {
        map[p.row][p.col] = "^>v<"[p.dir]
        p = p.prev
    }

    for(r in 0..< rows)
        println(map[r].concatToString())
    println()
}
