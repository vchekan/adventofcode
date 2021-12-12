package y2021.d9

import java.io.File

fun main() {
    val map = File("data/2021/d9.txt").readLines().map { it.map { it.toInt() - '0'.toInt() } }

    var part1 = 0
    for(row in map.indices) {
        for(col in map[row].indices) {
            if(map.isMin(row, col))
                part1 += 1 + map[row][col]
        }
    }

    println("Part 1: $part1")

    val basins = mutableListOf<Int>()
    for(row in map.indices) {
        for(col in map[row].indices) {
            if(map.isMin(row, col))
                basins.add(map.findBasinSize(row, col))
        }
    }
    basins.sortDescending()
    val part2 = basins.take(3).reduce { acc, i -> acc * i }
    println("Part 2: $part2")
}

fun List<List<Int>>.isMin(row: Int, col: Int): Boolean {
    val self = this[row][col]
    if(row > 0 && self >= this[row-1][col])
        return false
    if(row < this.size-1 && self >= this[row+1][col])
        return false
    if(col > 0 && self >= this[row][col-1])
        return false
    if(col < this[row].size-1 && self >= this[row][col+1])
        return false
    return true
}

fun List<List<Int>>.around(row: Int, col: Int): Sequence<Pair<Int,Int>> {
    return sequence {
        yield(Pair(row-1, col))
        yield(Pair(row+1, col))
        yield(Pair(row, col-1))
        yield(Pair(row, col+1))
    }.filter { it.first >= 0 && it.second >= 0 && it.first < this.size && it.second < this[0].size }
}


fun List<List<Int>>.findBasinSize(row: Int, col: Int): Int {
    val seen = mutableSetOf<Triple<Int,Int,Int>>()
    var newgen = listOf(Triple(row,col, this[row][col]))
    while(newgen.isNotEmpty()) {
        val newgen2 = newgen
            .flatMap { oldPoint ->
                this.around(oldPoint.first, oldPoint.second)
                    .filter { newpoint ->
                        val v = this[newpoint.first][newpoint.second]
                        v != 9 && newpoint !in seen && v > oldPoint.third
                    }.map { Triple(it.first, it.second, this[it.first][it.second]) }
            }


        seen.addAll(newgen)
        newgen = newgen2
    }

    return seen.size
}