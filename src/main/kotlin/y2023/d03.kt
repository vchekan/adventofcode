package y2023.d3

import y2023.ds
import java.io.File

val test = """467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...${'$'}.*....
.664.598..""".lines()

data class Digit(val pos: Set<Pair<Int,Int>>, val v: Int)

fun main() {
    val adjanced = adjanced(test)
    println(partNums(test, adjanced).sum())

    val map = File("data/2023/day3.txt").readLines().filter(String::isNotEmpty)
    val part1 = partNums(map, adjanced(map)).sum()
    println(part1)

    val part2Test = gears(test).sum()
    println(part2Test)

    val part2 = gears(map).sum()
    println(part2)
}

fun symbols(map: List<String>): Sequence<Pair<Int,Int>> {
    return sequence {
        for (row in map.indices) {
            for (col in 0 until map[row].length) {
                val ch = map[row][col]
                if (ch.isSymbol())
                    yield(row to col)
            }
        }
    }
}

fun adjanced(map: List<String>): Set<Pair<Int,Int>> {
    return sequence {
        for (symbol in symbols(map)) {
            for (dr in listOf(-1, 0, 1)) {
                for (dc in listOf(-1, 0, 1)) {
                    if (dr == 0 && dc == 0)
                        continue
                    val p = Pair(symbol.first + dr, symbol.second + dc)
                    if(p.first >= 0 && p.second >= 0 && p.first < map.size && p.second < map[0].length)
                        yield(p)
                }
            }
        }
    }.toSet()
}

val rx = Regex("\\d+")
fun partNums(map: List<String>, adjanced: Set<Pair<Int,Int>>): Sequence<Int> {
    return sequence {
        for (row in map.indices) {
            for (d in rx.findAll(map[row])) {
                if (d.range.any { col -> adjanced.contains(row to col) })
                    yield(d.value.toInt())
            }
        }
    }
}

fun gears(map: List<String>): Sequence<Int> = sequence {
    val digitsCells = mutableListOf<Digit>()
    for (row in map.indices) {
        for (d in rx.findAll(map[row])) {
            val digitPositions = Digit(d.range.map { col -> row to col}.toSet(), d.value.toInt())
            digitsCells.add(digitPositions)
        }
    }

    for(row in map.indices) {
        for(col in map[row].indices) {
            val ch = map[row][col]
            if(ch != '*')
                continue

            val around = (row to col).around(map).toList()
            val digits = digitsCells.filter { digitSpan ->
                around.any { it in digitSpan.pos }
            }

            if(digits.size == 2)
                yield(digits[0].v * digits[1].v)
        }
    }
}

fun Char.isSymbol() = this != '.' && !this.isDigit()

fun Pair<Int,Int>.around(map: List<String>): Sequence<Pair<Int,Int>> = sequence {
    for (dr in listOf(-1, 0, 1)) {
        for (dc in listOf(-1, 0, 1)) {
            if (dr == 0 && dc == 0)
                continue
            val p = Pair(this@around.first + dr, this@around.second + dc)
            if(p.first >= 0 && p.second >= 0 && p.first < map.size && p.second < map[0].length)
                yield(p)
        }
    }
}