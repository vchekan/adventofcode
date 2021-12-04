package `2020`

import java.io.File
import java.lang.Integer.min

fun main() {
    val nums = File("data/day9.txt").readLines().map { it.toLong() }.toList()

//    val q = ArrayDeque<Long>()
//    val set = mutableSetOf<Long>()
//    for(i in 0..25) {
//        q.addLast(nums[i])
//        set.add(nums[i])
//    }
//
//    for(i in 25..nums.size) {
//        val n = nums[i]
//
//        var found = false
//        for(nn in set) {
//            val compliment = n - nn
//            if(/*compliment != nn &&*/ set.contains(compliment)) {
//                println("$nn + $compliment = $n")
//                found = true
//                break
//            }
//        }
//
//        if(!found) {
//            println("No sum: $n")
//            return
//        }
//
//        val rm = q.removeFirst()
//        println("Removing $rm")
//        set.remove(rm)
//        set.add(n)
//        q.addLast(n)
//
//    }

    val target = 22406676L
    println(find2(nums, target))
    // pos, offset/len, sum
    var state = mutableListOf<Entry>()
    for(i in 0..nums.size-2) {
        state.add(Entry(i, 1, nums[i] + nums[i+1]))
    }

    while(state.isNotEmpty()) {

        val res = state.find { it.sum == target }
        if(res != null) {
            val n1 = nums.drop(res.pos).take(res.offset+1).minOrNull()!!
            val n2 = nums.drop(res.pos).take(res.offset+1).maxOrNull()!!
            println("part2: range: [${res.pos}..${res.pos+res.offset}] $res. Sum: ${n1+n2}")
            return
        }

        // 2511952
        state.removeIf{
            val right = it.pos + it.offset
            if(right == nums.size-1)
                true
            else {
                it.offset += 1
                it.sum += nums[it.pos + it.offset]
//                println("[${it.pos}/${it.offset}]=${it.sum}")
                it.sum > target
            }
        }
    }
}

fun find2(n: List<Long>, target: Long): Long {
    for(p1 in 0 until n.size-1) {
        var sum = n[p1]
        for(p2 in p1+1 until min(n.size, p1+20)) {
            sum += n[p2]
            if (sum == target) {
                val range = n.drop(p1).take(p2-p1)
                return range.minOrNull()!! + range.maxOrNull()!!
            }
            if(sum > target)
                break
        }
    }

    return -1
}

data class Entry(
    val pos: Int,
    var offset: Int,
    var sum: Long
)