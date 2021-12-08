package y2021.d8

import java.io.File

fun main() {
    val lines = File("data/2021/d8.txt").readLines().map { it.split(" | ") }
    val part1 = //listOf("acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf")
        lines.sumOf { (left, right) -> right.split(" ").count { it.length in arrayOf(2, 3, 4, 7) } }
    println(part1)
    assert(part1 == 355)

    // part 2
    val nums = lines.map { (left, right) ->
        val map = findBase(left.split(' ').map(String::toSet))
        val digits = right.split(' ').map { n -> map[n.toSet()]!! }
        digits.fold(0) {acc, i -> acc*10 + i }
    }
    val part2 = nums.sum()
    println(part2)
    assert(part2 == 983030)
}


fun findBase(signals: List<Set<Char>>): Map<Set<Char>,Int> {
    // 1: c f       [2]
    // 4: b c d f   [4]
    // 7: a c f     [3]
    // 8: a b c d e f g [7]
    //
    // 0, 6, 9: [6]
    // 2, 3, 5: [5]
    val sizes: List<MutableSet<Set<Char>>> = (0..9).map{ mutableSetOf<Set<Char>>() }.toList()
    signals.groupingBy { it.size }.aggregate { key, _: Boolean?, element: Set<Char>, _ -> sizes[key].add(element) }

    val s1 = sizes[2].single()
    val s4 = sizes[4].single()
    val s7 = sizes[3].single()
    val s8 = sizes[7].single()

    val bd = s4 - s7
    // Among those of size of 5, digit 5 is the only one which has "bd" segments
    val s5 = sizes[5].single { (bd subtract it).isEmpty()}
    // Difference between digit 2 and digit 5 is 2 segments, but difference between 5 and 3 is one segment
    val s2 = sizes[5].single { (s5 subtract it).size == 2 }
    val s3 = sizes[5].single { (s5 subtract it).size == 1 }
    // 9 is the only one among 5-segments which contains 3
    val s9 = sizes[6].single { (s3 subtract it).isEmpty() }
    // 0 is the only one which contains 1 and is not 8
    val s0 = sizes[6].single { it !== s9 && (s1 subtract it).isEmpty() }
    val s6 = sizes[6].single { it !== s0 && it !== s9 }

    return listOf(s0,s1,s2,s3,s4,s5,s6,s7,s8,s9).mapIndexed {index, set -> Pair(set, index)}.toMap()
}