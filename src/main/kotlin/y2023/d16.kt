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

enum class Dir {
    Up,
    Right,
    Down,
    Left
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
        Dir.Up -> this.copy(row = this.row - 1)
        Dir.Right -> this.copy(col = this.col + 1)
        Dir.Down -> this.copy(row = this.row + 1)
        Dir.Left -> this.copy(col = this.col - 1)
    } 

fun List<String>.move(p: Point, dir: Dir): List<Pair<Point, Dir>> =
    when(this[p.row][p.col]) {
        '/' -> when(dir) {
            Dir.Up -> Dir.Right
            Dir.Right -> Dir.Up
            Dir.Down -> Dir.Left
            Dir.Left -> Dir.Down
        }.let { listOf(it) }
        '\\' -> when(dir) {
            Dir.Up -> Dir.Left
            Dir.Right -> Dir.Down
            Dir.Down -> Dir.Right
            Dir.Left -> Dir.Up
        }.let { listOf(it) }
        '|' -> when(dir) {
            Dir.Up, Dir.Down -> listOf(dir)
            Dir.Right, Dir.Left -> listOf(Dir.Up, Dir.Down)
        }
        '-' -> when(dir) {
            Dir.Up, Dir.Down -> listOf( Dir.Left, Dir.Right)
            Dir.Right, Dir.Left -> listOf(dir)
        }
        '.' -> listOf(dir)
        else -> throw Exception()
    }
        .map { dir -> p.move(dir) to dir }
        .filter { p -> p.first.row in this.indices && p.first.col in this[0].indices }
