package aoc.area

data class Point(val row: Int, val col: Int)
data class PointXY(val x: Int, val y: Int)
typealias Area = List<List<Int>>

private val deltas = listOf(-1, 0, 1)
fun Area.around9(row: Int, col: Int): Sequence<Point> {
    return sequence {
        for(dx in deltas)
            for(dy in deltas) {
                if(dx == 0 && dy == 0)
                    continue
                val point = Point(row + dy, col + dx)
                if(point.row in 0 until this@around9.size && point.col in 0 until this@around9[row].size)
                    yield(point)
            }
    }
}

fun Area.around4(row: Int, col: Int): Sequence<Point> {
    return sequence {
        yield(Point(row-1, col))
        yield(Point(row+1, col))
        yield(Point(row, col-1))
        yield(Point(row, col+1))
    }.filter { it.row in 0 until this.size && it.col in 0 until this[row].size }
}

fun Area.around4(p: Point): Sequence<Point>  = this.around4(p.row, p.col)

fun Area.points(): Sequence<Point> = sequence {
    for(row in 0 until this@points.size)
        for(col in 0 until this@points[0].size)
            yield(Point(row, col))
}

fun Area.around9(p: Point): Sequence<Point> = this.around9(p.row, p.col)

operator fun Area.get(p: Point): Int = this[p.row][p.col]
operator fun List<MutableList<Int>>.set(p: Point, v: Int) {
    this[p.row][p.col] = v
}

fun <T> List<MutableList<T>>.display(): String =
    this.map {
        it.joinToString(" ")
    }.joinToString("\n")
