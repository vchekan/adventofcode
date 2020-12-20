package day16

import java.io.File

fun main() {
    var data = File("data/day16.txt").readText()
    scan(data)
}

fun scan(data: String) {
    val (rulesStr, ticket, nearbyTix) = data.split("\n\n").map(String::trim)

    val rules = rulesStr.split("\n").map {rule ->
        Rule(Regex("([\\w ]+): (\\d+)-(\\d+) or (\\d+)-(\\d+)").find(rule)?.groupValues!!)
    }.toMutableList()

    val tix = nearbyTix.split('\n').drop(1).map { it.split(',').map(String::toInt) }
    val invalid = tix.flatMap { it }.filter { rules.none{ r -> r.valid(it) } }.sum()
    println("part1: $invalid")

    val myTicket = ticket.split('\n').drop(1)[0].split(',').map { it.toInt() }

    val valid = tix.filter { ticket -> ticket.all {field -> rules.any{ r -> r.valid(field) } }}
    //println(valid.count())
    val valuesInField = valid.map{ it.withIndex()}.flatten().groupBy { it.index }.map { it.value.map { it.value } }
        .toMutableList().withIndex().toMutableList()

    var result2 = 1L
    for(_i in 0..50) {
        val found = valuesInField.find { vals ->
            val foundRules = rules.filter { rule -> vals.value.all { rule.valid(it) } }
            if (foundRules.size == 1) {
                println(">>>${foundRules[0].name}: ${vals.index} my val: ${myTicket[vals.index]}")
                if(foundRules[0].name.startsWith("departure"))
                    result2 *= myTicket[vals.index]
                rules.remove(foundRules[0])
                true
            } else {
//                println("${vals.index} latched on ${foundRules.size} rules")
                false
            }
        }
        if(found != null) {
            valuesInField.removeIf{it.index == found.index}
        }
    }

    println("part2: $result2")
}

fun part1() {

}

data class Rule(val name: String, val r1: Int, val r2: Int, val r3: Int, val r4: Int) {
    constructor(vals: List<String>) : this(vals[1], vals[2].toInt(), vals[3].toInt(), vals[4].toInt(), vals[5].toInt())
    fun valid(n: Int) : Boolean {return n in r1..r2 || n in r3..r4}
}