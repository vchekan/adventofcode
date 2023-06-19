@file:OptIn(ExperimentalStdlibApi::class)

package aoc.area.y2016

import java.io.File

val tests = listOf(
    "ADVENT" to "ADVENT",
    "A(1x5)BC" to "ABBBBBC",
    "(3x3)XYZ" to "XYZXYZXYZ",
    "A(2x2)BCD(2x2)EFG" to "ABCBCDEFEFG",
    "(6x1)(1x3)A" to "(1x3)A",
    "X(8x2)(3x3)ABCY" to "X(3x3)ABC(3x3)ABCY"
)

fun main() {
    val code = File("data/2016/day9.txt").readText()
    println(decode2(code, part2 = false))
    println(decode2(code, part2 = true))

}

fun decode2(line: String, part2: Boolean): Long {
    var ptr = 0
    val rx = Regex("""\((\d+)x(\d+)\)""")
    var count = 0L

    while(ptr < line.length) {
        when (line[ptr]) {
            ' ', '\n' -> ptr++
            '(' -> {
                val match = rx.matchAt(line, ptr) ?: throw Exception("Failed to parse instruction")
                val (sample, repeat) = match.destructured

                ptr = match.range.last + 1
                if(part2) {
                    val subexpr = line.substring(ptr, ptr + sample.toInt())
                    count += decode2(subexpr, part2) * repeat.toInt()
                } else {
                    count += sample.toInt() * repeat.toInt()
                }
                ptr += sample.toInt()
            }
            else -> {
                ptr++
                count++
            }
        }
    }

    return count
}