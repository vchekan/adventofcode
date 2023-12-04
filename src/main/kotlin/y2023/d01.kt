package y2023

import java.io.File

val test = """1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet"""

val test2 = """two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen"""

fun main() {
    println(calibrate(test))
    println(test.lines().map(String::calibrate2rx).sum())
    println(calibrate(File("data/2023/day1.txt").readText()))

    println(calibrate2(test2))
    println(calibrate2(File("data/2023/day1.2.txt").readText()))
}

fun calibrate(lines: String): Int =
    lines.lines().filter(String::isNotEmpty).sumOf { line ->
        10 * line.first(Char::isDigit).digitToInt() + line.last(Char::isDigit).digitToInt()
    }

val ds = arrayOf("one","two","three","four","five","six","seven","eight","nine")
val dss = ds + (0..9).map(Int::toString).toTypedArray()
val rxDirect = Regex(dss.joinToString("|"))
val rxReversed = Regex(dss.joinToString("|", transform = String::reversed))
val digitToIntMap = dss.withIndex().associate { it.value to if(it.value.length > 1) it.index + 1 else it.value[0].digitToInt() } +
        ds.withIndex().associate { it.value.reversed() to it.index + 1 }

fun calibrate2(lines: String): Int =
    lines.lines().filter(String::isNotEmpty).sumOf { line ->
        val d1 = digitToIntMap[rxDirect.find(line)!!.value]!!
        val d2 = digitToIntMap[rxReversed.find(line.reversed())!!.value]!!
        10 * d1 + d2
    }

val rx2 = Regex("(?=(\\d+|${ds.joinToString("|")}))")
fun String.calibrate2rx(): Int  {
    val digits = rx2.findAll(this).toList()
    val d1 = digitToIntMap[digits.first().groupValues[1]]!!
    val d2 = digitToIntMap[digits.last().groupValues[1]]!!
    return 10 * d1 + d2
}