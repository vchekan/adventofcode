package aoc.area.y2023

import aoc.area.Area
import aoc.area.Point
import aoc.area.around4
import aoc.area.get
import aoc.area.set
import java.io.File
import kotlin.math.max

val test = """...........
.S-------7.
.|F-----7|.
.||.....||.
.||.....||.
.|L-7.F-J|.
.|..|.|..|.
.L--J.L--J.
...........
"""

fun main() {
    val data = File("data/2023/day10.txt").readText()
    val map = data.parse()

    val (part1, border) = traverse(map)
    println(part1)
    println(map.display(border))

    println(map.trace(border))
}

data class Connector(val top: Boolean, val bottom: Boolean, val left: Boolean, val right: Boolean)

fun Area<Char>.asConnector(p: Point): Connector? =
    when(this[p]) {
        'S' -> Connector(true, true, true, true)
        '|' -> Connector(true, true, false, false)
        '-' -> Connector(false, false, true, true)
        'L' -> Connector(true, false, false, true)
        'J' -> Connector(true, false, true, false)
        '7' -> Connector(false, true, true, false)
        'F' -> Connector(false, true, false, true)
        else -> null
    }

fun String.parse(): Area<Char> = this.lines().filter(String::isNotEmpty)
    .map { it.toList() }

fun Area<Char>.find(c: Char): Point {
    for(l in this.indices)
        for(col in this[l].indices)
            if(this[l][col] == c)
                return Point(l, col)
    throw Exception()
}

fun traverse(map: Area<Char>): Pair<Int, Set<Point>> {
    val start = map.find('S')
    val distances: List<MutableList<Int>> = map.map { (1..it.size).map {-1}.toMutableList()}
    var step = 0
    distances.set(start, 0)
    var front = listOf(start)
    var max = Int.MIN_VALUE
    val visited = HashSet<Point>()
    visited.add(start)
    while(front.isNotEmpty()) {
        val front2 = front.flatMap { p -> map.steps(p) }.filter(visited::add)
        step++
        for(p in front2) {
            if (distances[p] == -1) {
                distances[p] = step
                max = max(max, step)
            }
        }
        front = front2
    }
    return max to visited
}

fun Area<Char>.steps(p: Point): Sequence<Point> = sequence {
    val conn = this@steps.asConnector(p)!!
    val around = this@steps.around4(p).toList()
    val (top, bottom, left, right) = around.map { this@steps.asConnector(it) }.toList()
    val (ptop, pbottom, pleft, pright) = around

    if(top != null && top.bottom && conn.top)
        yield(ptop)
    if(bottom != null && bottom.top && conn.bottom)
        yield(pbottom)
    if(left != null && left.right && conn.left)
        yield(pleft)
    if(right != null && right.left && conn.right)
        yield(pright)
}

enum class State {
    Outside,
    InBorder,
    Inside;

    fun inverse(): State = when(this) {
        Outside -> Inside
        Inside -> Outside
        InBorder -> throw Exception()
    }
}

fun Area<Char>.trace(border: Set<Point>): Int {
    var count = 0

    this.withIndex().map { (r,row) ->
        var state = State.Outside
        var entryChar = ' '
        var entryState = State.Outside
        for(c in row.indices) {
            val p = Point(r, c)
            if(!border.contains(p)) {
                if(state == State.Inside)
                    count++
                continue
            }

            val ch = this[p].let { if(it == 'S') this.resolveS(p) else it }

            when(ch) {
               '|' -> state = state.inverse()
               'J', '7' -> {
                   state = if(entryChar.isStepShape(ch))
                       entryState.inverse()
                   else
                       entryState
               }
               'L', 'F' -> {
                   entryChar = ch
                   entryState = state
                   state = State.InBorder
                }
                '-' -> { if(state != State.InBorder) throw Exception() }
                else -> throw Exception()
            }
        }
    }

    return count
}

fun Char.isStepShape(b: Char): Boolean =
    when(this) {
        'L' -> b == '7'
        'F' -> b == 'J'
        else -> throw Exception()
    }

fun Area<Char>.resolveS(p: Point): Char {
    val (top, bottom, left, right) = this.around4(p).map { this.asConnector(it) }.toList()
    return when {
        top != null && bottom != null && top.bottom && bottom.top -> '|'
        left != null && right != null && left.right && right.left -> '-'
        top != null && right != null && top.bottom && right.left -> 'L'
        right != null && bottom != null && right.left && bottom.top -> 'F'
        else -> throw NotImplementedError()
    }
}

fun Area<Char>.display(border: Set<Point>) {
    for (row in this.indices) {
        for(col in this[row].indices) {
            val p = Point(row, col)
            print(if (border.contains(p)) this[p] else ' ')
        }
        println()
    }
    println()
}