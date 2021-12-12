package y2021.d10

import java.io.File
import java.util.*

fun main() {
    val lines = File("data/2021/d10.txt").readLines()
    val part1 = lines.sumBy { line ->
        when(line.findCorrupt().first) {
            ')' -> 3
            ']' -> 57
            '}' -> 1197
            '>' -> 25137
            else -> 0
        }
    }
    println("Part 1: $part1")

    val good = lines
        .map { it.findCorrupt()}
        .filter { it.first == null }
        .map {
            it.second.score2()
        }.sorted()
    val part2 = good[good.size/2]
    println(part2)
}

fun String.findCorrupt(): Pair<Char?, List<Char>> {
    val s = Stack<Char>()
    for(ch in this) {
        when(ch) {
            '(','[','{','<' -> s.push(ch)
            else -> {
                if(!ch.match(s.pop()))
                    return Pair(ch, emptyList())
            }
        }
    }
    return Pair(null, s.toList().reversed())
}

val match= mapOf('(' to ')', '{' to '}', '<' to '>', '[' to ']')
fun Char.match(c: Char): Boolean = match[c] == this

fun List<Char>.score2(): Long = this.fold(0L) {acc, c -> acc*5 + when(c) {
    '(' -> 1
    '[' -> 2
    '{' -> 3
    '<' -> 4
    else -> throw Exception("unexpected char '$c'")
} }