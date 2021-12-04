package `2020`

import java.io.File

fun main() {
    val groups = File("data/day6.txt").readText().trim().split("\n\n")
    val part1 = groups.map { it.replace("\n", "").chars().distinct().count() }
        .sum()
    println(part1)

    val part2 = groups.map { group ->
        group.split('\n')
            .map(CharSequence::toSet)
            .reduce{a,b -> a.intersect(b)}.count()
    }.sum()


    println("part2: $part2")
}