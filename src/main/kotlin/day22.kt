package day22

import java.io.File
import kotlin.system.measureNanoTime
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@ExperimentalTime
fun main() {
    part2()
}

fun part1() {
    val(p1Str, p2Str) = File("data/day22.txt").readText().split("\n\n").map { it.trim() }
    val p1 = p1Str.lines().drop(1).map { it.toInt() }
    val p2 = p2Str.lines().drop(1).map { it.toInt() }

    val q1 = ArrayDeque<Int>()
    val q2 = ArrayDeque<Int>()

    q1.addAll(p1)
    q2.addAll(p2)

    while(q1.isNotEmpty() && q2.isNotEmpty()) {
        val c1 = q1.removeFirst()
        val c2 = q2.removeFirst()

        if(c1 > c2) {
            q1.addLast(c1)
            q1.addLast(c2)
        } else {
            q2.addLast(c2)
            q2.addLast(c1)
        }

//        println("${q1.size}:${q2.size}")
    }

    println(q1.size)
    println(q2.size)

    val q = (if(q1.isNotEmpty())
        q1
    else
        q2
            ).toList()

    val part1 = q.reversed().withIndex().map { (it.index+1) * it.value }.sum()
    println("part1: $part1")
}

@ExperimentalTime
fun part2() {
    val(p1Str, p2Str) = File("data/day22.txt").readText().split("\n\n").map { it.trim() }
    val p1 = p1Str.lines().drop(1).map { it.toInt() }
    val p2 = p2Str.lines().drop(1).map { it.toInt() }

    val q1 = ArrayDeque<Int>()
    val q2 = ArrayDeque<Int>()
    q1.addAll(p1)
    q2.addAll(p2)

    var p1win : Boolean
    val time = measureNanoTime { p1win = player1wins(q1, q2) }
    val q = if(p1win) q1 else q2
    val part2 = q.reversed().withIndex().map { (it.index+1) * it.value }.sum()

    println("== Post-game results ==")
    println("Player 1 deck: ${q1.joinToString(", ")}")
    println("Player 2 deck: ${q2.joinToString(", ")}")
    // 8411 low
    // 9833 low
    println("part2: $part2")
    println("Time: ${time.toDuration(DurationUnit.NANOSECONDS)}")
}

var count = 0

fun player1wins(q1: ArrayDeque<Int>, q2: ArrayDeque<Int>) : Boolean {

    var states1 = mutableSetOf<String>()
    var states2 = mutableSetOf<String>()

    var round = 0
    while(q1.isNotEmpty() && q2.isNotEmpty()) {
        round += 1
        count += 1
//        if(count % 10000 == 0)
//            println("count: $count")
//        println("-- Round $round --")
//        println("Player 1's deck: ${q1.joinToString(", ")}")
//        println("Player 2's deck: ${q2.joinToString(", ")}")

        val state1 = q1.joinToString()
        val state2 = q2.joinToString()

        if(!states1.add(state1) || !states2.add(state2))
            return true

        val card1 = q1.removeFirst()
        val card2 = q2.removeFirst()
//        println("Player 1 plays: $card1")
//        println("Player 2 plays: $card2")

        if(q1.size < card1 || q2.size < card2) {
            if(card1 > card2) {
                q1.addLast(card1)
                q1.addLast(card2)
//                println("Player 1 wins round $round")
            } else {
                q2.addLast(card2)
                q2.addLast(card1)
//                println("Player 2 wins round $round")
            }
            continue
        }

        val q1copy = ArrayDeque(q1.take(card1))
        val q2copy = ArrayDeque(q2.take(card2))
        if(player1wins(q1copy, q2copy)) {
            q1.addLast(card1)
            q1.addLast(card2)
//            println("Player 1 wins round $round")
        } else {
            q2.addLast(card2)
            q2.addLast(card1)
//            println("Player 2 wins round $round")
        }
    }

    return q2.isEmpty()
}
