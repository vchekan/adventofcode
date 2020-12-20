package day10

import java.io.File

fun main() {
    val adapters = File("data/day10.2.txt").readLines().map { it.toInt() }.toList()
    val device = adapters.maxOrNull()!! + 3
    val sorted = (adapters + listOf(0,device)).sorted()
    part2(sorted)
}

fun part1(sorted: List<Int>) {
    val diffs = sorted.windowed(2).map { it[1] - it[0] }
    println("sorted: ${sorted.joinToString(",")}")
    println("diffs: ${diffs.joinToString(",")}")

    val dist = arrayOf(0,0,0,0)

//    dist[sorted[1]] += 1
//    dist[3] += 1

    for(i in 0 until sorted.size-1) {
        val delta = sorted[i+1] - sorted[i]
        dist[delta] += 1
    }

    // 2664; 2738;
    // 2775
    println(dist.joinToString(", "))

    println("part1: ${dist[1]*(dist[3])}")
}

fun part2(sorted: List<Int>) {
    var canSkip = 0
    if(sorted[1] <= 3)
        canSkip += 1

    // 1,2,4
    var pattern1 = 0
    sorted.windowed(3).forEach {w ->
        if(w[2] - w[0] == 3)
            pattern1 += 1
    }

    // 1,2,3,4
    var pattern2 = 0
    sorted.windowed(4).forEach {w ->
        if(w[3] - w[0] == 3)
            pattern2 += 1
    }

    println(sorted.joinToString(", "))

    // 2147483649;
    val res2 = 2L shl pattern1
    val res3 = 4L shl pattern2
    val res = res2 + res3
    //println("part2: $res")

    //
    //
    //
    var f = LongArray(sorted.size) { 0L }
    f[sorted.size - 1] = 1
    for (i in sorted.size - 2 downTo 0) {
        f[i] = f[i + 1]
        if (i + 3 < sorted.size && sorted[i + 3] <= sorted[i] + 3) {
            f[i] += f[i + 3]
        }
        if (i + 2 < sorted.size && sorted[i + 2] <= sorted[i] + 3) {
            f[i] += f[i + 2]
        }
    }
    println("their>"+f[0])

    // 0, 1,
    //      4,  5,  6, 7,
    //      10, 11, 12,
    // 15, 16,
    // 19,
    // 22
    //
    // ************************
    // 0, 1, 2, 3, 4, [5]
    // 7, 8, 9, 10, 11, [5]
    // 14,
    // 17, 18, 19, 20, [4]
    // 23, 24, 25, [3]
    // 28,
    // 31, 32, 33, 34, 35, [5]
    // 38, 39,
    // 42,
    // 45, 46, 47, 48, 49, [5]
    // 52
    // ************************
    var run = 0
    var count = 1L
    for(i in 0 until sorted.size-1) {
        if(sorted[i+1] - sorted[i] == 1)
            run += 1
        else {
            if(run >= 3) {
                val l = run - 2
//                println("run=$run l=$l c=${(l-1)*2+1}")
                count *= (l-1)*2+2
            }
            run = 1
        }
    }

    println("my>   $count")
}

// https://hackernoon.com/google-interview-questions-deconstructed-the-knights-dialer-f780d516f029
fun part22(sorted: List<Int>) {

}