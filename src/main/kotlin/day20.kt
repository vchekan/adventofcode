package day20

import day18.mul
import java.io.File

data class Tile(val id: Long, val borders: List<String>, val lines: List<String>)

fun main() {
    val tiles = File("data/day20.txt").readText().split("\n\n")
        .filter { it.isNotEmpty() }
        .map { tile ->
            val allLines = tile.lines()
            val id = allLines[0].substring(5).trimEnd(':').toLong()
            val lines = allLines.drop(1)
            Tile(id, lines.borders(), lines)
        }

    val borders = tiles.flatMap { tile -> tile.borders.map { border -> Pair(border, tile) } }.groupBy({ p -> p.first}, { p -> p.second})

    val topLeft = tiles.filter { tile ->
        borders[tile.borders[0]]!!.none { it != tile}
            && borders[tile.borders[3]]!!.none { it != tile } }

    val topRight = tiles.filter { tile ->
        borders[tile.borders[0]]!!.none { it != tile}
                && borders[tile.borders[1]]!!.none { it != tile }}


    val bottomLeft = tiles.find { tile ->
        borders[tile.borders[2]]!!.none { it != tile}
                && borders[tile.borders[3]]!!.none { it != tile } ?: false }

    val bottomRight = tiles.find { tile ->
        borders[tile.borders[2]]!!.none { it != tile}
                && borders[tile.borders[1]]!!.none { it != tile }}

    val anyCorner = tiles.filter { tile ->
        borders[tile.borders[0]]!!.none { it != tile} && borders[tile.borders[1]]!!.none { it != tile }
        ||
        borders[tile.borders[1]]!!.none { it != tile} && borders[tile.borders[2]]!!.none { it != tile }
        ||
        borders[tile.borders[2]]!!.none { it != tile} && borders[tile.borders[3]]!!.none { it != tile }
        ||
        borders[tile.borders[3]]!!.none { it != tile} && borders[tile.borders[0]]!!.none { it != tile }
    }

    val connected = tiles.map { tile -> tile.borders.sumBy { border -> borders[border]!!.count() } }.sorted()
    println(connected.joinToString(","))


    println("${topLeft.size} ${topRight.size} ${anyCorner.size}")

    // 20644873457569
//    println("part1: ${topLeft.id.toBigInteger() * topRight.id.toBigInteger() * bottomLeft.id.toBigInteger() * bottomRight.id.toBigInteger()}")
    println("anyCorner: ${anyCorner.map { it.id }.joinToString()}")
    println("part1: ${anyCorner.map { it.id }.reduce(Long::mul)}")
}

/**
 * --0--
 * |    |
 * 3    1
 * |    |
 * --2--
 */
fun List<String>.borders() : List<String> {
    val borders = listOf(
        this[0],
        this.map { it.last() }.joinToString(""),
        this.last(),
        this.map { it[0] }.joinToString(""),
    )

//    val vFlipped = listOf(
//        this[0].reversed(),
//        this.map { it.last() }.joinToString(""),
//        this.last().reversed(),
//        this.map { it[0] }.joinToString(""),
//    )
//
//    val hFlipped = listOf(
//        this[0],
//        this.map { it.last() }.joinToString("").reversed(),
//        this.last(),
//        this.map { it[0] }.joinToString("").reversed(),
//    )

    val reversed = listOf(
        this[0].reversed(),
        this.map { it.last() }.joinToString("").reversed(),
        this.last().reversed(),
        this.map { it[0] }.joinToString("").reversed(),
    )

    return borders + reversed
}

