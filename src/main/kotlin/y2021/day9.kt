package y2021.d9

import java.io.File
import aoc.area.*

fun main() {
    val map = File("data/2021/d9.txt").readLines().map { it.map { it - '0' } }

    val minBasins = map.points().filter { map.isMin(it) }
    var part1 = minBasins.sumOf { 1 + map[it] }
    println("Part 1: $part1")
    assert(part1 == 436)

    val part2 = minBasins.map { map.findBasinSize(it) }.sortedDescending().take(3).reduce { acc, i -> acc * i }

    println("Part 2: $part2")
    assert(part2 == 1317792)
}

fun List<List<Int>>.isMin(p: Point): Boolean = this.around4(p).all { this[it] > this[p] }

fun List<List<Int>>.findBasinSize(p: Point): Int {
    val seen: MutableSet<Point> = mutableSetOf()
    var newgen = listOf(p)
    while(newgen.isNotEmpty()) {
        val newgen2 = newgen
            .flatMap { oldPoint ->
                this.around4(oldPoint)
                    .filter { newpoint ->
                        val v = this[newpoint]
                        v != 9 && newpoint !in seen && v > this[oldPoint]
                    }
            }
        
        seen.addAll(newgen)
        newgen = newgen2
    }

    return seen.size
}