package aoc.area.y2023.d22

import java.io.File

val test = """1,0,1~1,2,1
0,0,2~2,0,2
0,2,3~2,2,3
0,0,4~0,2,4
2,0,5~2,2,5
0,1,6~2,1,6
1,1,8~1,1,9
"""

fun main() {
    val data = File("data/2023/day22.txt").readText()
    val cubes = data.parse()

    val supportedBy = supportedBy(cubes)
    val canNotDisintegrate = supportedBy.filter { it.value.size == 1 }.values.distinct().count()
    val canDisintegrate = cubes.size - canNotDisintegrate

    println("part 1: $canDisintegrate")
    println("part 2: ${part2(cubes.compact())}")
}

data class Cube(val id:Int, val p1: Point, val p2: Point) {
    val area: Pair<Point2, Point2> get() = p1.toPoint2() to p2.toPoint2()
    fun moveTo(floor:Int): Cube = when(this.p1.z) {
        floor -> this
        else -> this.copy(p1 = p1.copy(z = floor), p2 = p2.copy(z = floor + (p2.z - p1.z)))
    }
}
data class Point(val x: Int, val y: Int, val z: Int) {
    fun toPoint2() = Point2(x, y)
}
data class Point2(val x: Int, val y: Int)

fun supportsOther(cubes: List<Cube>): Map<Cube, List<Cube>> {
    val compact = cubes.compact()
    val levelBottomsMap = compact.groupBy { it.p1.z }
    val supports = compact.map { cube ->
        val top = levelBottomsMap[cube.p2.z + 1] ?: return@map cube to emptyList<Cube>()
        cube to top.filter { it.area.intersects(cube.area) }
    }.toMap()
    return supports
}

fun supportedBy(cubes: List<Cube>): Map<Cube, List<Cube>> {
    val compact = cubes.compact()
    val levelTopsMap = compact.groupBy { it.p2.z }
    val supports = compact.map { cube ->
        val bottom = levelTopsMap[cube.p1.z - 1] ?: return@map cube to emptyList<Cube>()
        cube to bottom.filter { it.area.intersects(cube.area) }
    }.toMap()
    return supports
}

fun part2(cubes: List<Cube>): Int {
    val supportsOther = supportsOther(cubes)
    val supportedBy = supportedBy(cubes)
    val supportedByInit = supportedBy.entries.sortedBy { it.key.id }.map { it.value.size }.toIntArray()
    val supportedByCount = IntArray(supportedByInit.size)
    var disintegrated = 0
    for(cube in cubes) {
        supportedByInit.copyInto(supportedByCount)
        var front = listOf(cube)
        while(front.isNotEmpty()) {
            front = front.flatMap { base ->
                val hanging = supportsOther[base] ?: emptyList()
                hanging.mapNotNull { h ->
                    supportedByCount[h.id]--
                    when (supportedByCount[h.id]) {
                        0 -> { disintegrated++ ; h}
                        -1 -> throw Exception()
                        else -> null
                    }
                }
            }
        }
    }

    return disintegrated
}

fun String.parse(): List<Cube> =
    this.lines().filter { it.isNotEmpty() }
        .mapIndexed {i, line ->
            val parts = Regex("\\d+").findAll(line).map { it.value.toInt() }.toList()
            val p1 = Point(parts[0], parts[1], parts[2])
            val p2 = Point(parts[3], parts[4], parts[5])
            Cube(i, p1, p2)
        }

fun Cube.findFinalLevel(topMap: Map<Int,List<Cube>>): Int {
    if(this.p1.z == 1)
        return 1
    return ((p1.z - 1 downTo 1).firstOrNull { level ->
        val floor = topMap[level] ?: return@firstOrNull false
        floor.count { it.area.intersects(this.area)} > 0
    } ?: 0) + 1
}

fun List<Cube>.compact(): List<Cube> {
    val topSorted = this.sortedBy { it.p2.z }
    val compactedTopMap = mutableMapOf<Int,MutableList<Cube>>()
    val compacted = topSorted.map { cube ->
        val level2 = cube.findFinalLevel(compactedTopMap)
        val cube2 = cube.moveTo(level2)
        compactedTopMap.computeIfAbsent(cube2.p2.z) { ArrayList()}.add(cube2)
        cube2
    }
    return compacted
}

/**
 *   xxxx
 *   xxxx
 *   xxxx
 *
 */
fun Pair<Point2,Point2>.intersects(a: Pair<Point2,Point2>): Boolean =
    // Assuming x and y are ordered in p1, p2.
    // The idea is to inverse condition of rects which do not intersect: those which are
    // above, on the right, below or on the lft of `this` rectangle.
    !(a.first.y > second.y ||
            a.first.x > second.x ||
            a.second.y < first.y ||
            a.second.x < first.x
            )
