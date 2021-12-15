package y2021.d11

import aoc.area.*
import java.io.File

fun main() {
    val initMap = File("data/2021/d11.txt").readLines().map { it.map{ it-'0' } }
    var map1 = initMap.map { it.toMutableList() }
    val steps = 100
    var part1 = 0

    repeat(steps) {
        part1 += tick(map1)
    }
    println(part1)
    assert(part1 == 1705)

    val map2 = initMap.map { it.toMutableList() }
    var part2 = 1
    val squids = map2.size * map2[0].size
    while(tick(map2) != squids) {
        part2++
    }
    println(part2)
    assert(part2 == 265)
}

fun tick(map: List<MutableList<Int>>): Int {
    var flashCount = 0
    // initial increment
    var flashes = map.points().filter { ++map[it] == 10 }.toList()
    // Loop over new flashes until no new flash
    while(flashes.isNotEmpty()) {
        flashCount += flashes.size
        // new flashes are neighbours which exceeded limit after 1 increment
        flashes = flashes.flatMap { point ->
            map.around9(point).filter { neighbour -> ++map[neighbour] == 10 }
        }
    }

    // reset flashed to 0
    map.points().filter { map[it] > 9 }.forEach { map[it] = 0 }

    return flashCount
}