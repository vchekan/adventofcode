package y2021.d20

import y2021.d3.toInt
import java.io.File

val steps = 50
fun main() {
    val lines = File("data/2021/d20.txt").readText()
    val blocks = lines.split("\n\n").map { it.trim() }.filter { it.isNotEmpty() }
    val encoder = blocks[0].lines().reduce(String::plus).map { it == '#' }.toBooleanArray()
    // keep image oversized by 4 so it is simpler to apply encoding later
    val initImage: List<BooleanArray> =  blocks[1].lines()
        .let { map ->
            val paddingLines = (1..2+steps+10).map { ".".repeat(map[0].length) }
            paddingLines + map + paddingLines
        }
        .map {
            val padding = ".".repeat(2+steps+10)
            "$padding$it$padding".map { it == '#' }.toBooleanArray()
        }
//        .map { (listOf(false, false) + it.map { it == '#' } + listOf(false, false)).toBooleanArray() }
//        .let {
//            listOf(BooleanArray(it.size+4), BooleanArray(it.size+4)) +
//            it +
//            listOf(BooleanArray(it.size+4), BooleanArray(it.size+4))
//        }

    var image = initImage
    repeat(steps) { image = image.enhanceImage(encoder, it) }
    val part1 = image.subList(2, image.size-3).sumOf { line ->
        (2..line.size-3).count{line[it]}
    }
    println(part1)
}

fun List<BooleanArray>.enhanceImage(encoder: BooleanArray, step: Int): List<BooleanArray> {
    val res = List(this.size) { BooleanArray(this[it].size) }

    for(row in 1 until this.size - 1) {
        for(col in 1 until this[row].size - 1) {
            val index =
                (this[row-1][col-1].toInt() shl 8) or
                (this[row-1][col].toInt() shl 7) or
                (this[row-1][col+1].toInt() shl 6) or
                (this[row][col-1].toInt() shl 5) or
                (this[row][col].toInt() shl 4) or
                (this[row][col+1].toInt() shl 3) or
                (this[row+1][col-1].toInt() shl 2) or
                (this[row+1][col].toInt() shl 1) or
                this[row+1][col+1].toInt()
            val light = encoder[index]
            res[row][col] = light
        }
    }

    val filling = step % 2 == 0
    for(i in res.indices) {
        res[0][i] = filling
        res[res.size-1][i] = filling
    }
    for(i in 1 until res.size-1) {
        res[i][0] = filling
        res[i][res.size-1] = filling
    }
    println(res.display())
    println()

    return res
}

fun List<BooleanArray>.display(): String = this.map {line ->
    line.map {ch -> if(ch) '#' else '.' }.joinToString("")
}.joinToString("\n")
