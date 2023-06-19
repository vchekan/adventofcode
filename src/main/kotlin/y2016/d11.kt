package aoc.area.y2016


import y2021.d3.toInt
import kotlin.concurrent.timerTask
import kotlin.system.measureTimeMillis

const val linesTest = """The first floor contains a hydrogen-compatible microchip and a lithium-compatible microchip.
The second floor contains a hydrogen generator.
The third floor contains a lithium generator.
The fourth floor contains nothing relevant."""


const val lines = """The first floor contains a strontium generator, a strontium-compatible microchip, a plutonium generator, and a plutonium-compatible microchip.
The second floor contains a thulium generator, a ruthenium generator, a ruthenium-compatible microchip, a curium generator, and a curium-compatible microchip.
The third floor contains a thulium-compatible microchip.
The fourth floor contains nothing relevant.
"""

fun main() {
    val test1 = Walk(parseLines(linesTest)).travel()
    println("> $test1")
    assert(test1 == 11)

    val ms = measureTimeMillis {
        val floors = parseLines(lines)
        val part1 = Walk(floors).travel()
        println(part1)
        assert(part1 == 37)

        nameTable.addAll(listOf("elerium", "dilithium"))
//    nameTable.sort()
        val floors2 = floors +
                Element(false, nameTable.indexOf("elerium"), 0) +
                Element(true, nameTable.indexOf("elerium"), 0) +
                Element(false, nameTable.indexOf("dilithium"), 0) +
                Element(true, nameTable.indexOf("dilithium"), 0)

        val part2 = Walk(floors2).travel()
        println("part2: $part2")
    }

    println("Solved in ${ms}ms")
}

val rxChip = Regex("([a-z]+)-compatible microchip")
val genRx = Regex("([a-z]+) generator")
fun parseLines(data: String): Set<Element> {
    val set = LinkedHashSet<String>()
    val elements = data.lines().flatMapIndexed { floor, line ->
        val chips = rxChip.findAll(line).map { it.groups[1]!!.value }.toList()
        val gens = genRx.findAll(line).map { it.groups[1]!!.value }.toList()
        set.addAll(chips + gens)
        (chips.map { false to it } + gens.map { true to it }).map { (isGen, name) ->
            Triple(isGen, name, floor)
        }
    }.toSet()

    nameTable.clear()
    nameTable.addAll(set.sorted())
    return elements.map { (isGen, name, floor) ->  Element(isGen, nameTable.indexOf(name), floor) }.toSet()
}


val nameTable = mutableListOf<String>()

data class Element(val isGen: Boolean, val name: Int, val floor: Int)
data class State(val elements: Set<Element>, val floor: Int) {
    var steps: Int = -1

    fun display(): String =
        (3 downTo 0).map { floor ->
            val items = elements.filter { it.floor == floor }
            val line = ".  ".repeat(elements.size + 2).toCharArray()
            line[0] = 'F'
            line[1] = '0' + (floor + 1)
            if(floor == this.floor)
                line[3] = 'E'
            items.forEach { e ->
                val pos = (2 + e.name * 2 + if(e.isGen) 0 else 1) * 3
                line[pos] = nameTable[e.name][0].uppercaseChar()
                line[pos+1] = if(e.isGen) 'G' else 'M'
            }
            String(line)
        }.joinToString("\n")

    val _id = id()
    private fun id(): Long {
        // Encode every chip's floor delta with generator's.
        // chips: 7, delta +/-3(6) + floor: 4 =
        //        3 + 2 = 5 bits per chip, 7 chips = 35 bits
        // plus elevator, 4 floors = 2 bits
        // total 37 bits

        val sorted = elements.sortedWith(Comparator<Element> { a, b -> a.name - b.name }
            .thenComparator { a, b -> a.isGen.toInt() - b.isGen.toInt() })
        val deltas = (sorted.indices step 2).map { i ->
            val delta = sorted[i+1].floor -  sorted[i].floor
            delta to sorted[i].floor
        }

        val signature = deltas.sortedWith(Comparator<Pair<Int, Int>> { a, b -> a.second - b.second }
            .thenComparator { a, b -> a.first - b.first })

        var res = 0L
        for((delta, floor) in signature) {
            res = (res.shl(3) or (delta.toLong() + 3)).shl(2) or floor.toLong()
        }
        res = res.shl(2) or floor.toLong()

        return res
    }

    override fun hashCode(): Int {
        return _id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other == null)
            return false
        if(other !is State)
            return false
        return other._id == this._id
    }
}

class Walk(val initFloors: Set<Element>) {
    val itemCount = initFloors.size
    val queue = ArrayDeque<State>()
    val visited = mutableSetOf<State>()

    fun travel(): Int {
        queue.addLast(State(initFloors, 0).apply { steps = 0 })

        while(true) {
            val state = queue.removeFirst()

            if (state.elements.count { it.floor == 3 } == itemCount)
                return state.steps

            val items = state.elements.filter { it.floor == state.floor }
            arrayOf(1, -1).filter { state.floor + it in 0..3 }.forEach { floorDelta ->
                val floorTo = state.floor + floorDelta
                for ((i, item1) in items.withIndex()) {
                    val state1 = state.copy(
                        elements = state.elements - item1 + item1.copy(floor = floorTo),
                        floor = floorTo
                    ).apply { steps = state.steps + 1 }
                    if (state1.isValid(floorTo) && state1.isValid(state.floor) && visited.add(state1)) {
                        queue.addLast(state1)
                    }

                    if(floorDelta > 0) {    // optimization: do not move down 2 items
                        for (i2 in i + 1 until items.size) {
                            val item2 = items[i2]
                            val elements2 = state.elements - item1 - item2 +
                                    item1.copy(floor = floorTo) +
                                    item2.copy(floor = floorTo)
                            val state2 = state.copy(
                                elements = elements2,
                                floor = floorTo
                            ).apply { steps = state.steps + 1 }

                            if (state2.isValid(floorTo) && state2.isValid(state.floor) && visited.add(state2)) {
                                queue.addLast(state2)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun State.isValid(floor: Int): Boolean {
    val f = this.elements.filter { it.floor == floor }
    val hasUnprotectedChips = f.any { chip -> !chip.isGen && f.none { gen -> gen.isGen && gen.name == chip.name} }
    val isInvalid = hasUnprotectedChips && f.any { it.isGen }
    return !isInvalid
}