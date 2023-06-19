package aoc.area.y2016

import java.io.File
import kotlin.math.abs

enum class Direction {
    Up, Right, Down, Left;

    fun turn(turn: Char): Direction = when(turn) {
        'L' -> Direction.values()[(this.ordinal + 3) % 4]
        'R' -> Direction.values()[(this.ordinal + 1) % 4]
        else -> throw Exception("Unknown turn: $turn")
    }
}

data class Point(val x: Int, val y: Int) {
    fun step(steps: Int, dir: Direction): Point =
        when(dir) {
            Direction.Up -> this.copy(y =  y + steps)
            Direction.Right -> this.copy(x = x + steps)
            Direction.Down -> this.copy(y = y - steps)
            Direction.Left -> this.copy(x = x - steps)
        }

    fun trace(steps: Int, dir: Direction): Sequence<Point> = sequence {
        for(i in 1 until steps) {
            yield(when (dir) {
                Direction.Up -> this@Point.copy(y = y + i)
                Direction.Right -> this@Point.copy(x = x + i)
                Direction.Down -> this@Point.copy(y = y - i)
                Direction.Left -> this@Point.copy(x = x - i)
            })
        }
    }

    fun distance() = abs(x) + abs(y)
}

fun main() {
    val input = File("data/2016/day1.txt").readText().trim()
        //"R8, R4, R4, R8"
        .split(", ").map { s ->
        val turn = s[0]
        val steps = s.substring(1).toInt()
        turn to steps
    }

    var dir = Direction.Up
    var point = Point(0, 0)
    val visited = mutableSetOf(point)
    var firstVisitedTwice: Point? = null
    for((turn, steps) in input) {
        dir = dir.turn(turn)
        if(firstVisitedTwice == null) {
            for(trace in point.trace(steps, dir))
                if(!visited.add(trace)) {
                    firstVisitedTwice = trace
                    break
                }
        }
        point = point.step(steps, dir)
    }

    println(point.distance())
    println(firstVisitedTwice?.distance())
}