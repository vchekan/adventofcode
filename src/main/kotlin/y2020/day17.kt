package `2020`

import java.io.File

interface HasAround {
    fun around() : List<out HasAround>
}

val cube3d = (14 until 14+26).map {i -> Point3( i % 3 - 1, i/3 % 3 - 1, i/9 % 3 - 1) }
val cube4d = (41 until 41+80).map { i -> Point4( i % 3 - 1, i/3 % 3 - 1, i/9 % 3 - 1, i/27 % 3 - 1) }

data class Point3(val x: Int, val y: Int, val z: Int) : HasAround {
    override fun around(): List<Point3> = cube3d.map{ it + this}
    operator fun plus(b: Point3) : Point3 = Point3(x+b.x, y+b.y, z+b.z)
}

data class Point4(val x: Int, val y: Int, val z: Int, val w: Int) : HasAround {
    override fun around(): List<Point4> = cube4d.map{ it + this}
    operator fun plus(b: Point4) : Point4 = Point4(x+b.x, y+b.y, z+b.z, w + b.w)
}

fun main() {
    val plan = parse {x,y -> Point3(x,y,0) }
    val part1 = calc(plan)
    println("part1: $part1")

    val plan2 = parse {x,y -> Point4(x,y,0, 0) }
    val part2 = calc(plan2)
    println("part2: $part2")
}

fun <T> parse(mapper: (Int, Int) -> T) : Set<T> {
    return File("data/day17.txt").readLines().mapIndexed{y, line ->
        line.withIndex().filter {(_,ch) -> ch == '#' }.map{(x, _) -> mapper(x, y)}
    }.flatten().toMutableSet()
}

fun calc(plan: Set<HasAround>): Int {
    var cube = plan.toMutableSet()
    for(i in 1..6) {
        val plan2 = mutableSetOf<HasAround>()
        cube.forEach { p ->
            val around = p.around().count { cube.contains(it) }
            if(around in 2..3)
                plan2.add(p)

            val activated = p.around().filter { !cube.contains(it) }
                .filter { missing -> missing.around().count{ neighbor -> cube.contains(neighbor)} == 3 }
            plan2.addAll(activated)
        }
        cube = plan2
    }
    return cube.size
}
