package aoc.area.y2023.d15

import java.io.File

val test = "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7"

fun main() {
    val data = File("data/2023/day15.txt").readText()
    val part1 = data.trim().split(',').map(String::encode).sum()
    println(part1)

    println( data.part2() )
}

fun String.encode(): Int {
    var res = 0
    for(ch in this) {
        res += ch.code
        res *= 17
        res %= 256
    }

    return res
}

fun String.part2(): Int {
    // Exploit LinkedHashMap's property of preserving position
    val boxes = Array(256) {LinkedHashMap<String,Int>()}
    this.trim().split(',').forEach { instruction ->
        if(instruction.endsWith('-')) {
            val label = instruction.substring(0, instruction.length - 1)
            val hash = label.encode()
            boxes[hash].remove(label)
        } else {
            val (label, focus) = instruction.split('=')
            val hash = label.encode()
            boxes[hash][label] = focus.toInt()
        }
    }

    return boxes.flatMapIndexed() { b, box ->
        box.values.mapIndexed { slot, focus ->
            (b + 1) * (slot + 1) * focus
        }
    }.sum()
}