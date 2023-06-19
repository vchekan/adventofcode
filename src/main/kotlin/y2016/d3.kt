package aoc.area.y2016

import java.io.File

data class Triangle(val a: Int, val b: Int, val c: Int) {
    companion object {
        val rx = Regex(""" *(\d+) +(\d+) +(\d+)""")
        fun parse(line: String): Triangle {
            val (a, b, c) = rx.matchEntire(line)?.destructured ?: throw Exception("Failed to parse line: '$line'")
            return Triangle(a.toInt(), b.toInt(), c.toInt())
        }
    }

    fun valid(): Boolean = a + b > c && b + c > a && c + a > b
}

fun main() {
    part1()
    part2()
}

fun part1() {
    val triangles = File("data/2016/day3.txt").readLines().filter(String::isNotEmpty).map(Triangle::parse)
    println(triangles.count(Triangle::valid))
}

fun part2() {
    val triangles = File("data/2016/day3.txt").readLines().filter(String::isNotEmpty).map(Triangle::parse)
        .chunked(3).flatMap { chunk ->
            val (t1, t2, t3) = chunk
            listOf(
                Triangle(t1.a, t2.a, t3.a),
                Triangle(t1.b, t2.b, t3.b),
                Triangle(t1.c, t2.c, t3.c)
            )
        }
    println(triangles.count(Triangle::valid))
}