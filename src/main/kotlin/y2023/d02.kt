package y2023

import java.io.File

val test1 = """Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"""

data class Game(val id: Int, val grabs: List<Grab>)
typealias Grab = Map<Char,Int>

fun main() {
    val testGames = parse(test1)
    val validGames = testGames.validGames()
    val part1Test = validGames.sumOf { it.id }
    println(part1Test)

    val part1 = parse(File("data/2023/day2.txt").readText())
        .validGames()
        .sumOf { it.id }
    println(part1)

    val part2Test = testGames.power()
    println(part2Test)

    val part2 = parse(File("data/2023/day2.txt").readText()).power()
    println(part2)

}

fun List<Game>.validGames(): List<Game> =
    this.filter { game ->
        game.grabs.none { grab ->
            grab['r']?.let { it > 12 } ?: false ||
                    grab['g']?.let { it > 13 } ?: false ||
                    grab['b']?.let { it > 14 } ?: false
        }
    }
fun List<Game>.power(): Int =
    this.map(Game::maxCubes).sumOf { it.values.reduce { a, b -> a * b } }

fun Game.maxCubes(): Map<Char,Int> =
    this.grabs.flatMap { it.map { it.key to it.value } }
        .groupBy { it.first }
        .map { it.key to it.value.maxOf { it.second } }.toMap()

val rxGame = Regex("""^Game (\d+): (.+)""")
fun parse(lines: String) : List<Game> =
    lines.lines().filter(String::isNotEmpty).map { line ->
        val (id, grabsStr) = rxGame.find(line)!!.destructured
        val grabs = grabsStr.split("; ").map { grab ->
            grab.split(", ").associate { coloredCubes ->
                val (count, color) = coloredCubes.split(' ')
                color[0] to count.toInt()
            }
        }
        Game(id.toInt(), grabs)
    }

