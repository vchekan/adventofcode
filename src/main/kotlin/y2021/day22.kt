package y2021.d22

import y2021.d3.toInt
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

data class Cube(val x1: Int, val x2: Int, val y1: Int, val y2: Int, val z1: Int, val z2: Int, val voids: List<Cube> = emptyList()) {
    //companion object { val Nil =  Cube(0,0,0,0,0,0) }
    fun volume(): Long {
        val v =  (x2 - x1 + 1).toLong() * (y2-y1+1) * (z2-z1+1) - //voids.fold(0L) {acc, cube -> acc + cube.volume() }
            voids.fold(0L) {acc, c -> acc + c.volume() }

//        var doubleCount = 0L
//        for(i in 0 until voids.size -1) {
//            for(j in i+1 until voids.size) {
//                val intersection = voids[i].intersection(voids[j])
//                doubleCount += intersection?.volume() ?: 0
//            }
//        }

        return v //+ doubleCount
    }
    fun rawVolume(): Long = (x2 - x1 + 1).toLong() * (y2-y1+1) * (z2-z1+1)
    fun doVoid(v: Cube): Cube {
        val intersect = intersection(v) ?: return this
        return copy(voids = voids.map { it.doVoid(v) } + intersect)
    }
    fun isPart1(): Boolean = x1 in -50..50 && x2 in -50..50 && y1 in -50..50 && y1 in -50..50 && z1 in -50..50 && z2 in -50..50
    fun hasIntersection(other: Cube): Boolean =
        (other.x1 in x1..x2 || other.x2 in x1..x2) &&
        (other.y1 in y1..y2 || other.y2 in y1..y2) &&
        (other.z1 in z1..z2 || other.z2 in z1..z2)
    fun isEnclosedIn(other: Cube) = x1 in other.x1..other.x2 && x2 in other.x1..other.x2 &&
            y1 in other.y1..other.y2 && y2 in other.y1..other.y2 &&
            z1 in other.z1..other.z2 && z2 in other.z1..other.z2
    fun intersection(other: Cube): Cube? = when {
            !hasIntersection(other) -> null
            else -> Cube(
                max(x1, other.x1), min(x2, other.x2),
                max(y1, other.y1), min(y2, other.y2),
                max(z1, other.z1), min(z2, other.z2)
            )
    }
}
data class Cuboid(val type: CubeType, val cube: Cube) {
    fun volume(): Long = when (type) {
        CubeType.Void -> 0L
        else -> cube.volume()
    }
}

enum class CubeType {
    Fill,
    DoubleCount,
    Void
}


@OptIn(ExperimentalTime::class)
fun main() {
    test()
//    println(embeddedVoids(File("data/2021/d22.3.txt").readLines().parse()))
    //bruteForce(File("data/2021/d22.2.txt").readLines().parse())
    //containmentStats()
    return
    
    val cuboids = File("data/2021/d22.txt").readLines().parse()

    val (on, off) = cuboids.partition { it.type == CubeType.Fill }
    println("On commands: ${on.size}, off commands: ${off.size}")
    println("On volume: ${on.sumOf{it.cube.volume()}}, off volume: ${off.sumOf{it.cube.volume()}}")
    println("Overlaping partially: ${overlapingPartiallyCount(cuboids)}/${cuboids.size}")

    val max = File("data/2021/d22.2.txt").readLines().parse()
        .flatMap { listOf(it.cube.x1, it.cube.x2, it.cube.y1, it.cube.y2, it.cube.z1, it.cube.z2)
    }.maxOrNull()
    println("max value: $max")
    val min = File("data/2021/d22.2.txt").readLines().parse()
        .flatMap { listOf(it.cube.x1, it.cube.x2, it.cube.y1, it.cube.y2, it.cube.z1, it.cube.z2)
        }.minOrNull()
    println("min value: $min")
    val maxDelta = File("data/2021/d22.2.txt").readLines().parse()
        .flatMap { listOf(it.cube.x1 - it.cube.x2, it.cube.y1 - it.cube.y2, it.cube.z1 - it.cube.z2)
        }.map { it.absoluteValue }.maxOrNull()
    println("max delta: $maxDelta")


    val totalSum = cuboids
        //.filter { it.cube.isPart1() }
        .filter { it.type == CubeType.Fill }
        .sumOf { it.cube.volume() }
    // 11560839384435931
    //  2758514936282235
    println("total sum: $totalSum")

    // 5772382522132673
    // 5176872436879385
    // 2758514936282235

    //=2758514936282235
    // 1057350025204340
    // 6936339741598500
    // 3223162487147049
    //  840997747344773
    // 6283284003717
    val cuboids2 = File("data/2021/d22.2.txt").readLines().parse()
    println( measureTimedValue { solve(cuboids2) })
}

