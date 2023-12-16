package aoc.area.y2023.d14

import java.io.File
import java.security.MessageDigest

val test = """O....#....
O.OO#....#
.....##...
OO.#O....O
.O.....O#.
O.#..O.#.#
..O..#O..O
.......O..
#....###..
#OO..#....
"""

fun main() {
    val count = 1_000_000_000
    val data = File("data/2023/day14.txt").readText()
    val map = data.parse()
    val states = HashMap<String,Int>()

    var count1 = 0
    var count2 = 0
    repeat(count) {
        map.cycle()
        val id = map.id()
        if(states.contains(id)) {
            val period = it - states[id]!!
            val remainder = (count - (it + 1)) % period
            repeat(remainder) { map.cycle() }

            println(map.load())
            return

        }
        states[id] = it
    }
}

fun List<CharArray>.cycle() {
    this.north()
    this.west()
    this.south()
    this.east()
}

fun List<CharArray>.north() {
    val availableMap = IntArray(this[0].size)

    for((l, line) in this.withIndex()) {
        for(c in line.indices) {
            when(line[c]) {
                '#' -> availableMap[c] = l + 1
                'O' -> {
                    line[c] = '.'
                    this[availableMap[c]][c] = 'O'
                    availableMap[c] = availableMap[c] + 1
                }
            }
        }
    }
}

fun List<CharArray>.west() {
    for(line in this) {
        var p1 = 0
        for((col, ch) in line.withIndex()) {
            when(ch) {
                '#' -> p1 = col + 1
                'O' -> {
                    line[col] = '.'
                    line[p1++] = 'O'
                }
            }
        }
    }
}

fun List<CharArray>.east() {
    for(line in this) {
        var p1 = line.size - 1
        for((col, ch) in line.withIndex().reversed()) {
            when(ch) {
                '#' -> p1 = col - 1
                'O' -> {
                    line[col] = '.'
                    line[p1--] = 'O'
                }
            }
        }
    }
}

fun List<CharArray>.south() {
    val availableMap = IntArray(this[0].size) { this.size - 1 }

    for((l, line) in this.withIndex().reversed()) {
        for(c in line.indices) {
            when(line[c]) {
                '#' -> availableMap[c] = l - 1
                'O' -> {
                    line[c] = '.'
                    this[availableMap[c]][c] = 'O'
                    availableMap[c] = availableMap[c] - 1
                }
            }
        }
    }
}


fun String.parse(): List<CharArray> =
        this.lines().filter(String::isNotEmpty).map { it.toCharArray() }

fun List<CharArray>.display() {
    for(line in this)
        println(line.concatToString())
    println()
}

fun List<CharArray>.load() =
    this.reversed().withIndex().sumOf { (it.index + 1) * it.value.count { it == 'O' } }

@OptIn(ExperimentalStdlibApi::class)
fun List<CharArray>.id(): String {
    val md5 = MessageDigest.getInstance("MD5")
    for(line in this)
        for(ch in line)
            md5.update(ch.code.toByte())
    return md5.digest().toHexString()
}