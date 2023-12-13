package y2023.d9

import java.io.File

val test = """0 3 6 9 12 15
1 3 6 10 15 21
10 13 16 21 30 45
"""

var backwards = false

fun main() {
    println(test.solve())
    println(File("data/2023/day9.txt").readText().solve())

    backwards = true
    println(test.solve())
    println(File("data/2023/day9.txt").readText().solve())
}

fun String.solve(): Int =
    this.parse().map(List<Int>::extrapolate).sum()

fun String.parse(): List<List<Int>> =
    this.lines().filter(String::isNotEmpty).map {line -> line.split(" ").map(String::toInt)}

fun List<Int>.diff(): List<Int> =
        this.windowed(2, 1).map { w -> w[1] - w[0] }

fun List<Int>.diffs(): Sequence<List<Int>> = sequence {
    var diff = this@diffs
    while(diff.any { it != 0 }) {
        yield(diff)
        diff = diff.diff()
    }
}

fun List<Int>.extrapolate(): Int {
    val diffs = if(backwards)
        this.diffs().map { it.first() }.toList().reversed().fold(0) {a, b -> b - a }
    else
        this.diffs().map { it.last() }.sum()
    return diffs
}

