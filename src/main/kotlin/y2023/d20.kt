package aoc.area.y2023.d20

import java.io.File

val test = """broadcaster -> a, b, c
%a -> b
%b -> c
%c -> inv
&inv -> a
"""

fun main() {
    val data = File("data/2023/day20.txt").readText()
    val(broadcaster, modules) = data.parse()

    val part1 = (0 ..< 1000).fold(0 to 0) { acc, _ ->
        val (h, l) = Engine(modules).process(broadcaster, {})
        acc.first + l to acc.second + h
    }
    println("$part1 ${part1.first * part1.second}")

    val engine2 = Engine(modules)
    val seen = HashSet<String>()
    val starts = HashMap<String,Long>()
    var calculated = 0

    while(calculated < engine2.finalCounters.size) {
        engine2.process(broadcaster) { s ->
            if (seen.add(s))
                starts.putIfAbsent(s, engine2.count)
            else {
                calculated++
                starts[s] = starts[s]!! - engine2.count
            }
        }
    }

    println(starts.values.reduce { acc, c -> acc * c })
}

data class Signal(val from: String, val signal: Boolean, val to: String)

data class Module(val name: String, val isFlipflop: Boolean, val to: List<String>) {
    var state: Boolean = false
    var cstate = HashMap<String,Boolean>()

    fun handle(signal: Signal): Boolean? {
        if(isFlipflop) {
            if(signal.signal)
                return null
            state = !state
            return state
        } else {
            // Conjunction module
            cstate[signal.from] = signal.signal
            state = !cstate.values.all { it }
//            if(state && name == "bt")
//                println("bt: $state")
            return state
        }
    }
}

fun String.parse(): Pair<List<String>, List<Module>> {
    val lines = this.lines().filter { it.isNotEmpty() }
    val broadcast = lines.first { it.startsWith("broadcaster ") }
        .substringAfter("-> ").split(',').map { it.trim() }
    val modules = lines.filter { !it.startsWith("broadcaster") }.map { line ->
        Module(name = line.drop(1).substringBefore(" -> "),
            isFlipflop = line[0] == '%',
            to = line.substringAfter(" -> ").split(',').map { it.trim() }
            )
    }

    return broadcast to modules
}

class Engine(signals: List<Module>) {
    val nameMap = signals.associateBy { it.name }
    var count = 0L
    val finalCounters = this.findCounters().toSet()
    init {
        for(src in nameMap.values) {
            for(dst in src.to) {
                val module = nameMap[dst] ?: continue
                if(!module.isFlipflop) {
                    module.cstate[src.name] = false
                }
            }
        }
    }

    fun process(start: List<String>, onCounter: (String) -> Unit): Pair<Int,Int> {
        count++
        var low = 1 // button = 1
        var high = 0
        val queue = ArrayDeque<Signal>()
        start.forEach { s -> queue.addLast(Signal("broadcaster", false, s)) }
        while(queue.isNotEmpty()) {
            val signal = queue.removeFirst()
            if(signal.signal) high++ else low++

            if(!signal.signal && signal.to in finalCounters)
                onCounter(signal.to)

            val module = nameMap[signal.to] ?: continue
            val signal2 = module.handle(signal) ?: continue
            module.to.forEach { to ->
                queue.addLast(Signal(signal.to, signal2, to))
            }
        }

        return low to high
    }

    fun findCounters(): List<String> {
        val finalInvertor = this.nameMap.entries.single { it.value.to.size == 1 && it.value.to[0] == "rx" }.key
        val counters = this.nameMap.entries.filter { finalInvertor in it.value.to }.map { it.key }
        return counters
    }
}