package aoc.area.y2021.d15
import aoc.area.*

import java.io.File
import java.util.*
import kotlin.Comparator
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
fun main() {
    val map = File("data/2021/d15.txt").readLines().map { it.map { it - '0' } }
    val part1 = findPath(map, Point(0,0), Point(map.size-1, map.last().size-1))
    println(part1)
    assert(part1 == 386)

    val(map2, buildMapTime) = measureTimedValue {
        buildMap2(map)
    }
    val (part2, part2Time) = measureTimedValue { dijkstras(map2, Point(0,0), Point(map2.size-1, map2.last().size-1)) }
    println(part2)
//    assert(part2 == 2806)
    println("Timing:")
    println("  build map: $buildMapTime")
    println("  part2: $part2Time")
}

fun findPath(map: Area, start: Point, target: Point): Int? {
    var newPoints: Map<Point,Int> = mapOf(start to 0)
    val seen = mutableMapOf<Point,Int>(start to 0)

    while(newPoints.isNotEmpty()) {
        newPoints = newPoints.flatMap { (point, danger) ->
            map.around4(point).map { newPoint ->
                newPoint to danger + map[newPoint]
            }.filter { newPoint ->
                val existing = seen[newPoint.first]
                if(existing == null || existing > newPoint.second) {
                    seen[newPoint.first] = newPoint.second
                    true
                } else
                    false
            }
        }.toMap()
    }

    return seen[target]
}

fun findPath2(map: Area, start: Point, target: Point): Int? {
    // Replace `seen` from map to array gives 2s -> 880ms improvement.
    // Use IntArray for rows: 790ms
    // Using flat IntArray : back to 800+ms ???
    // combined map and filter for new points + remove danger from `newPoints` (convert map into set): 760ms
    var newPoints: List<Point> = listOf(start)
    val seen = Array(map.size) {IntArray(map[0].size) {-1}}
    val cols = map[0].size

    // Naive pre-population
//    for(r in 0 until map.size-1)
//        seen[r+1][0] = seen[r][0] + map[r+1][0]
//    for(c in 0 until map[0].size-1)
//        seen[0][c+1] = seen[0][c] + map[0][c+1]
//    for(r in 1 until map.size)
//        for(c in 1 until cols)
//            seen[r][c] = min(seen[r-1][c], seen[r][c-1])
//    var newPoints = mutableListOf<Pair<Point,Int>>()

    while(newPoints.isNotEmpty()) {
        newPoints = newPoints.flatMap { point ->
            val danger = seen[point.row][point.col]
            map.around4(point).mapNotNull { neighbour ->
                neighbour to danger + map[neighbour]
                val newPoint = neighbour
                val newDanger = danger + map[neighbour]
                val existing = seen[newPoint.row][newPoint.col]
                if(existing == -1 || existing > newDanger) {
                    seen[newPoint.row][newPoint.col] = newDanger
                    newPoint
                } else
                    null
            }
        }.distinct()
    }

    return seen[target.row][target.col]
}

fun dijkstras(map: Area, start: Point, target: Point): Int {
    // 520ms
    val weights = Array(map.size) {IntArray(map[0].size) {Int.MAX_VALUE} }
    val cmp = Comparator<Point> {p1, p2 -> weights[p1.row][p1.col] - weights[p2.row][p2.col] }
    val remaining = PriorityQueue<Point>(map.size*2, cmp)
    val visited = mutableSetOf<Point>()
    
    weights[start.row][start.col] = 0
    remaining.add(start)

    while (visited.size < map.size*map[0].size) {
        val node = remaining.poll()!!
        if(!visited.add(node))
            continue
        map.around4(node).filterNot(visited::contains).forEach { neighbour ->
            val weight2 = weights[node.row][node.col] + map[neighbour]
            if(weights[neighbour.row][neighbour.col] > weight2)
                weights[neighbour.row][neighbour.col] = weight2
            remaining.add(neighbour)
        }
    }

    return weights[target.row][target.col]
}


fun buildMap2(map: Area): Area {
    val map2 = Array(map.size*5) { Array(map[0].size*5) {-1} }
    for(row in 0 until map.size*5) {
        for(col in 0 until map[0].size*5) {
            val value = (map[row % map.size][col % map[0].size] +
                    row / map.size + col / map[0].size - 1) % 9 + 1
            map2[row][col] = value
        }
    }
    return map2.toList().map { it.toList() }
}