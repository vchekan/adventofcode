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

fun findHReflection2(map: List<String>, allowedErrors: Int = 0): Int? =
    (0..map.size-2).find { row ->
        val half1 = map.take(row + 1).reversed()
        val half2 = map.drop(row +1)
        val errors = half1.zip(half2).sumOf { (l1, l2) -> l1.diff(l2) }
        errors == allowedErrors
    }

fun List<String>.transpose(): List<String> =
    this[0].indices.map { c ->
        this.map { it[c] }.joinToString("")
    }

fun String.diff(b: String): Int = this.zip(b).count { it.first != it.second }
