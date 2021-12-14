package aoc.area.y2021.d14

import java.io.File

fun main() {
    val lines = File("data/2021/d14.txt").readLines()
    val template = lines.first()
    val replacements = lines.drop(2).map {
        val (k,v) = it.split(" -> ")
        k to listOf("${k[0]}$v", "$v${k[1]}")
    }.toMap()


    val part1 = countPolymers(10, template, replacements)
    println("Part 1: $part1")
    assert(part1 == 3697L)

    val part2 = countPolymers(40, template, replacements)
    println(part2)
    assert(part2 == 4371307836157L)

}

fun countPolymers(iterations: Int, template: String, replacements: Map<String, List<String>>): Long {
    var templatePolymers = template.windowed(2, 1).groupingBy { it }.fold(0L) { acc, _ -> acc + 1 }

    // Iterate `iterations` times, starting with the template
    val result = (1..iterations).fold(templatePolymers) { polymers, _ ->
        // Map every pair into 2 new pairs
        polymers.flatMap { (polymer,count) -> replacements[polymer]!!.map { it to count } }
            // and fold the result into (pair,count) pairs
            .groupingBy { it.first }.fold(0L) {accumulator, element -> accumulator + element.second }
    }
    // Exploit the fact that Kotlin uses ordered maps by default and preserves order.
    val firstChar = result.entries.first().key[0] to 1L
    // Every char is present in 2 pairs, once in the beginning, and another time as the second element.
    // So we can get true count by counting the second element and ammending for the very first item, which does not have
    // a pair.
    val counts = (result.map { (k,v) -> k[1] to v } + firstChar)
        .groupingBy { it.first }.aggregate { _, acc: Long?, element, _ -> (acc ?: 0) + element.second }
    return counts.values.maxOrNull()!! - counts.values.minOrNull()!!
}