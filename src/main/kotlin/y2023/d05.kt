package y2023.d5

import java.io.File
import kotlin.math.max
import kotlin.math.min

val test = """seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4
"""

fun main() {
    val (seedsTest, mappingsTest) = parse(test)
    println(seedsTest.minOfOrNull { seed -> mappingsTest.applyMappings(seed).first })

    val data = File("data/2023/day5.txt").readText()
    val (seeds, mappings) = parse(data)
    println(seeds.minOfOrNull {seed -> mappings.applyMappings(seed).first})

    // part 2
    val seedRanges = seeds.windowed(2, step = 2).map { Range(it[0], it[0] + it[1] - 1) }
    var min = Long.MAX_VALUE
    var count = 0
    for(range in seedRanges) {
        var n = range.start
        while(n <= range.end) {
            val (res, width) = mappings.applyMappings(n, Long.MAX_VALUE)
            n += width
            min = min(min, res)
            count++
        }
    }
    println("part2: $min, count: $count")
}

val rx = Regex("\\d+")

data class Range(val start: Long, val end: Long)

data class RangeMap(val dstStart: Long, val srcStart: Long, val length: Long) {
    val dstEnd: Long get() = dstStart + length - 1
    val srcEnd: Long get() = srcStart + length - 1
}
typealias Mapping = List<RangeMap>

fun String.parse(): RangeMap {
    val (dst, src, len) = rx.findAll(this).map { it.value.toLong() }.toList()
    return RangeMap(dst, src, len)
}

fun parse(almanac: String): Pair<List<Long>, List<Mapping>> {
    val sections = almanac.split("\n\n")
    val header = rx.findAll(sections.first()).map { it.value.toLong() }.toList()
    val mappings = sections.drop(1).map(String::lines).map { section ->
        section.drop(1).filter(String::isNotEmpty).map(String::parse)
    }
    return header to mappings
}

fun Mapping.apply(n: Long, width: Long): Pair<Long, Long> {
    for(range in this) {
        if(n in range.srcStart..range.srcEnd) {
            val n2 = range.dstStart + (n - range.srcStart)
            val width2 = range.dstEnd - n2 + 1
            return n2 to min(width, width2)
        }
    }
    return n to width
}

fun List<Mapping>.applyMappings(n: Long, width: Long = Long.MAX_VALUE): Pair<Long, Long> {
    return this.fold(n to width) {acc, mapping -> mapping.apply(acc.first, acc.second) }
}