fun List<String>.parse() = this.map {
    val(onOff, x1, x2, y1, y2, z1, z2) = Regex("((?:on)|(?:off)) x=(-?\\d+)..(-?\\d+),y=(-?\\d+)..(-?\\d+),z=(-?\\d+)..(-?\\d+)")
        .find(it)!!.destructured
    val on = when(onOff) {
        "on" -> CubeType.Fill
        "off" -> CubeType.Void
        else -> throw Exception()
    }
    Cuboid(on, Cube(x1.toInt(), x2.toInt(), y1.toInt(), y2.toInt(), z1.toInt(), z2.toInt()))
}

fun overlapingPartiallyCount(cuboids: List<Cuboid>): Int {
    var count = 0
    for(i in 0 until cuboids.size - 1) {
        for(j in i+1 until cuboids.size) {
            if(cuboids[i].cube.hasIntersection(cuboids[j].cube) && ! cuboids[i].cube.isEnclosedIn(cuboids[j].cube))
                count++
        }
    }
    return count
}

fun solve(initCuboids: List<Cuboid>): Long {
    var volume = 0L
//    val doubleCount = mutableListOf<Cube>()
//    val voids = mutableListOf<Cube>()
    val list = mutableListOf<Cuboid>()

    for(i in initCuboids.indices) {
        val c = initCuboids[i]
        var correction = 0L
        val newCubes = list.mapNotNull { prev ->
            val intersect = prev.cube.intersection(c.cube) ?: return@mapNotNull null
            if(prev.type == CubeType.Fill && c.type == CubeType.Fill) {
                // Cube intersects Cube
                correction -= intersect.volume()
                Cuboid(CubeType.DoubleCount, intersect)
//            } else if(prev.type == CubeType.Fill && c.type == CubeType.Void) {
//                // Void intersects Cube
//                correction -= intersect.volume()
//                Cuboid(CubeType.Void, intersect)
            } else if(prev.type == CubeType.Void && c.type == CubeType.Fill) {
                // Cube intersects Void. No new region in result but additional volume is created.
                correction += intersect.volume()
                null
            } else if(prev.type == CubeType.DoubleCount && c.type == CubeType.Fill) {
                // We've compensated Double Count twice, let's undo one of the compensations
                correction += intersect.volume()
                null
//            } else if(prev.type == CubeType.DoubleCount && c.type == CubeType.Void) {
//                // Double Count intersects Void
//                // We need to negate double count, which means to *increase* the intersection
//                correction += intersect.volume()
//                Cuboid(CubeType.Void, intersect)
            } else {
                null
            }
        }
        val selfVolume = if(c.type == CubeType.Fill) {
            list.add(c)
            volume += c.cube.volume()
            c.cube.volume()
        } else if(c.type == CubeType.Void) {
            0
        } else {
            throw Exception()
        }

        list.addAll(newCubes)

        volume += correction
        println("$selfVolume + $correction = ${selfVolume + correction} => $volume")
    }

    return volume
}

