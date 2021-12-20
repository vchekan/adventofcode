package y2021.d18

import java.io.File
import kotlin.math.max

sealed interface SNum {
    var parent: SPair?
    fun isLeftChild(): Boolean = parent!!.left === this
    fun root(): SPair = generateSequence(this) { it.parent }.last() as SPair
    operator fun plus(b: SNum): SNum {
        this.reduce()
        b.reduce()
        val pair = SPair(this, b, null)
        this.parent = pair
        b.parent = pair
        pair.reduce()
        //println(">${pair.render(noColor = false)}")
        return pair
    }
}
data class SDigit(var n: Int, override var parent: SPair?): SNum {
    override fun toString(): String {
        return n.toString()
    }
}
data class SPair(var left: SNum, var right: SNum, override var parent: SPair?): SNum {
    override fun toString(): String {
        return "($left, $right)"
    }
}

fun main() {
    var lines = File("data/2021/d18.txt").readLines()
    val part1 = lines
        .map(String::toSNum)
        .reduce(SNum::plus)
        .magnitude()
    println("Part 1: $part1")

    var max = Int.MIN_VALUE
    for(i in 0 until lines.size-1) {
        for(j in i+1 until lines.size-1) {
            var nums = lines.map(String::toSNum)
            val n1 = (nums[i] + nums[j]).magnitude()
            nums = lines.map(String::toSNum)
            val n2 = (nums[j] + nums[i]).magnitude()
            val n = max(n1, n2)
            if(n > max)
                max = n
        }
    }
    // 4577: low
    println("Part 2: $max")
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
                a.parent = pair
                b.parent = pair
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
    when(this) {
        is SDigit -> {}
        is SPair -> {
            if(depth == 4) {
                this.findFirstLeft()?.let{ it.n += (left as SDigit).n }
                this.findFirstRight()?.let { it.n += (right as SDigit).n }
                when(isLeftChild()) {
                    true -> parent!!.left = SDigit(0, parent)
                    false -> parent!!.right = SDigit(0, parent)
                }
                return true
            } else {
                if(left.explode(depth + 1)) {
                    return true
                }

                return right.explode(depth + 1)
            }
        }
    }

    return false
}

fun SPair.findFirstRight(): SDigit? {
    var node: SPair = this
    while(true) {
        val isRightChild = (node.parent ?: return null).right === node
        node = node.parent!!
        if(!isRightChild)
            break
    }

    var firstRight = node.right
    while(firstRight is SPair)
        firstRight = firstRight.left

    return firstRight as SDigit
}

fun SPair.findFirstLeft(): SDigit? {
    var node: SPair = this
    while(true) {
        val isLeftChild = (node.parent ?: return null).left === node
        node = node.parent!!
        if(!isLeftChild)
            break
    }

    var firstLeft = node.left
    while(firstLeft is SPair)
        firstLeft = firstLeft.right

    return firstLeft as SDigit
}

fun SNum.split(): Boolean {
    when(this) {
        is SPair -> {
            if(left.split())
                return true
            return right.split()
        }
        is SDigit -> {
            if(this.n <= 9)
                return false
            val pair = SPair(SDigit(n / 2, null), SDigit(n - n / 2, null), parent)
            pair.left.parent = pair
            pair.right.parent = pair
            if(isLeftChild())
                parent!!.left = pair
            else
                parent!!.right = pair

            return true
        }
    }

    return false
}

fun SNum.reduce() {
    while(true) {
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
//            "$n"
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
