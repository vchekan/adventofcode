package y2021.d17

import kotlin.math.absoluteValue

// target area: x=144..178, y=-100..-76
data class Pos(val x: Int, val y: Int)
data class Velocity(val x: Int, val y: Int)
val dx = //20..30
    144..178
val dy = //-10..-5
    -100..-76
fun main() {
    var maxYglobal = Int.MIN_VALUE
    var vx = 1
    var vy = 1
    var hits = mutableSetOf<Velocity>()
    while(true) {
        var maxY =Int.MIN_VALUE
        val res = simulate(Velocity(vx ,vy), Pos(0,0))
            .takeWhile {
                if(it.y > maxY)
                    maxY = it.y
                it.y >= dy.first
            }
            //.firstOrNull { it.x in dx && it.y in dy }
            .lastOrNull()

        if(res != null && res.x in dx && res.y in dy) {
            hits.add(Velocity(vx, vy))
            if(maxY > maxYglobal)
                maxYglobal = maxY
            println("$res: $maxY/$maxYglobal, velocity: ($vx, $vy), hits: $hits")
        }

        if(res != null && res.x < dx.first) { // undershot
            vx++
        } else if(res != null && res.x > dx.last) {    // overshot
            vx--
        } else { // in target range
            vy++
        }
        if(vy> 1000)
            break
    }

    // 67

    // direct hits
    for(vx1 in 0..dx.last) {
        for(vy1 in dy.first..dy.first.absoluteValue) {
            if(vx1 ==9 && vy1 == 0)
                println()
            val res = simulate(Velocity(vx1 ,vy1), Pos(0,0))
                .takeWhile {
                    it.y >= dy.first
                }
                .firstOrNull {
                    it.x in dx && it.y in dy
                }
            if(res != null && res.x in dx && res.y in dy) {
                hits.add(Velocity(vx1, vy1))
                println("direct: ${hits.size} $($vx1, $vy1)")
            }
        }
    }

        println("total hist: ${hits.size}")

        //
        val instanceSet = """23,-10  25,-9   27,-5   29,-6   22,-6   21,-7   9,0     27,-7   24,-5
25,-7   26,-6   25,-5   6,8     11,-2   20,-5   29,-10  6,3     28,-7
8,0     30,-6   29,-8   20,-10  6,7     6,4     6,1     14,-4   21,-6
26,-10  7,-1    7,7     8,-1    21,-9   6,2     20,-7   30,-10  14,-3
20,-8   13,-2   7,3     28,-8   29,-9   15,-3   22,-5   26,-8   25,-8
25,-6   15,-4   9,-2    15,-2   12,-2   28,-9   12,-3   24,-6   23,-7
25,-10  7,8     11,-3   26,-7   7,1     23,-9   6,0     22,-10  27,-6
8,1     22,-8   13,-4   7,6     28,-6   11,-4   12,-4   26,-9   7,4
24,-10  23,-8   30,-8   7,0     9,-1    10,-1   26,-5   22,-9   6,5
7,5     23,-6   28,-10  10,-2   11,-1   20,-9   14,-2   29,-7   13,-3
23,-5   24,-8   27,-9   30,-7   28,-5   21,-10  7,9     6,6     21,-5
27,-10  7,2     30,-9   21,-8   22,-7   24,-9   20,-6   6,9     29,-5
8,-2    27,-8   30,-5   24,-7""".replace("\n", "").split(" ")
                    .map { it.trim() }.filterNot { it.isNullOrBlank() }.toSet()
        val s2 = hits.map { "${it.x},${it.y}" }.toSet()
        val misses = instanceSet - s2
        println(misses)
}

fun simulate(initV: Velocity, initPos: Pos): Sequence<Pos> {
    var v = initV
    var pos = initPos
    return sequence {
        while(true) {
            pos = Pos(pos.x + v.x, pos.y + v.y)
            yield(pos)
            v = Velocity(v.x.dec().coerceAtLeast(0), v.y-1)
        }
    }
}