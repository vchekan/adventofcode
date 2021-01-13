package day20

import java.io.File
import java.lang.Exception
import kotlin.math.absoluteValue

data class Tile(val id: Long, var borders: List<String>, val bordersReversed: List<String>, val bordersPlusReversed: List<String>, var lines: List<String>)

fun main() {
    val tiles = File("data/day20.txt").readText().split("\n\n")
        .filter { it.isNotEmpty() }
        .map { it.trim() }
        .map { tile ->
            val allLines = tile.lines()
            val id = allLines[0].substring(5).trimEnd(':').toLong()
            val lines = allLines.drop(1)
            val borders = bordersFromTile(lines)
            val bordersReversed = borders.map { it.reversed() }
            Tile(id, borders, bordersReversed, lines.bordersDirectAndReverse(), lines)
        }

//    val borders = tiles.flatMap { tile -> tile.borders.map { border -> Pair(border, tile) } }.groupBy({ p -> p.first}, { p -> p.second})
//
//    val topLeft = tiles.filter { tile ->
//        borders[tile.borders[0]]!!.none { it != tile}
//            && borders[tile.borders[3]]!!.none { it != tile } }
//
//    val topRight = tiles.filter { tile ->
//        borders[tile.borders[0]]!!.none { it != tile}
//                && borders[tile.borders[1]]!!.none { it != tile }}
//
//
//    val bottomLeft = tiles.find { tile ->
//        borders[tile.borders[2]]!!.none { it != tile}
//                && borders[tile.borders[3]]!!.none { it != tile } ?: false }
//
//    val bottomRight = tiles.find { tile ->
//        borders[tile.borders[2]]!!.none { it != tile}
//                && borders[tile.borders[1]]!!.none { it != tile }}
//
//    val anyCorner = tiles.filter { tile ->
//        borders[tile.borders[0]]!!.none { it != tile} && borders[tile.borders[1]]!!.none { it != tile }
//        ||
//        borders[tile.borders[1]]!!.none { it != tile} && borders[tile.borders[2]]!!.none { it != tile }
//        ||
//        borders[tile.borders[2]]!!.none { it != tile} && borders[tile.borders[3]]!!.none { it != tile }
//        ||
//        borders[tile.borders[3]]!!.none { it != tile} && borders[tile.borders[0]]!!.none { it != tile }
//    }
//
//    val connected = tiles.map { tile -> tile.borders.sumBy { border -> borders[border]!!.count() } }.sorted()
//    println(connected.joinToString(","))
//
//
//    println("${topLeft.size} ${topRight.size} ${anyCorner.size}")
//
//    // 20644873457569
////    println("part1: ${topLeft.id.toBigInteger() * topRight.id.toBigInteger() * bottomLeft.id.toBigInteger() * bottomRight.id.toBigInteger()}")
//    println("anyCorner: ${anyCorner.map { it.id }.joinToString()}")
//    println("part1: ${anyCorner.map { it.id }.reduce(Long::mul)}")

    //
    // part 2
    //
    val plan = assemble(tiles)
    val map = makeMap(plan)
    findMonster(map)
//    println(map.joinToString("\n"))
}

fun makeMap(tiles: List<List<Tile>>) : List<String> {
    val size = tiles[0][0].lines.size
    return tiles
        .map {tileRow ->
            (1  until size-1).map { cellRow -> tileRow.map {it.lines[cellRow].drop(1).take(size-2)}.joinToString("") }
        }.flatten()
}

val monster = listOf(
    "                  # ",
    "#    ##    ##    ###",
    " #  #  #  #  #  #   "
)

fun findMonster(plan: List<String>) {
    val len = plan[0].length - monster[0].length

    val rx = Regex(monster.map { it.replace(" ", "[.#]") }.joinToString(".{${len+1}}"))

    for(plan2 in listOf(plan, vFlip(plan))) {
        for(plan3 in listOf(plan2, rotateLeft(plan2), rotateRight(plan2))) {
            val str = plan3.joinToString("|")
            val monsters = //rx.findAll(str).map{it.range.start}.toList()
                            matchInterlaped(str, rx).toList()
            val monsterCount = monsters.count()
            if (monsterCount > 0) {
                val planHashes = str.count { it == '#' }
                val monsterHashes = monster.map { it.count { it == '#' } }.sum()
                val part2 = planHashes - monsterHashes * monsterCount

                println(paintMonsters(plan3, monsters))

                println("total hashes: $planHashes; monsters: $monsterCount; monster hashes: $monsterHashes; part2: $part2")
                return
            }
        }
    }
}

fun matchInterlaped(str: String, rx: Regex) : Sequence<Int> {
    var ptr = 0
    return sequence {
        while(true) {
            val match = rx.find(str, ptr) ?: return@sequence
            yield(match.range.first)
            ptr = match.range.first + 1
        }
    }
}

fun paintMonsters(plan: List<String>, matches: List<Int>): String {
    val monsterPoints = monster.withIndex()
        .map { r -> r.value.withIndex().filter { c -> c.value == '#' }.map { c -> Pair(r.index, c.index) } }
        .flatten()
    val plan2 = plan.toMutableList()
    for(match in matches) {
        val offset = match//.range.first
        val row = offset / (plan[0].length + 1)
        val col = offset % (plan[0].length + 1)
        for(mp in monsterPoints) {
            val ca = plan2[row + mp.first].toCharArray()
            if(col + mp.second >= ca.size )
                continue
            ca[col + mp.second] = 'O'
            plan2[row + mp.first] = ca.joinToString("")
        }
    }
    return plan2.joinToString("\n").replace("O", "\u001B[102m#\u001B[0m")
}

