package aoc.area.y2023.d11

import aoc.area.Area
import aoc.area.Point
import aoc.area.get
import java.io.File
import kotlin.math.absoluteValue

val test = """...#......
.......#..
#.........
..........
......#...
.#........
.........#
..........
.......#..
#...#.....
"""

var dist = 1000000L - 1L

fun main() {
    val data = File("data/2023/day11.txt").readText()
    val map = data.parse()
    val doubleRows = map.withIndex().filter { it.value.none { it == '#' } }.map { it.index }
    val doubleCols = map.first().indices.filter { col -> map.none { row -> row[col] == '#' } }
    val galaxies = map.indices.flatMap { r ->
        map[r].indices.map { c ->
            Point(r, c)
        }
    }.filter { map[it] == '#' }

    val galaxiPairs = (0..galaxies.size - 2).flatMap { g1 ->
        (g1+1..< galaxies.size).map { g2 -> galaxies[g1] to galaxies[g2] }
    }

    val distances = galaxiPairs.map { (g1, g2) ->
        (g1.row - g2.row).absoluteValue +
                (g1.col - g2.col).absoluteValue +
                doubleRows.count { r ->inRange(r,g1.row, g2.row) } * dist +
                doubleCols.count { c -> inRange(c, g1.col, g2.col) } * dist
    }

    println(distances.sum())
}

fun String.parse(): Area<Char> =
    this.lines().filter(String::isNotEmpty).map { it.toList() }

fun inRange(v: Int, a: Int, b: Int): Boolean =
    if(a < b)
        v in a..b
    else
        v in b..a
