package aoc.area.y2016

import java.io.File

fun solve() {

}

fun main() {
    val lines = File("data/2016/day6.txt").readLines()
    val len = lines[0].length

    val msg = (0 until len).map { i ->
        val freq = lines.map{ it[i] }.groupingBy { it }.eachCount().entries.sortedWith(compareBy { it.value })
        freq.last().key to freq.first().key
    }

    println(msg.map { it.first }.toCharArray().concatToString())
    println(msg.map { it.second }.toCharArray().concatToString())

}