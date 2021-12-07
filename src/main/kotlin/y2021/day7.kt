package y2021.d7

import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val positions = //listOf(16,1,2,0,4,2,7,1,2,14)
        File("data/2021/d7.txt").readLines()[0].split(',').map(String::toInt)

    val p = (0..positions.maxOrNull()!!).map { pos -> positions.sumOf { (pos - it).absoluteValue } }
    val part1 = p.minOrNull()
    println(part1)
    assert(part1 == 343605)

    val p2 = (0..positions.maxOrNull()!!).map { pos1 ->
        positions.sumOf { pos2 ->
            val delta = (pos1 - pos2).absoluteValue
            (delta * (delta + 1)) / 2
        }
    }
    val part2 = p2.minOrNull()
    println(part2)
    assert(part2 == 96744904)
}