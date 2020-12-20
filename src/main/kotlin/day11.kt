package day11

import java.io.File

fun main() {
    part2()
}

typealias Plan = List<CharArray>


fun part1() {
    var plan = File("data/day11.txt").readLines().map { it.toCharArray() }

    while(true) {
        var changed = false
        var clone = plan.map { it.clone() }
        for (r in clone.indices) {
            for (c in clone[r].indices) {
                if (clone.shouldTake(r, c)) {
                    plan[r][c] = '#'
                    changed = true
                }
            }
        }

        clone = plan.map { it.clone() }
        for (r in clone.indices) {
            for (c in clone[r].indices) {
                if (clone.shouldFree(r, c)) {
                    plan[r][c] = 'L'
                    changed = true
                }
            }
        }

        if(!changed) {
            val s = plan.map { it.count { it == '#' } }.sum()
            println("part1: $s")
            break
        }
    }

//    show(plan)
}

fun part2() {
    var plan = File("data/day11.txt").readLines().map { it.toCharArray() }

    while(true) {
        var changed = false

        var vis = plan.visibilityPart2()
        for (r in plan.indices) {
            for (c in plan[r].indices) {
                if (plan[r][c] == 'L' && vis[r][c] == 0) {
                    plan[r][c] = '#'
                    changed = true
                }
            }
        }

        vis = plan.visibilityPart2()
        for (r in plan.indices) {
            for (c in plan[r].indices) {
                if (plan[r][c] == '#' && vis[r][c] >= 5) {
                    plan[r][c] = 'L'
                    changed = true
                }
            }
        }

        if(!changed) {
            val s = plan.map { it.count { it == '#' } }.sum()
            println("part2: $s")
            break
        }
    }
}

fun walkDiagonalTopLeft(w: Int, h: Int): Sequence<Pair<Int,Int>> {
    return sequence {
        for (rd in 0 until w * h) {
            val delta = rd / h
            val r = rd % h
            val c = (r + delta) % w
            yield(Pair(r, c))
        }
    }
}

fun walkDiagonalTopRight(w: Int, h: Int): Sequence<Pair<Int,Int>> {
    return sequence {
        for (rd in 0 until w * h) {
            val sum = rd / h
            val r = rd % h
            var c =  (sum - r) % w
            while(c < 0)
                c += w
            yield(Pair(r, c))
        }
    }
}


val around = arrayOf(0 to 1, 1 to 0, 1 to 1, 0 to -1, -1 to 0, -1 to -1, 1 to -1, -1 to 1)

fun Plan.shouldTake(r: Int, c: Int): Boolean {
    return this[r][c] == 'L' &&
            around.map { (dx,dy) -> r+dy to c+dx }
            .filter { (r,c) -> r >= 0 && c >= 0 && r < size && c < get(0).size }
            .none { (r,c) -> this[r][c] == '#' }

}

fun Plan.shouldFree(r: Int, c: Int) : Boolean {
    return this[r][c] == '#' &&
        around.map { (dx,dy) -> r+dy to c+dx }
            .filter { (r,c) -> r >= 0 && c >= 0 && r < size && c < get(r).size }
            .count {(r,c) -> this[r][c] == '#'} >= 4

}

fun show(plan: Plan) {
    for(r in plan.indices) {
        for (c in plan[r])
            print(c)
        println()
    }
}

fun Plan.visibilityPart2(): List<List<Int>> {
    var visibility = this.map { it.map { 0 }.toMutableList() }

    // horizontal scan
    for (r in indices) {
        var last: Int = -1
        for (c in get(r).indices) {
            if (this[r][c] == '.')
                continue
            if (last != -1) {
                if(this[r][last] == '#')
                    visibility[r][c] += 1
                if(this[r][c] == '#')
                    visibility[r][last] += 1
            }
            last = c
        }
    }

    // vertical scan
    for(c in this[0].indices) {
        var last: Int = -1
        for(r in indices) {
            if (this[r][c] != '.') {
                if (last != -1) {
                    if(this[last][c] == '#')
                        visibility[r][c] += 1
                    if(this[r][c] == '#')
                        visibility[last][c] += 1
                }
                last = r
            }
        }
    }

    // top-left diagonal scan
    var last = Pair(-1, -1)
    for ((r, c) in walkDiagonalTopLeft(get(0).size, size)) {
        if (r == 0 || c == 0)
            last = Pair(-1, -1)

        if (this[r][c] != '.') {
            if (last != Pair(-1, -1)) {
                if(this[last.first][last.second] == '#')
                    visibility[r][c] += 1
                if(this[r][c] == '#')
                    visibility[last.first][last.second] += 1
            }
            last = Pair(r, c)
        }
    }

    // top-right diagonal
    last = Pair(-1,-1)
    for((r,c) in walkDiagonalTopRight(get(0).size, size)) {
        if (r == 0 || c == this[0].size-1)
            last = Pair(-1, -1)

        if(this[r][c] != '.') {
            if (last != Pair(-1, -1)) {
                if(this[last.first][last.second] == '#')
                    visibility[r][c] += 1
                if(this[r][c] == '#')
                    visibility[last.first][last.second] += 1
            }
            last = Pair(r,c)
        }
    }

    return visibility
}