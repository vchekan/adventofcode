package y2021.d18

import java.io.File
import kotlin.math.max
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

sealed interface SNum {
    var parent: SPair?
    fun isLeftChild(): Boolean = parent!!.left === this
    fun root(): SPair = generateSequence(this) { it.parent }.last() as SPair
    operator fun plus(b: SNum): SNum {
        this.reduce()
        b.reduce()
        val pair = SPair(this, b, null)
        pair.reduce()
        return pair
    }
}
data class SDigit(var n: Int, override var parent: SPair?): SNum {
    override fun toString(): String = n.toString()
}
data class SPair(var left: SNum, var right: SNum, override var parent: SPair?): SNum {
    init {
        left.parent = this
        right.parent = this
    }
    override fun toString(): String = "($left, $right)"
}

@OptIn(ExperimentalTime::class)
fun main() {
    var lines = File("data/2021/d18.txt").readLines()

    val (part1, time1) = measureTimedValue {
        lines
            .map(String::toSNum)
            .reduce(SNum::plus)
            .magnitude()
    }
    println("Part 1: $part1, time: $time1")
    assert(part1 == 3756)


    val (part2, time2) = measureTimedValue {
        var part2 = Int.MIN_VALUE
        for(i in 0 until lines.size-1) {
            for(j in i+1 until lines.size-1) {
                var nums = lines.map(String::toSNum)
                val n1 = (nums[i] + nums[j]).magnitude()
                nums = lines.map(String::toSNum)
                val n2 = (nums[j] + nums[i]).magnitude()
                val n = max(n1, n2)
                if(n > part2)
                    part2 = n
            }
        }
        part2
    }
    println("Part 2: $part2, time: $time2")
    assert(part2 == 4585)
}

fun String.toSNum(): SNum = Parser(this).parseSNum()
class Parser(private val str: String) {
    var pos = 0

    fun parseSNum(): SNum {
        return when (str[pos]) {
            '[' -> {
                pos++
                val a = parseSNum()
                consume(',')
                val b = parseSNum()
                consume(']')
                val pair = SPair(a, b, null)
                pair
            }
            in '0'..'9' -> {
                var n = 0
                while(str[pos].isDigit())
                    n = n * 10 + (str[pos++] - '0')
                SDigit(n, null)
            }
            else -> throw Exception("Unexpected char '${str[pos]}' at position $pos")
        }
    }

    private fun consume(c: Char) {
        if(str[pos] != c)
            throw Exception("Unexpected char at pos $pos. Got '${str[pos]}' but expected $c")
        pos++
    }
}

fun SNum.explode(depth: Int = 0): Boolean {
    return when(this) {
        is SDigit -> false
        is SPair -> {
            if(depth == 4) {
                this.findFirstLeftOrRight(searchLeft = true)?.let{ it.n += (left as SDigit).n }
                this.findFirstLeftOrRight(searchLeft = false)?.let { it.n += (right as SDigit).n }
                when(isLeftChild()) {
                    true -> parent!!.left = SDigit(0, parent)
                    false -> parent!!.right = SDigit(0, parent)
                }
                true
            } else {
                left.explode(depth + 1) || right.explode(depth + 1)
            }
        }
    }
}

/**
 * Two-phase algorithm. First. walk up, looking for a node where direction of inheritance is changed and switch
 * to the opposite direction sub-branch. Then, descend, looking for the deepest child with the opposite direction
 * to the searched one.
 */
fun SPair.findFirstLeftOrRight(searchLeft: Boolean): SDigit? {
    fun selectSameDirection(n: SPair): SNum = if(searchLeft) n.left else n.right
    fun selectReverseDirection(n: SPair): SNum = if(searchLeft) n.right else n.left
    var directionChangeNode: SPair = this
    while(true) {
        val isParentDirectionChange = selectSameDirection(directionChangeNode.parent ?: return null) !== directionChangeNode
        directionChangeNode = directionChangeNode.parent!!
        if(isParentDirectionChange)
            break
    }

    // Now, when parent turn node is found, descend in *opposite* direction to the requested,
    // to find "the most" left or right node.
    var descendNode = selectSameDirection(directionChangeNode)
    while(true)
        when(descendNode) {
            is SPair -> descendNode = selectReverseDirection(descendNode)
            is SDigit -> return descendNode
        }
}


fun SNum.split(): Boolean {
    return when(this) {
        is SPair -> left.split() || right.split()
        is SDigit -> {
            if(this.n <= 9)
                return false
            val pair = SPair(SDigit(n / 2, null), SDigit(n - n / 2, null), parent)
            if(isLeftChild())
                parent!!.left = pair
            else
                parent!!.right = pair

            true
        }
    }
}

fun SNum.reduce() {
    while(true) {
        println(this.render(noColor = false))
        if(this.explode())
            continue
        if(this.split())
            continue
        break
    }
}

fun SNum.render(level: Int = 0, noColor: Boolean = true): String {
    return when (this) {
        is SDigit -> {
            val (color1, color2) = if(!noColor && level < 6 && n > 9) "\u001B[36m" to "\u001B[0m" else "" to ""
            "$color1$n$color2"
        }
        is SPair -> {
            val (color1, color2) = if (!noColor && level >= 4) "\u001B[31m" to "\u001B[0m" else "" to ""
            "$color1[${this.left.render(level + 1, noColor)},${this.right.render(level + 1, noColor)}]$color2"
        }
    }
}

fun SNum.magnitude(): Int = when(this) {
    is SDigit -> this.n
    is SPair -> 3 * this.left.magnitude() + 2 * this.right.magnitude()
}
