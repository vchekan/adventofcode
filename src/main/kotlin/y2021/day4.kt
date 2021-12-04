package y2021.d4

import java.io.File

fun main() {
    val lines = File("data/2021/d4.txt").readText()
    val calls = lines.split("\n\n")[0].split(',').map(String::toInt)

    val cards = lines.trim().split("\n\n").drop(1).map(String::parseCard)
    var sets = cards.flatMap { card ->
        cardToLineSet(card).map { set -> Pair(set, card) }
    }.toMap()

    var called = mutableSetOf<Int>()
    var firstVictory = false
    calls@ for(call in calls) {
        called.add(call)
        val winnerCards = sets.entries.filter { called.containsAll(it.key) }
        if(winnerCards.isEmpty())
            continue
        for(winnerCard in winnerCards) {
            if(!firstVictory) {
                val unmarked = winnerCard.value.flatten().filterNot { called.contains(it) }.sum()
                println("Part 1: ${unmarked * call}")
                firstVictory = true
            }
            sets = sets.filter { it.value !== winnerCard.value }
            if (sets.isEmpty()) {
                val unmarked = winnerCard.value.flatten().filterNot { called.contains(it) }.sum()
                println("part2: ${unmarked * call}")
                break@calls
            }
        }

    }
}

fun String.parseCard(): List<List<Int>> =
    this.split('\n').map { line ->
        line.trim().split(" +".toRegex()).map { it.toInt() }
    }

fun cardToLineSet(card: List<List<Int>>): List<Set<Int>> =
     card.map { it.toSet() } +
        (card[0].indices).map {idx -> card.map {row -> row[idx] }.toSet() }