fun assemble(tiles: List<Tile>): List<List<Tile>> {
    var borders = directAndReverseBorders(tiles)
    val size = Math.sqrt(tiles.size.toDouble()).toInt()

    val visited = mutableSetOf<Long>()
    val plan = (1..size).map { mutableListOf<Tile>() }.toList()
    var left: String? = null
    var top: String? = null
    for(row in 0 until size) {
        for(col in 0 until size) {
            if(row > 0)
                top = plan[row-1][col].borders[2].reversed()

            val tile = findTile(left, top, tiles, borders, visited)
                .fit(left, top, borders)
            plan[row].add(tile)
            visited.add(tile.id)

//            println("Tile $row $col ${tile.id}")
//            println(tile.lines.joinToString("\n"))
//            println()

            left = if(col == size-1)
                null
            else
                tile.borders[1].reversed() // my right border is somebody's reverse left border
        }
    }

    return plan
}

fun findTile(left: String?, top: String?, tiles: List<Tile>, borders: Map<String,List<Tile>>, visited: Set<Long>): Tile {
    // top-left corner
    if(left == null && top == null)
        return corners(tiles, borders).first()
    // top line
    if(top == null)
        return borders[left]!!.single { t -> !visited.contains(t.id) }
    // start of a new line
    if(left == null)
        return borders[top]!!.single { t -> !visited.contains(t.id) }
    // inner tile
    return borders[left]!!.single { t -> !visited.contains(t) && t.bordersPlusReversed.contains(top) }
}

fun Tile.fit(left: String?, top: String?, borders: Map<String, List<Tile>>) : Tile {
    var outsides = this.borders.withIndex().filter { borders[it.value]!!.none { it.id != id } }
    val isFlipped: Boolean
    var positionDelta: Int

    // Top-left cell
    if(left == null && top == null) {
        assert(outsides.size == 2 )

        when(outsides[0].index) {
            0 -> return rotateLeft().fixBorders()
            1 -> return this
            2 -> return hFlip().fixBorders()
            3 -> return this
            else -> throw Exception()
        }
        assert((outsides[0].index - outsides[1].index).absoluteValue == 1)
    }
    // 1st row
    else if(top == null) {
        // for right-top corner there will be 2 free corners
        assert(outsides.size in 1..2)
        positionDelta = outsides[0].index
        isFlipped = this.bordersReversed.contains(left)
        assert(isFlipped || this.borders.contains(left))
    }
    // inner cell
    else {
        assert(outsides.size <= 2)
        positionDelta = this.borders.indexOf(top)
        if(positionDelta == -1) {
            positionDelta = this.bordersReversed.indexOf(top)
            isFlipped = true
        } else
            isFlipped = false
        assert(positionDelta != -1)
    }

    return when(Pair(positionDelta, isFlipped)) {
        0 to false -> this
        0 to true -> vFlip()
        1 to false -> this.rotateLeft()
        1 to true -> hFlip().rotateLeft()
        2 to false -> vFlip().hFlip()
        2 to true -> hFlip()
        3 to false -> this.rotateRight()
        3 to true -> hFlip().rotateRight()
        else ->
            throw Exception("Unimplemented move for delta: $positionDelta flipped: $isFlipped")
    }.fixBorders()
}

fun vFlip(lines: List<String>) = lines.map{ it.reversed()}
fun Tile.vFlip(): Tile = this.copy(lines = vFlip(lines))
fun hFlip(lines: List<String>) = lines.reversed()
fun Tile.hFlip(): Tile = this.copy(lines = hFlip(lines))
fun Tile.rotateRight(): Tile = this.copy(lines = rotateRight(lines))
fun Tile.fixBorders(): Tile = this.copy(borders = bordersFromTile(this.lines))
fun Tile.rotateLeft(): Tile = this.copy(lines = rotateLeft(lines))

fun rotateLeft(lines: List<String>) =
    lines.indices.map { i ->
        val col = lines.size - 1 - i
        lines.map { it[col] }.joinToString("")
    }


fun rotateRight(lines: List<String>) =
    lines.indices.map { i ->
        lines.reversed().map { it[i] }.joinToString("")
    }


fun directAndReverseBorders(tiles: List<Tile>): Map<String,List<Tile>> =
    tiles.map {tile -> tile.bordersPlusReversed.map { Pair(tile, it) } }
        .flatten()
        .groupBy({ it.second }, {it.first})


fun corners(tiles: List<Tile>, borders: Map<String,List<Tile>>): List<Tile> =
    tiles.filter { t ->
        borders[t.bordersPlusReversed[0]]!!.count { it.id != t.id } +
            borders[t.bordersPlusReversed[1]]!!.count { it.id != t.id } +
            borders[t.bordersPlusReversed[2]]!!.count { it.id != t.id } +
            borders[t.bordersPlusReversed[3]]!!.count { it.id != t.id } == 2
    }


/**
 * --0--
 * |    |
 * 3    1
 * |    |
 * --2--
 */
fun List<String>.bordersDirectAndReverse() : List<String> {
    val borders = listOf(
        this[0],
        this.map { it.last() }.joinToString(""),
        this.last().reversed(),
        this.map { it[0] }.joinToString("").reversed(),
    )

    val reversed = listOf(
        this[0].reversed(),
        this.map { it.last() }.joinToString("").reversed(),
        this.last(),
        this.map { it[0] }.joinToString(""),
    )

    return borders + reversed
}

fun bordersFromTile(lines: List<String>) : List<String> =
    listOf(
        lines[0],
        lines.map { it.last() }.joinToString(""),
        lines.last().reversed(),
        lines.map { it[0] }.joinToString("").reversed(),
    )


