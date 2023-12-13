package aoc.area.y2023.d13

import java.io.File

val test = """#.##..##.
..#.##.#.
##......#
##......#
..#.##.#.
..##..##.
#.#.##.#.

#...##..#
#....#..#
..##..###
#####.##.
#####.##.
..##..###
#....#..#
"""

fun main() {
    val data = File("data/2023/day13.txt").readText()
    val maps = data.parse()
    val part1 = maps.sumOf { it.metric(::findHReflection2) }
    println(part1)

    val part2 = data.parse().sumOf { it.metric {findHReflection2(it, 1)} }
    println(part2)
}

fun String.parse(): List<List<String>> =
    this.split("\n\n").map { map ->
        map.lines().filter(String::isNotEmpty)
    }

fun List<String>.metric(f: (List<String>) -> Int?): Int {
    val h = f(this)
    if(h != null)
        return (h +1) * 100
    return f(this.transpose())!! + 1
}

fun findHReflection2(map: List<String>, errors: Int = 0): Int? {
    for(row in 0..map.size-2) {
        var diffs = map[row].diff(map[row+1])
        var i1 = row-1
        var i2 = row +2
        while(diffs <= errors && i1 >= 0 && i2 < map.size) {
            diffs += map[i1].diff(map[i2])
            i1--
            i2++
        }
        if(diffs == errors)
            return row
    }
    return null
}

fun List<String>.transpose(): List<String> =
    this[0].indices.map { c ->
        this.map { it[c] }.joinToString("")
    }

fun String.diff(b: String): Int =
    this.indices.count { this[it] != b[it] }