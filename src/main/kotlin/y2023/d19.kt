package aoc.area.y2023.d19

import java.io.File

val test = """px{a<2006:qkq,m>2090:A,rfg}
pv{a>1716:R,A}
lnx{m>1548:A,A}
rfg{s<537:gd,x>2440:R,A}
qs{s>3448:A,lnx}
qkq{x<1416:A,crn}
crn{x>2662:A,R}
in{s<1351:px,qqz}
qqz{s>2770:qs,m<1801:hdj,R}
gd{a>3333:R,R}
hdj{m>838:A,pv}

{x=787,m=2655,a=1222,s=2876}
{x=1679,m=44,a=2067,s=496}
{x=2036,m=264,a=79,s=2244}
{x=2461,m=1339,a=466,s=291}
{x=2127,m=1623,a=2188,s=1013}
"""

fun main() {
    val data = File("data/2023/day19.txt").readText()
    val (wfs, parts) = data.parse()
    val engine = Engine(wfs)
    val accepted = parts.filter { engine.eval(it) == "A" }
    val part1 = accepted.sumOf { it.values.sum() }
    println(part1)

    val ranges = "xmas".associateWith { IntRange(1, 4000) }
    val part2 = engine.match(ranges, engine.nameMap["in"]!!)
    println(part2)
}

data class Workflow(val name: String, val conditions: List<Condition>, val default: String)
data class Condition(val component: Char, val cmp: Char, val v: Int, val dst: String)
typealias Part = Map<Char,Int>

fun String.parse(): Pair<List<Workflow>, List<Part>> {
    val(workflows, parts) = this.split("\n\n").map { it.trim().lines() }
    return workflows.map { it.parseWf() } to parts.map { it.parsePart() }
}

fun String.parseWf(): Workflow {
    val name = this.substringBefore('{')
    val parts = this.substringAfter('{').trim('}').split(',')

    return Workflow(name = name,
        conditions = parts.take(parts.size-1).map { it.parseCond() },
        default = parts.last())
}

fun String.parseCond(): Condition =
    Condition(
        component = this[0],
        cmp = this[1],
        v = this.drop(2).substringBefore(':').toInt(),
        dst = this.substringAfter(':')
    )

fun String.parsePart(): Part {
    return this.trim('{','}').split(',').associate {component ->
        component.split('=').let { it[0].single() to it[1].toInt() }
    }
}

class Engine(val wfs: List<Workflow>) {
    val nameMap = wfs.associateBy { it.name }

    fun eval(part: Part): String {
        var current = nameMap["in"]!!
        outer@ while (true) {
            for (cond in current.conditions) {
                val arg1 = part[cond.component] ?: continue
                val arg2 = cond.v
                val dst = when {
                    cond.cmp == '<' && arg1 < arg2 ->
                        cond.dst

                    cond.cmp == '>' && arg1 > arg2 ->
                        cond.dst

                    else -> continue
                }

                // matched
                if (dst in listOf("A", "R"))
                    return dst
                // goto dst
                current = nameMap[dst]!!
                continue@outer
            }

            if (current.default in listOf("A", "R"))
                return current.default
            // no match, use default
            current = nameMap[current.default]!!
        }
    }

    fun match(parts: Map<Char, IntRange>, wf: Workflow): Long {
        var res = 0L

        var map = parts
        for (cond in wf.conditions) {
            val range = map[cond.component] ?: continue
            val (match, nomatch) = when (cond.cmp) {
                '<' -> range.splitLessThan(cond.v)
                '>' -> range.splitMoreThan(cond.v)
                else -> throw Exception()
            }

            // Matched and routed: amend map and call with new workflow
            // Matched and rejected: return 0
            // Matched and accepted: return `count * range`
            // Not matched: continue with amended map
            // End of rules rejected: return 0
            // End of rule accepted: return multiply all ranges
            // End of rule routed: the same map call with new workflow
            if (match != null) {
                if (cond.dst == "A") {
                    val map2 = map + (cond.component to match)
                    res += map2.values.map { it.endInclusive.toLong() - it.start + 1}.reduce { acc, i -> acc * i }
                } else if(cond.dst == "R") {
                    //return 0
                } else {
                    // route only with matched range
                    val m1 = match(map + (cond.component to match), nameMap[cond.dst]!!)
                    res += m1
                }
            }

            if (nomatch != null) {
                // continue only with unmatched range
                map = map + (cond.component to nomatch)
            }

        }

        return res + when(wf.default) {
            "R" -> 0
            "A" -> map.map { it.value.endInclusive.toLong() - it.value.start + 1}.reduce {acc, i -> acc * i }
            else -> match(map, this.nameMap[wf.default]!!)
        }
    }
}


// Return matched, not matched pair
fun IntRange.splitLessThan(a: Int) : Pair<IntRange?,IntRange?> =
    //      |---------|
    //  ----<
    //  ----------<
    //  ---------------<
    //  --------------------|
    when {
        a <= this.start -> null to this
        a > this.last -> this to null
        else -> IntRange(this.first, a - 1) to IntRange(a, this.last)
}

fun IntRange.splitMoreThan(a: Int) : Pair<IntRange?,IntRange?> =
//      |---------|
//                >--------------
//           >-------------------
//     >-------------------------
//   >---------------------------
    when {
        a >= this.last -> null to this
        a < this.first -> this to null
        else -> IntRange(a + 1, this.last) to IntRange(this.first, a)
    }