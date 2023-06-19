package aoc.area.y2016

import java.io.File

fun main() {
        val words = File("data/2016/day7.txt").readLines()
        .map { it.split('[', ']') }
    val part1 = words.count { line ->
        val hasAbba = line.nonBraket().any(String::isABBA)
        val hasHypernetAbba = line.inBraket().any(String::isABBA)
        hasAbba && !hasHypernetAbba
    }
    println(part1)

    val part2 = words.count { line ->
        // build 2-deep prefix tree for "aba"
        val prefixes = Array('z' - 'a' + 1) { Array('z' - 'a' + 1) { false } }
        line.inBraket().forEach { word ->
            word.scanABA().forEach { i ->
                prefixes[word[i] - 'a'][word[i+1] - 'a'] = true
            }
        }

        line.nonBraket().any { word ->
            word.scanABA().any { i -> prefixes[word[i+1] - 'a'][word[i] - 'a'] }
        }
    }

    println(part2)
}

fun String.isABBA(): Boolean =
    (0 until length - 3).any { i ->
        this[i] != this[i + 1] && this[i + 2] == this[i + 1] && this[i + 3] == this[i]
    }

fun String.scanABA(): List<Int> =
    (0 until this.length - 2).filter {
        this[it] == this[it+2] && this[it] != this[it+1]
    }

// When split by "[]", even words are outside of brkets and non even are inside
fun List<String>.nonBraket(): List<String> = (0 until this.size step 2).map { this[it] }
fun List<String>.inBraket(): List<String> = (1 until this.size step 2).map { this[it] }
    