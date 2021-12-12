package y2021.d12

import java.io.File

fun main() {
    val map = File("data/2021/d12.txt").readLines().map {
            val(a, b) = Regex("([a-zA-Z]+)-([a-zA-Z]+)").find(it)!!.destructured
            Pair(a, b)
        }
        .let { it + it.map {(a,b) -> Pair(b, a) } } // graph is non-directional, which means both way travel is possible
        .filterNot { it.second == "start" }
        .groupingBy { it.first }.fold({_,_ -> mutableSetOf<String>()}, {_, acc, edge -> acc.add(edge.second); acc } )

    val part1 = findAll(map, emptySet(), 0, "start", null)
    println("Part 1: $part1")
    assert(part1 == 4573)

    val smallCaves = (map.keys + map.values.flatten() - "start" - "end").toSet()
    val part2 = part1 + smallCaves.sumOf { extra -> findAll(map, emptySet(), 0, "start", extra) }
    println("Part 2: $part2")
    assert(part2 == 117509)
}

fun findAll(map: Map<String,Set<String>>, seenSmall: Set<String>, count: Int, node: String, extraVisit: String?): Int {

    if(node == "end") {
        return when(extraVisit) {
            null -> count + 1
            else -> 0
        }
    }

    return map[node]?.sumBy { child ->
        val isSmallCave = child.first().isLowerCase()
        val seenCave = isSmallCave && child in seenSmall
        val useExtraVisit = child == extraVisit && seenCave

        return@sumBy when {
            // Big cave, keep walking
            !isSmallCave -> findAll(map, seenSmall, count, child, extraVisit)
            // Small cave unseen before, mark as visited
            !seenCave -> findAll(map, seenSmall + child, count, child, extraVisit)
            // Small cave seen before but can borrow visit
            useExtraVisit -> findAll(map, seenSmall, count, child, null)
            // Small cave seen before and no borrow visit left
            seenCave -> return@sumBy 0
            else -> throw Exception("Should not happen")
        }
    } ?: 0
}