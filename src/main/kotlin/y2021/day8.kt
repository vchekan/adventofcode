package y2021.d8

import java.io.File
import kotlin.math.sign

fun main() {
    // 1: c f       [2]
    // 4: b c d f   [4]
    // 7: a c f     [3]
    // 8: a b c d e f g [7]
    //
    // 0, 6, 9: [6]
    // 2, 3, 5: [5]

    val lines = File("data/2021/d8.txt").readLines()
    val part1 = //listOf("acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf")
        lines.map {
            val(left, right) = it.split(" | ")
            right.split(" ").count { it.length in arrayOf(2, 3, 4, 7) }
        }.sum()

    println(part1)

    //
    //
    //
    val signalToDisplayMap = mutableMapOf<Char,Set<Char>>()
    for(c in "abcdefg")
        signalToDisplayMap[c] = "abcdefg".toSet()

    val nums = lines.map {
        val(left, right) = it.split(" | ")
        val map = findBase(left.split(' ').map(String::toSet))
        val digits = right.split(' ').map { n -> map[n.toSet()]!! }
        digits.fold(0) {acc, i -> acc*10 + i }
    }

    println(nums.sum())
}

// return [1,4,7,8]
fun findBase(signals: List<Set<Char>>): Map<Set<Char>,Int> {
    val (s1, s4, s7, s8) = listOf(
        signals.find { it.size == 2 }!!,
        signals.find { it.size == 4 }!!,
        signals.find { it.size == 3 }!!,
        signals.find { it.size == 7 }!!,
    )

    val a = (s7 - s1).single()
    val eg = s8 - s4 - a
    val bd = s4 - s7
    val s9 = signals.single { it.size == 6 && (it intersect eg != eg) }
    val e = (s8 - s9).single()
    val s0 = signals.single { it.size == 6 && (it intersect bd != bd) }
    val s6 = signals.single { it.size == 6 && it != s0 && it != s9 }
    val s5 = s6 - e
    val s2 = signals.single { it.size == 5 && it.contains(e) }
    val s3 = signals.single { it.size == 5 && it intersect s1 == s1 }
    val f = (s3 - s2).single()

    return listOf(s0,s1,s2,s3,s4,s5,s6,s7,s8,s9).mapIndexed {index, set -> Pair(set, index)}.toMap()
}