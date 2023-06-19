package aoc.area.y2016

import java.io.File

fun main() {
    val lines = File("data/2016/day10.txt").readLines()
    val f = Factory()
    f.run(lines)
}

class Factory {
    val bots = hashMapOf<Int,Bot>()
    val outputBin = hashMapOf<Int,MutableList<Int>>()

    companion object {
        val rxBot = Regex("""bot (\d+) gives low to (.+) (\d+) and high to (.*) (\d+)""")
        val rxValue = Regex("\\d+")
    }

    fun run(lines: List<String>) {
        for(line in lines.filter { it.startsWith("bot") }) {
            // bot 88 gives low to bot/output 51 and high to bot/output 42
            val (id, lowDst, low, highDst, high) = rxBot.matchEntire(line)?.destructured ?: throw Exception("Failed to parse: $line")
            val bot = Bot(id.toInt(), listOf( low.toInt() to (lowDst == "bot"), high.toInt() to (highDst == "bot")))
            if( bots.put(id.toInt(), bot) != null)
                throw Exception("Bot already defined")
        }

        // value 67 goes to bot 187
        for(line in lines.filter { it.startsWith("value") }) {
            val (chip, botId) = rxValue.findAll(line).map { it.value.toInt() }.toList()
            val bot = bots.get(botId) ?: throw Exception("Bot not found: $botId. Line: $line")
            bot.put(chip)
        }

        val part2 = (0..2).map { outputBin[it]!![0] }.fold(1) {acc, chip -> acc * chip}
        println(part2)
    }

    inner class Bot(val id: Int, val routes: List<Pair<Int,Boolean>>, val chips: MutableList<Int> = mutableListOf()) {
        fun put(chip: Int) {
            chips.add(chip)
            if(chips.size == 2) {
                chips.sort()
                pass()
            }
        }

        fun pass() {
            if(chips[0] == 17 && chips[1] == 61)
                println("part 1: $id")
            for( (chip, route) in chips.zip(routes)) {
                val (to, isBot) = route
                when(isBot) {
                    true -> this@Factory.bots[to]!!.put(chip)
                    false -> this@Factory.outputBin.computeIfAbsent(to) { mutableListOf() }.add(chip)
                }
            }
            chips.clear()
        }
    }
}

