package `2020`

import java.io.File

fun main() {
    val lines = File("data/day8.txt").readLines()

    var program = lines.map{ line ->
        val (op, arg) = line.split(' ')
        var num = arg.substring(1).toInt()
        if(arg[0] == '-')
            num = -num
        Cmd(op, num)
    }.toList()

//    `2020`.run(program)
    for(i in 0..program.size) {
        val original = program[i].op
        if(program[i].op == "jmp")
            program[i].op = "nop"
        else if (program[i].op == "nop")
            program[i].op = "jmp"
        if(run(program))
            return
        program[i].op = original
    }

}

fun run(program: List<Cmd>): Boolean {
    var acc = 0
    var ip = 0
    val visited = mutableSetOf<Int>()
    while(true) {
        if(!visited.add(ip)) {
//            println("Loop detected. ACC: $acc")
            return false
        }

        if(ip == program.size) {
            println("ACC: $acc")
            return true
        }

        val cmd = program[ip]
        when (cmd.op) {
            "acc" -> acc += cmd.num
            "jmp" -> {
                ip += cmd.num
                continue
            }
            "nop" -> {}
        }
        ip += 1
    }

}

data class Cmd(
    var op: String,
    val num: Int
)