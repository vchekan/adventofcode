package day19

import java.io.File

fun main() {
    val (rulesStr, data) = File("data/day19.txt").readText().split("\n\n")
    val rules = rulesStr.lines().map { line ->
        val(n, rule) = line.split(':')
        Pair(n, rule.trim())
    }.toMap()

    val r0 = rules["0"]!!
    val rx = Regex(buildRx(r0, rules))
    println(rx)
    val count = data.lines().count { line ->
        val r = rx.find(line)
        val m = rx.matches(line)
        m
    }
    println("part1: $count")

    val rx42 = buildRx("42", rules)
    val rx31 = buildRx("31", rules)
    // You can do it matching 42+ and 31+ and then checking that the number of appearances of group 42 is bigger than
    // the number of appearances of group 31. This way you have the same number of appearances of 42 and 31 and some
    // more 42 at the beginning that you can attribute to rule 8.
//    val rx11 = (1..10).map { "(?:" + "(?:$rx42)".repeat(it) + "(?:$rx31)".repeat(it) + ")" }.joinToString("|")
    val rxPart2 = Regex("((?:$rx42)+)((?:$rx31)+)")
    val count2 = data.lines().count { line ->
        val rr = rxPart2.find(line)!!.groups
        val m = rxPart2.matches(line)
        m
    }
    // 299
    println("part2: $count2")

}

fun buildRx(r: String, rules: Map<String,String>) : String {
    val rx = when {
        r.startsWith('"') -> r.substring(1, 2)
        r.contains('|') -> {
            val (r1,r2) = r.split('|')
            val rr1 = buildRx(r1.trim(), rules)
            val rr2 = buildRx(r2.trim(), rules)
            "(?:(?:$rr1)|(?:$rr2))"
        }
        else -> {
            r.split(' ').map { buildRx(rules[it.trim()]!!, rules) }.joinToString("")
        }
    }
    return rx
}
