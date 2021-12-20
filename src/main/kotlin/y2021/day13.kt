package aoc.area.y2021.d13

import aoc.area.*

import java.io.File

fun main() {
    val(p1, p2) = File("data/2021/d13.txt").readText().split("\n\n")
    val dots = p1.lines().map { val(x, y) = it.split(","); PointXY(x.toInt(), y.toInt()) }.toSet()
    val folds = p2.lines().filter { it.isNotEmpty() }.map { val(axis, value) = it.split("=")
        Pair(axis.last(), value.toInt())
    }


    val part1 = folds.take(1).fold(dots) { acc, op ->
        when(op.first) {
            'y' -> acc.foldY(op.second)
            'x' -> acc.foldX(op.second)
            else -> throw Exception()
        }
    }

    println(part1.size)

    val part2 = folds.fold(dots) { acc, op ->
        when(op.first) {
            'y' -> acc.foldY(op.second)
            'x' -> acc.foldX(op.second)
            else -> throw Exception()
        }
    }

    val screen = Array(7) { CharArray(5*8) { '.' } }
    for(p in part2)
        screen[p.y][p.x] = '#'
    for(line in screen)
        println(line)
}

fun Set<PointXY>.foldY(y: Int): Set<PointXY> {
    return mapNotNull {
        when {
            it.y < y -> it
            it.y > y -> PointXY(it.x, y*2 - it.y)
            else -> null
        }
    }.toSet()
}

fun Set<PointXY>.foldX(x: Int): Set<PointXY> {
    return mapNotNull {
        when {
            it.x < x -> it
            it.x > x -> PointXY(2*x-it.x, it.y)
            else -> null
        }
    }.toSet()
}