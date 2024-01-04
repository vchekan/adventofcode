package aoc.area.y2023.d12

import java.io.File

val test = """???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1
"""

fun main() {
    val data = File("data/2023/day12.txt").readText()
    val part1 = data.parse().map {
        match(it.first, it.second, 0, 0, 0, mutableMapOf(), "")
    }.sum()
    println(part1)

    val map = data.parse().map { it.expand() }
    map.map { match(it.first, it.second, 0, 0, 0, mutableMapOf(), "") }.sum().let(::println)

}

fun match(p: String, nums: List<Int>, pPtrInit: Int, nPtr: Int, count: Long, cache: MutableMap<Pair<Int,Int>,Long>, backtrace: String): Long {
    cache[pPtrInit to nPtr]?.let { return it }

    var pPtr = pPtrInit
    // matched
    if(pPtr !in p.indices && nPtr !in nums.indices) {
        verify(p, backtrace, nums)
        return count + 1
    }
    // not all nums matched
    if(pPtr !in p.indices && nPtr < nums.size)
        return 0
    // not all patterns matched
    if(nPtr !in nums.indices)
        if(p.subSequence(pPtr, p.length).contains('#'))
            return 0
        else {
            verify(p, backtrace, nums)
            return count + 1    // remaining is '.' or '?' so it's a match
        }

    val num = nums[nPtr]
    // skip '.'
    while(pPtr in p.indices && p[pPtr] == '.')
        pPtr++
    if(pPtr !in p.indices)
        return 0

    val res = if(canFit(p, pPtr, num))
        match(p, nums, pPtr + num + 1, nPtr + 1, count, cache, "$backtrace,$num@$pPtr") +
                if(p[pPtr] == '?')
                    // If wildcard, can additionally try to skip it (interpret as '.')
                    match(p, nums, pPtr + 1, nPtr, count, cache, backtrace)
                else
                    0
    else
        if(p[pPtr] == '#')
            // can't fit, but is at '#', so can't skip either
            0
        else
            // can't fit in current position, try the next one
            match(p, nums, pPtr + 1, nPtr, count, cache, backtrace)
    cache[pPtrInit to nPtr] = res
    return res
}



fun canFit(pattern: String, ptr: Int, n: Int): Boolean =
    (ptr + n <= pattern.length) &&                              // fit by the length
    !(ptr - 1 in pattern.indices && pattern[ptr-1] == '#') &&   // can't be right after '#'
    (ptr ..< ptr + n).none { pattern[it] == '.' } &&         // does not have '.' in the middle
    !(ptr + n in pattern.indices && pattern[ptr + n] == '#')    // can't have '#' next to it

fun String.parse(): List<Pair<String,List<Int>>> =
    this.lines().filter(String::isNotEmpty).map { line ->
        line.split(' ').let {
            val pattern = it[0]//.trim('.')
            val nums = it[1].split(',').map(String::toInt)
            pattern to nums
        }
    }

fun Pair<String, List<Int>>.expand(): Pair<String, List<Int>> =
    (1..5).map { this.first }.joinToString("?") to
            (1..5).flatMap { this.second }

fun verify(s: String, starts: String, nums: List<Int>) {
    val ss = CharArray(s.length) {'.'}
    starts.split(',').filter(String::isNotEmpty).map { it.trim().substringAfter('@').toInt() }
        .zip(nums)
        .forEach { (pos, len) ->
            (pos..< pos + len).forEach { ss[it] = '#' }
        }

    s.toCharArray().zip(ss).forEachIndexed { index, pair ->
        val (c, cc) = pair
        when {
            c == '.' && cc == '.' -> {}
            c == '?' && cc == '.' -> {}
            c == '?' && cc == '#' -> {}
            c == '#' && cc == '#' -> {}
            else ->
                throw Exception("\nPattern: $s $nums\n '$c' <- '$cc'\n$starts   \n$s    \n${ss.concatToString()}   ${nums.joinToString()}\n${" ".repeat(index)}^")
        }
    }
}