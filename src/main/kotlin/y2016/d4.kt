package aoc.area.y2016

import java.io.File

data class Room(val room: String, val id: Int, val checksum: String) {
    companion object {
        val rx = Regex("""(.+)-(\d+)\[(.+)]""")
        fun parse(line: String): Room {
            val (room, id, checksum) = rx.matchEntire(line)?.destructured ?: throw Exception()
            return Room(room, id.toInt(), checksum)
        }
    }

    fun verify(): Boolean {
        val freqency = room.filter { it.isLetter() }.groupingBy { it }.eachCount().entries
            .sortedWith(compareByDescending<Map.Entry<Char, Int>> { it.value }.thenBy { it.key })
            .map { it.key }
        return (0 until 5).all { freqency[it] == checksum[it] }
    }

    fun decode(): String =
        String(room.map { ch ->
            when(ch) {
                '-' -> ' '
                else -> Char((ch - 'a' + id) % ('z' - 'a' + 1) + 'a'.code)
            }
        }.toCharArray())
}

fun main() {
    val rooms = File("data/2016/day4.txt").readLines().filter(String::isNotEmpty).map(Room::parse)
    val part1 = rooms.filter { it.verify() }.sumOf { it.id }
    println(part1)

    val part2 = rooms.filter { it.verify() }.single { it.decode() == "northpole object storage" }.id
    println(part2)
}