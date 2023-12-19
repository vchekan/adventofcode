package aoc.area.y2023.d16

import aoc.area.Point
import java.io.File

val test =""".|...\....
|.-.\.....
.....|-...
........|.
..........
.........\
..../.\\..
.-.-/..|..
.|....-|.\
..//.|...."""

/*
    '/'
    0 -> 1      00 -> 01
    1 -> 0      01 -> 00
    2 -> 3      10 -> 11
    3 -> 2      11 -> 10

    '\'
    0 -> 3      00 -> 11
    1 -> 2      01 -> 10
    2 -> 1      10 -> 01
    3 -> 0      11 -> 00

    '-'
    0 -> 1,3    00 -> 01, 11
    1 -> 1      01 -> 01
    2 -> 1,3    10 -> 01, 11
    3 -> 3      11 -> 11

    '|'
    0 -> 0      00
    1 -> 0,2    01
    2 -> 2      10
    3 -> 0,2    11
 */
enum class Dir {
    Up,     // 0
    Right,  // 1
    Down,   // 2
    Left;   // 3

    fun next(m: Char): List<Dir> = when {
        m == '/' -> listOf(entries[ordinal xor 1])
        m == '\\' -> listOf(entries[ordinal xor 0b11])
        m == '-' && (ordinal.and(1) == 0) -> listOf(Left, Right)
        m == '|' && (ordinal.and(1) != 0) -> listOf(Up, Down)
        else -> listOf(this)
    }
}

fun main() {
    val data = File("data/2023/day16.txt").readText()
    val map = data.parse()
    val start = Point(0, 0) to Dir.Right
    println(map.run(start))

    val allStarts = map.indices.map { row -> Point(row, 0) to Dir.Right} +
            map.indices.map { row -> Point(row, map[0].length-1) to Dir.Left } +
            map[0].indices.map { col -> Point(0, col) to Dir.Down} +
            map[0].indices.map { col -> Point(map.size - 1, col) to Dir.Up }

    val part2 = allStarts.maxOf { map.run(it) }
    println(part2)
}

fun List<String>.run(start: Pair<Point,Dir>): Int {
    val energized = mutableSetOf(start)

    var beams = listOf(start)
    while(beams.isNotEmpty()) {
       beams = beams.flatMap { this.move(it.first, it.second) }
           .filter { energized.add(it) }
    }

    return energized.distinctBy { it.first }.count()
}

fun String.parse(): List<String> = this.lines()
    .filter(String::isNotEmpty)
fun Point.move(d: Dir): Point =
    when(d) {
        Dir.Up -> copy(row = row - 1)
        Dir.Right -> copy(col = col + 1)
        Dir.Down -> copy(row = row + 1)
        Dir.Left -> copy(col = col - 1)
    } 

fun List<String>.move(p: Point, dir: Dir): List<Pair<Point, Dir>> =
    dir.next(this[p.row][p.col])
        .map { newDir -> p.move(newDir) to newDir }
        .filter { newPoint -> newPoint.first.row in this.indices && newPoint.first.col in this[0].indices }
