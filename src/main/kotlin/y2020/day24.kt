package `2020`

import java.io.File
import java.lang.Exception

@ExperimentalStdlibApi
fun main() {
    part1()
}

fun part1() {
    val part1 = File("data/day24.txt").readLines()
        .map(::parse)
        .map(::move)
        .groupingBy {it}.eachCount()
        .map { it.value % 2 }
        .count { it == 1 }
    println("part1: $part1")
}

@ExperimentalStdlibApi
fun part2() {
    var plan = File("data/day24.txt").readLines()
        .map(::parse)
        .map(::move)
        .groupingBy {it}.eachCount()
        .map { Pair(it.key, it.value % 2 == 1) }
        .filter { it.second  }
        .map { it.first }
        .toSet()

    for(i in 1..100) {
        val remainingBlack = plan.filter {
            val blackAround = it.adjacent().count { plan.contains(it) }
            !(blackAround == 0 || blackAround > 2)
        }
        val newBlack = plan.flatMap { it.adjacent().filter { !plan.contains(it) }}
            .distinct()
            .filter {
                val adjacentBlacks = it.adjacent().count { plan.contains(it) }
                adjacentBlacks == 2
            }
        plan = (remainingBlack + newBlack).toSet()
    }

    println("part2: ${plan.size}")
}

fun parse(line: String) : List<Pair<Int,Int>> =
    Regex("(e)|(se)|(sw)|(w)|(nw)|(ne)").findAll(line).map {
        when(it.value) {
            "e" -> Pair(2,0)
            "w" -> Pair(-2,0)
            "se" -> Pair(1,-1)
            "sw" -> Pair(-1,-1)
            "nw" -> Pair(-1,1)
            "ne" -> Pair(1,1)
            else -> throw Exception("Unknown direction ${it.value}")
        }
    }.toList()

fun move(steps: List<Pair<Int,Int>>): Pair<Int,Int> {
    val r = steps.fold(Pair(0, 0)) { a, b -> Pair(a.first + b.first, a.second + b.second) }
//    println(r)
    return r
}

fun Pair<Int,Int>.adjacent() : List<Pair<Int,Int>> =
    listOf(
        2 to 0,
        -2 to 0,
        1 to 1,
        1 to -1,
        -1 to 1,
        -1 to -1
    ).map {Pair(first + it.first, second + it.second)}