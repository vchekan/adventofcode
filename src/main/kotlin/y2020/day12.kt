package `2020`

import java.io.File
import java.lang.Exception
import kotlin.math.absoluteValue

fun main() {
    part2()
}

data class State(val x: Int, val y: Int, val dir: Int)
data class State2(val x: Int, val y: Int, val dir: Int, val wx: Int, val wy: Int)
val dirs = "ENWS"

fun State.move(cmd: String) : State {
    val num = cmd.substring(1).toInt()
    return when(cmd[0]) {
        'N' -> this.copy(y = this.y + num)
        'S' -> this.copy(y = this.y - num)
        'E' -> this.copy(x = this.x + num)
        'W' -> this.copy(x = this.x - num)
        'L' -> this.copy(dir = (this.dir + num) % 360 )
        'R' -> this.copy(dir = (this.dir - num + 360) % 360)
        'F' -> {
            val cmd2 = dirs[this.dir/90] + cmd.substring(1)
            this.move(cmd2)
        }
        else -> throw Exception("Unknown command")
    }
}

fun rotate(x: Int, y: Int, angle: Int) : Pair<Int,Int> {
    return when(angle) {
        0 -> Pair(x, y)
        90 -> Pair(-y, x)
        180 -> Pair(-x, -y)
        270 -> Pair(y, -x)
        else -> throw Exception("Can't handle angle $angle")
    }
}

fun State2.move(cmd: String): State2 {
    val num = cmd.substring(1).toInt()
    println(">>$this $cmd")
    return when(cmd[0]) {
        'N' -> this.copy(wy = this.wy + num)
        'S' -> this.copy(wy = this.wy - num)
        'E' -> this.copy(wx = this.wx + num)
        'W' -> this.copy(wx = this.wx - num)
        'L' -> {
            val vec = rotate(wx, wy, num)
            this.copy(wx = vec.first, wy = vec.second)
        }
        'R' -> {
            val vec = rotate(wx, wy, (360 - num))
            this.copy(wx = vec.first, wy = vec.second)
        }
        'F' -> this.copy(x = x + wx * num, y = y + wy * num)
        else -> throw Exception("Unknown command")
    }

}

fun part1() {
    // 9535
    val final = File("data/day12.txt").readLines()
        .fold(State(0,0, 0)) { state, l ->
            state.move(l)
        }

    println(final)
    println("> ${final.x.absoluteValue + final.y.absoluteValue}")
}

fun part2() {
    val final = File("data/day12.txt").readLines()
        .fold(State2(0,0, 0, 10, 1)) { state, l ->
            state.move(l)
        }
    println(final)
    println("> ${final.x.absoluteValue + final.y.absoluteValue}")
}