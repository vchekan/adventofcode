package y2021.d1

import java.io.File

fun main() {
    val depths = File("data/2021/d1.txt").readLines()
        .map(String::toInt)

    val part1 = depths.zip(depths.drop(1)).count { it.first < it.second }
    println(part1)

    val windows = depths.windowed(3, 1) {it.sum()}
    val part2 = windows.zip(windows.drop(1)).count { it.first < it.second }
    println(part2)
}

