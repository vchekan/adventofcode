package aoc.area.y2016

import java.io.File

fun main() {
    val lines = File("data/2016/day12.txt").readLines()
    val prog = lines.map { it.split(" ") }
    val part1 = Computer(prog).run()
    println(part1)

    val part2 = Computer(prog).also { it["c"] = 1 }.run()
    println(part2)
}

class Computer(val prog: List<List<String>>) {
    val regs = IntArray(4)
    var ip = 0

    fun run(): Int {
        while(ip in prog.indices) {
            when(prog[ip][0]) {
                "cpy" -> this[arg2] = this[arg1]
                "inc" -> this[arg1] = this[arg1] + 1
                "dec" -> this[arg1] = this[arg1] - 1
                "jnz" -> {
                    if(this[arg1] != 0) {
                        ip += this[arg2]
                        continue
                    }
                }
            }
            ip++
        }

        return regs[0]
    }

    operator fun get(arg: String): Int = when(arg[0]) {
        in 'a'..'d' -> this.regs[arg[0] - 'a']
        else -> arg.toInt()
    }

    operator fun set(arg: String, v: Int) = when(arg[0]) {
        in 'a'..'d' -> this.regs[arg[0] - 'a'] = v
        else -> throw Exception("Unknown register: $arg")
    }

    val arg1: String get() = prog[ip][1]
    val arg2: String get() = prog[ip][2]
}