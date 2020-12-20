package day15

import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@ExperimentalTime
fun main() {
    val time0 = System.nanoTime()
    val p1 = solve(2020)
    val time1 = System.nanoTime()
    val p2 = solve(30000000)
    val time2 = System.nanoTime()
    val d1 = (time1 - time0).toDuration(DurationUnit.NANOSECONDS)
    val d2 = (time2 - time0).toDuration(DurationUnit.NANOSECONDS)
    println("part1: $p1 ($d1)")
    println("part2: $p2 ($d2)")
    println("max: $max")
}

val data = "9,6,0,10,18,2,1"
    .split(',').map { it.toInt() }
var max = 0

fun solve(target: Int): Int {
    val input = data.take(data.size-1).withIndex().map { Pair(it.value, it.index) }
        .toMap().toMutableMap()

    var last = data.last()
    for(turn in input.size until target-1) {
        val turn0 = input[last]
        val num = if(turn0 == null)
            0
        else
            turn - turn0
        input[last] = turn
        last = num
        if(num > max)
            max = num
    }
    return last
}