fun bruteForce(cuboids: List<Cuboid>): Long {
    fun minmax(a: (Cuboid) -> Int): Pair<Int,Int> = cuboids.minOf(a) to cuboids.maxOf(a)
    val x1range = minmax { it.cube.x1 }
    val x2range = minmax { it.cube.x2 }
    val y1range = minmax { it.cube.y1 }
    val y2range = minmax { it.cube.y2 }
    val z1range = minmax { it.cube.z1 }
    val z2range = minmax { it.cube.z2 }
    val xrange = x1range.first..x2range.second
    val yrange = y1range.first..y2range.second
    val zrange = z1range.first..z2range.second

    val rowSize = xrange.last -xrange.first + 1
    val a = BooleanArray(rowSize)

    var count = 0L
    for(z in zrange) {
        for(y in yrange) {
            for(cuboid in cuboids.filter { z in it.cube.z1..it.cube.z2 && y in it.cube.y1..it.cube.y2 }) {
//                for(i in a.indices) a[i] = false
//                for(x in cuboid.cube.x1..cuboid.cube.x2) {
//                    a[x - xrange.first] = when (cuboid.type) {
//                        CubeType.Fill -> true
//                        CubeType.Void -> false
//                        else -> throw Exception()
//                    }
//                }
                count += a.fold(0L) {acc, i -> acc + i.toInt() }
            }
        }
    }

    return count
}

fun containmentStats() {
    val cuboids = File("data/2021/d22.2.txt").readLines().parse()
    var containedCount = 0
    for(i in 0 until cuboids.size-1) {
        for(j in i+1 until cuboids.size) {
            if(cuboids[i].cube.isEnclosedIn(cuboids[j].cube))
                containedCount++
        }
    }

    println("contained: $containedCount")
}

fun test() {
    val cuboids = listOf(
//        Cuboid(CubeType.Fill, Cube(1,1,2,2,0,0)),
//        Cuboid(CubeType.Fill, Cube(2,2,2,2,0,0)),
//        Cuboid(CubeType.Fill, Cube(2,2,2,2,0,0)),

//        Cuboid(CubeType.Void, Cube(3, 3, 3, 3, 0, 0)),
//        Cuboid(CubeType.Fill, Cube(1, 3, 1, 3, 0, 0)),
//        Cuboid(CubeType.Fill, Cube(2, 4, 2, 4, 0, 0)),
//        Cuboid(CubeType.Fill, Cube(3, 5, 3, 5, 0, 0)),

//        Cuboid(CubeType.Fill, Cube(1, 1, 2, 3, 0, 0)),
//        Cuboid(CubeType.Fill, Cube(1, 2, 2, 2, 0, 0)),
//        Cuboid(CubeType.Fill, Cube(1, 1, 1, 2, 0, 0)),
//        Cuboid(CubeType.Fill, Cube(3, 3, 1, 3, 0, 0)),

        //Cuboid(CubeType.Fill, Cube(1, 10, 1, 10, 0, 0)),

        Cuboid(CubeType.Fill, Cube(1, 4, 1, 4, 0, 0)),
        Cuboid(CubeType.Fill, Cube(3, 6, 2, 2, 0, 0)),
        Cuboid(CubeType.Void, Cube(4, 4, -1, 2, 0, 0)),
        Cuboid(CubeType.Void, Cube(3, 4, 1, 2, 0, 0)),
    )

    println(embeddedVoids(cuboids))
}

fun embeddedVoids(cuboids: List<Cuboid>): Long {
    val voided = cuboids.mapIndexed { i, c1 ->
        cuboids.subList(i+1, cuboids.size).fold(c1) {acc, c2 ->
            acc.copy(cube = acc.cube.doVoid(c2.cube))
        }

//        val withVoid = cuboids.subList(i+1, cuboids.size).mapNotNull { c2 ->
//            if(c1.type == CubeType.Void && c2.type == CubeType.Void)
//                return@mapNotNull null
////            c1.cube.intersection(c2.cube)
//            c1.copy(cube = c1.cube.doVoid(c2.cube))
//        }
//
//        c1.copy(cube = withVoid)
////        voids
    }

//    println(voided[0].volume())
    val v = voided.fold(0L) {acc, cuboid -> acc + cuboid.volume() }
    return v
}