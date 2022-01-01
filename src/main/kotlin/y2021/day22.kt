package y2021.d22

import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
fun main() {
    val cuboids = File("data/2021/d22.txt").readLines().parse()

    val part1 = embeddedVoids(cuboids.filter { it.cube.x1 in -50..50 })
    println("Part 1: $part1")
    assert(part1 == 583636L)

    val (part2, time2) = measureTimedValue {  embeddedVoids(cuboids) }
    println("Part 2: $part2 time: $time2")
    assert(part2 == 1294137045134837)
}

data class Cube(val x1: Int, val x2: Int, val y1: Int, val y2: Int, val z1: Int, val z2: Int, val voids: List<Cube> = emptyList()) {
    fun volume(): Long = (x2 - x1 + 1).toLong() * (y2-y1+1) * (z2-z1+1) - voids.fold(0L) {acc, c -> acc + c.volume() }
    fun applyVoid(v: Cube): Cube {
        val intersect = intersection(v) ?: return this
        return copy(voids = voids.map { it.applyVoid(intersect) } + intersect)
    }
    fun hasIntersection(other: Cube): Boolean =
        !(
            other.x2 < x1 || other.x1 > x2 ||
            other.y2 < y1 || other.y1 > y2 ||
            other.z2 < z1 || other.z1 > z2
        )
    fun intersection(other: Cube): Cube? = when {
            !hasIntersection(other) -> null
            else -> Cube(
                max(x1, other.x1), min(x2, other.x2),
                max(y1, other.y1), min(y2, other.y2),
                max(z1, other.z1), min(z2, other.z2)
            )
    }
}
data class Cuboid(val type: Boolean, val cube: Cube) {
    fun volume(): Long = when (type) {
        true -> cube.volume()
        false -> 0L
    }
}

fun List<String>.parse() = this.map {
    val(onOff, x1, x2, y1, y2, z1, z2) = Regex("((?:on)|(?:off)) x=(-?\\d+)..(-?\\d+),y=(-?\\d+)..(-?\\d+),z=(-?\\d+)..(-?\\d+)")
        .find(it)!!.destructured
    Cuboid(onOff == "on", Cube(x1.toInt(), x2.toInt(), y1.toInt(), y2.toInt(), z1.toInt(), z2.toInt()))
}

fun embeddedVoids(cuboids: List<Cuboid>): Long {
    return cuboids.mapIndexed { i, c1 ->
        cuboids.subList(i+1, cuboids.size).fold(c1) { acc, c2 ->
            acc.copy(cube = acc.cube.applyVoid(c2.cube))
        }
    }.fold(0L) {acc, cuboid -> acc + cuboid.volume() }
}