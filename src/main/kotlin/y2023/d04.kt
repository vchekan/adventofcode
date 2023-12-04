package y2023.d4

import java.io.File
import kotlin.math.min

val test = """Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11"""

fun main() {
    val cards = parse(test)
    println( cards.map(Card::wins).sum() )

    println( parse(File("data/2023/day4.txt").readText()).map(Card::wins).sum() )

    println(cards.play())
    println(parse(File("data/2023/day4.txt").readText()).play())
}

data class Card(val id: Int, val winning: Set<Int>, val have: Set<Int>, var instances: Long = 1L)

val rx = Regex("\\d+")
fun parse(lines: String): List<Card> =
    lines.lines().filter(String::isNotEmpty).map { line ->
        val(left, right) = line.split('|').map { rx.findAll(it).map { it.value.toInt() }.toList() }
        Card(left.first(), left.drop(1).toSet(), right.toSet())
    }

fun Card.wins(): Int {
    val matches = this.matches()
    return if(matches == 0) 0 else 1 shl (matches - 1)
}

fun Card.matches(): Int = this.winning.intersect(this.have).count()

fun List<Card>.play(): Long {
    var count = 0L
    for((i, card) in this.withIndex()) {
        count += card.instances
        val wins = card.matches()
        for(j in i+1..min(i  + wins, this.size - 1))
            this[j].instances += card.instances
    }
    return count
}