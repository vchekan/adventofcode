package y2021.d24

import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * ALU simulator. Is not used to find actual solution, but to research structure of algorithm.
 */
class Alu(breakpoints: List<Pair<String,Int>> = emptyList()) {
    var w = 0L
    var x = 0L
    var y = 0L
    var z = 0L
    var inputPtr = 0
    fun execute(program: List<List<String>>, input: String) {
        program.forEachIndexed { ip, op ->
            val res = when(op[0]) {
                "inp" -> (input[inputPtr++] - '0').toLong()
                else -> {
                    val v1 = load(op[1])
                    val v2 = if(op[2][0].isLetter()) load(op[2]) else op[2].toLong()
                    when(op[0]) {
                        "add" -> v1 + v2
                        "mul" -> v1 * v2
                        "div" -> v1 / v2
                        "mod" -> v1 % v2
                        "eql" -> if(v1 == v2) 1 else 0
                        else -> throw Exception()
                    }
                }
            }
            store(op[1], res)
        }
    }
    private fun store(reg: String, n: Long) {
        when(reg) {
            "w" -> w = n
            "x" -> x = n
            "y" -> y = n
            "z" -> z = n
            else -> throw Exception()
        }
    }
    private fun load(reg: String): Long = when(reg) {
        "w" -> w
        "x" -> x
        "y" -> y
        "z" -> z
        else -> throw Exception()
    }

    override fun toString(): String = "w: $w x: $x y: $y z: $z"
    companion object {
        fun compile(lines: List<String>): List<List<String>> = lines.map { it.split(' ') }
    }
}

@OptIn(ExperimentalTime::class)
fun main() {
    val program = Alu.compile(File("data/2021/d24.txt").readLines())
//    val input = "99997996989156".map(Char::digitToInt).toIntArray()

    // part1: 79997391969649
    //7[0] z: 15 x: 1 accumulator y: 15, remainder: 0, remainder+k2: 14
    //9[1] z: 407 x: 1 accumulator y: 17, remainder: 15, remainder+k2: 28
    //9[2] z: 10594 x: 1 accumulator y: 12, remainder: 17, remainder+k2: 30
    //9[3] z: 275463 x: 1 accumulator y: 19, remainder: 12, remainder+k2: 24
    //7[4] z: 10594 x: 0 accumulator y: 0, remainder: 19, remainder+k2: 7
    //3[5] z: 275455 x: 1 accumulator y: 11, remainder: 12, remainder+k2: 24
    //9[6] z: 10594 x: 0 accumulator y: 0, remainder: 11, remainder+k2: 9
    //1[7] z: 407 x: 0 accumulator y: 0, remainder: 12, remainder+k2: 1
    //9[8] z: 10600 x: 1 accumulator y: 18, remainder: 17, remainder+k2: 30
    //6[9] z: 275609 x: 1 accumulator y: 9, remainder: 18, remainder+k2: 32
    //9[10] z: 10600 x: 0 accumulator y: 0, remainder: 9, remainder+k2: 9
    //6[11] z: 407 x: 0 accumulator y: 0, remainder: 18, remainder+k2: 6
    //4[12] z: 15 x: 0 accumulator y: 0, remainder: 17, remainder+k2: 4
    //9[13] z: 0 x: 0 accumulator y: 0, remainder: 15, remainder+k2: 9

    val t = measureTime { solve() }
    println("Solved in $t")
}

fun solve() {
    var input = "99999999999999".map(Char::digitToInt).toIntArray()
    val res = mutableListOf<String>()
    while(true) {
        val res1 = simulate(input)
        if(res1 == 0L) {
            res.add(input.joinToString(""))
        }
        val pos = if(res1 < 0) -res1.toInt() else input.size-1
        if (!input.dec(pos))
            break
    }
    println("Part 1: ${res.first()}")
    println("Part 2: ${res.last()}")
    assert(res.first() == "79997391969649")
    assert(res.last() == "16931171414113")
}

/** Decrement BCD array */
fun IntArray.dec(pos: Int = this.size - 1): Boolean {
    var i = pos
    while (this[i] == 1) {
        if(i-- == 0)
            return false
    }
    this[i]--
    while(++i < this.size)
        this[i] = 9
    return true
}

//
val k1 = listOf(1,1,1,1,26,1,26,26,1,1,26,26,26,26).toIntArray()
val k2 = listOf(14,13,13,12,-12,12,-2,-11,13,14,0,-12,-13,-6).toIntArray()
val k3 = listOf(8,8,3,10,8,8,8,5,9,3,4,9,2,7).toIntArray()
fun simulate(input: IntArray): Long {
    var z = 0L
    for(i in input.indices) {
        val w = input[i]
        z = if((z % 26L).toInt() + k2[i] == w)
            z / k1[i]
        else {
            // It MUST match every reduction on order to reduce to 0 in the final,
            // because count of *26 is equal of count of /26 (see k1).
            if(k1[i] == 26)
                return -i.toLong()
            z / k1[i] * 26 + w + k3[i]
        }
    }
    return z
}

/**
 * Helper function which is not used in solution, but only to visualize internal logic
 */
fun explain(input: IntArray): Int {
    var z = 0
    for(i in input.indices) {
        val w = input[i]
        val remainder = z % 26
        val remainderPlusK2 = z % 26 + k2[i]
        var x = if(z % 26 + k2[i] != w) 1 else 0
        z /= k1[i]
        z *= 25*x+1
        z += (w+k3[i])*x
        println("$w[$i] z: $z x: $x accumulator y: ${(w+k3[i])*x}, remainder: $remainder, remainder+k2: $remainderPlusK2")
    }
    return z
}