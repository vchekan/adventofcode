package y2021.d6

import java.io.File

fun main() {
    val timers = //"3,4,3,1,2".split(',').map(String::toInt)
        File("data/2021/d6.txt").readLines()[0].split(',').map(String::toInt)
    val part1 = solution2(timers, 80)
    val part2 = solution2(timers, 256)
    println(part1)
    println(part2)
    assert(part1 == 390011L)
    assert(part2 == 1746710169834)
}

fun solution1(init: List<Int>, days: Int): Long {
    val counts = LongArray(9)
    init.forEach{ timer -> counts[timer]++ }

    for(day in 1..days) {
        val count0 = counts[0]
        for(timer in 1 until counts.size)
            counts[timer-1] = counts[timer]
        counts[8] = count0
        counts[6] += count0
    }

    return counts.sum()
}

fun solution2(init: List<Int>, days: Int): Long {
    val counts = LongArray(9)
    init.forEach{ timer -> counts[timer]++ }
    var p0 = 0
    var p6 = 6
    for(day in 1..days) {
        val count0 = counts[p0]
        p0 = p0.wrap(8)
        p6 = p6.wrap(8)
        counts[p6] += count0
    }
    return counts.sum()
}

fun Int.wrap(max: Int): Int = if(this < max) this + 1 else 0
