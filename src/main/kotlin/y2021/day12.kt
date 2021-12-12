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
        if(child.first().isLowerCase())
            if(child in seenSmall) {
                if(extraVisit == child)
                    return@sumBy findAll(map, seenSmall, count, child, null)
                return@sumBy 0
            }

        val newSeen = seenSmall + when {
            child.first().isLowerCase() -> seenSmall + child
            else -> seenSmall
        }
        findAll(map, newSeen, count, child, extraVisit)
    } ?: 0
}