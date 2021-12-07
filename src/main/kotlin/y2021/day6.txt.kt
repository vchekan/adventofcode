package y2021.d6

import java.io.File

fun main() {
    val timers = //"3,4,3,1,2".split(',').map(String::toInt)
        File("data/2021/d6.txt").readLines()[0].split(',').map(String::toInt)
    println(solution1(timers, 80))
    println(solution1(timers, 256))
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