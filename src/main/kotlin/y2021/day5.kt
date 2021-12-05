package y2021.d5

import java.io.File

data class Point(val x: Int, val y: Int)
data class Line(val p1: Point, val p2: Point)

fun Line.toPoints(): Sequence<Point> {
    val deltaY = (p2.y - p1.y).coerceIn(-1, 1)
    val deltaX = (p2.x - p1.x).coerceIn(-1, 1)
    return sequence {
        var point = p1
        while(true) {
            yield(point)
            if(point == p2)
                break
            point = Point(point.x + deltaX, point.y + deltaY)
        }
    }
}

fun String.toLine(): Line {
    val(x1, y1, x2, y2) = Regex("(\\d+),(\\d+) -> (\\d+),(\\d+)").find(this)!!.destructured
    return Line(Point(x1.toInt(), y1.toInt()), Point(x2.toInt(), y2.toInt()))
}


fun main() {
    val lines = File("data/2021/d5.txt").readLines().map(String::toLine)

    val c1 = lines.filter { it.p1.x == it.p2.x || it.p1.y == it.p2.y }
        .flatMap { it.toPoints() }
        .groupingBy { it }.eachCount().filter { it.value >= 2 }
        .size
    assert(c1 == 6397)
    println("Part 1: $c1")

    val c2 = lines
        .flatMap { it.toPoints() }
        .groupingBy { it }.eachCount().filter { it.value >= 2 }
        .size
    println("Part 2: $c2")
    assert(c2 == 22335)
}