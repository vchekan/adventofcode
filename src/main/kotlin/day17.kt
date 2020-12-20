//package day17
//
//import java.io.File
//
//interface HasAround {
//    fun around() : Sequence<HasAround>
//}
//
//data class Point3(val x: Int, val y: Int, val z: Int): HasAround {
//    override fun around() : Sequence<Point3> {
//        return sequence {
//            for (i in 14 until 14+26) {
//                yield(Point3( x + i % 3 - 1, y + i/3 % 3 - 1, z + i/9 % 3 - 1))
//            }
//        }
//    }
//}
//
//val cube3d = (14 until 14+26).map {i -> Point3( i % 3 - 1, i/3 % 3 - 1, i/9 % 3 - 1) }
//val cube4d = (41 until 41+80).map { i -> Point4( i % 3 - 1, i/3 % 3 - 1, i/9 % 3 - 1, i/27 % 3 - 1) }
//operator fun Point3.plus(b: Point3) : Point3 {return Point3(x+b.x, y+b.y, z+b.y)}
//operator fun Point4.plus(b: Point4) : Point4 { return Point4(x+b.x, y+b.y, z+b.y, w + b.w)}
//
//
//data class Point4(val x: Int, val y: Int, val z: Int, val w: Int) : HasAround {
//    override fun around() : Sequence<Point4> {
//        return sequence {
//            for (i in 41 until 41+80) {
//                yield(Point4( x + i % 3 - 1, y + i/3 % 3 - 1, z + i/9 % 3 - 1, w + i/27 % 3 - 1))
//            }
//        }
//    }
//}
//
//fun main() {
//    val plan = parse {x,y -> Point3(x,y,0)}
//    val part1 = calc(plan)
//    println("part1: $part1")
//
//    val plan2 = parse {x,y -> Point4(x,y,0, 0)}
//    val part2 = calc(plan2)
//    println("part2: $part2")
//}
//
//fun parse(mapper: (Int, Int) -> HasAround) : Set<HasAround> {
//    return File("data/day17.txt").readLines().mapIndexed{y, line ->
//        line.withIndex().filter {(_,ch) -> ch == '#' }.map{(x, _) -> mapper(x, y)}
//    }.flatten().toMutableSet()
//}
//
//fun calc(plan: Set<HasAround>): Int {
//    var cube = plan.toMutableSet()
//    for(i in 1..6) {
//        val plan2 = mutableSetOf<HasAround>()
//        cube.forEach { p ->
//            val around = p.around().count { cube.contains(it) }
//            if(around in 2..3)
//                plan2.add(p)
//
//            val activated = p.around().filter { !cube.contains(it) }
//                .filter { missing -> missing.around().count{ neighbor -> cube.contains(neighbor)} == 3 }
//            activated.forEach(plan2::add)
//        }
//        cube = plan2
//    }
//    return cube.size
//}
